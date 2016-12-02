package fr.pjthin.vertx.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import fr.pjthin.vertx.adapter.DeployServicePostProcessor;
import fr.pjthin.vertx.client.data.User;
import fr.pjthin.vertx.container.core.ServiceContainer;
import fr.pjthin.vertx.mongo.MongoClientDataWrapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

/**
 * Spring configuration.
 * 
 * @author Pidji
 */
@Configuration
@ComponentScan(basePackages = "fr.pjthin.vertx.service")
public class SpringConfiguration {

	// timeout of 20 seconds
	private static final long WAITING_VERTX_TIMEOUT = 20 * 1000;

	@Bean
	public ClusterManager clusterManager() {
		return new HazelcastClusterManager();
	}

	@Bean
	public ServiceContainer serviceContainer(Vertx vertx) {
		return new ServiceContainer(vertx);
	}

	@Bean
	public VertxOptions vertxOptions(ClusterManager clusterManager) {
		// FIXME find another way for loading configuration (only work with
		// eclipse)
		return new VertxOptions(ConfigurationUtils.loadJsonFromClassPath(ConfigurationUtils.CLUSTER_PATH))
				.setClusterManager(clusterManager);
	}

	@Bean(name = "mongodbConfiguration")
	public JsonObject mongodbConfiguration() {
		// FIXME find another way for loading configuration (only work with
		// eclipse)
		return ConfigurationUtils.loadJsonFromClassPath(ConfigurationUtils.MONGODB_PATH);
	}

	@Bean
	public DeployServicePostProcessor serviceDeployerPostProcessor(ServiceContainer container) {
		return new DeployServicePostProcessor(container);
	}

	@Bean
	public Vertx vertx(VertxOptions vertxOptions) {
		// helper to get synchronously the clustered vertx instance.
		final SynchronizedVertxContainer vertxContainer = new SynchronizedVertxContainer();

		// asynchronous creation of clustered vertx
		Vertx.clusteredVertx(vertxOptions, (handler) -> {
			vertxContainer.handler = handler;
			// it's done, taking back handler to waiting thread
			synchronized (vertxContainer) {
				vertxContainer.notifyAll();
			}
		});

		// waiting for deploying clustered vertx
		synchronized (vertxContainer) {
			try {
				vertxContainer.wait(WAITING_VERTX_TIMEOUT);
			}
			catch (InterruptedException e) {
				throw new RuntimeException("Time out waiting creation of clustered vertx instance", e);
			}
		}

		if (vertxContainer.handler.succeeded()) {
			return vertxContainer.handler.result();
		}
		else {
			throw new RuntimeException("Failed creating clustered vertx with configuration " + vertxOptions);
		}
	}

	@Bean
	public MongoClient mongoClient(Vertx vertx, JsonObject mongodbConfiguration) {
		return MongoClient.createShared(vertx, mongodbConfiguration, "vertx-services-pool");
	}

	@Bean
	public MongoClientDataWrapper<User> mongoClientUser(MongoClient mongoClient) {
		// if you want to use reflection
		// return MongoClientDataWrapper.getInstance(mongoClient, User.class);
		// but method already exists so just pass them
		return MongoClientDataWrapper.getInstance(mongoClient, User::toJson, User::new);
	}

	@Bean(initMethod = "start", destroyMethod = "stop")
	public Launcher launcher(Vertx vertx, ServiceContainer container) {
		return new Launcher(vertx, container);
	}

	/**
	 * Helper class to get {@link Vertx} instance synchronously.
	 * 
	 * @author Pidji
	 */
	private static class SynchronizedVertxContainer {
		private AsyncResult<Vertx> handler;
	}
}
