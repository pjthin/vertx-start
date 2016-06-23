package fr.pjthin.vertx.service;

import fr.pjthin.vertx.client.data.User;
import fr.pjthin.vertx.mongo.MongoClientDataWrapper;
import fr.pjthin.vertx.service.container.DeployServicePostProcessor;
import fr.pjthin.vertx.service.container.ListProxyfiedService;
import fr.pjthin.vertx.service.container.ServiceContainer;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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
    public Launcher launcher() {
        return new Launcher();
    }

    @Bean
    public ClusterManager clusterManager() {
        return new HazelcastClusterManager();
    }

    @Bean
    public ServiceContainer serviceContainer() {
        return new ServiceContainer();
    }

    @Bean
    public VertxOptions vertxOptions(ClusterManager clusterManager) {
        // FIXME find another way for loading configuration (only work with eclipse)
        return new VertxOptions(ConfigurationUtils.loadJsonFromClassPath(ConfigurationUtils.CLUSTER_PATH))
                .setClusterManager(clusterManager);
    }

    @Bean(name = "mongodbConfiguration")
    public JsonObject mongodbConfiguration() {
        // FIXME find another way for loading configuration (only work with eclipse)
        return ConfigurationUtils.loadJsonFromClassPath(ConfigurationUtils.MONGODB_PATH);
    }

    /**
     * 
     * @return list of services to deploy (will be filled by postProcessor)
     */
    @Bean
    public ListProxyfiedService toDeployServices() {
        return new ListProxyfiedService();
    }

    @Bean
    public DeployServicePostProcessor serviceDeployerPostProcessor() {
        return new DeployServicePostProcessor();
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
            } catch (InterruptedException e) {
                throw new LauncherException("Time out waiting creation of clustered vertx instance", e);
            }
        }

        if (vertxContainer.handler.succeeded()) {
            return vertxContainer.handler.result();
        } else {
            throw new LauncherException("Failed creating clustered vertx with configuration " + vertxOptions);
        }
    }

    @Bean
    public MongoClient mongoClient(Vertx vertx, JsonObject mongodbConfiguration) {
        return MongoClient.createShared(vertx, mongodbConfiguration, "vertx-services-pool");
    }

    @Bean
    public MongoClientDataWrapper<User> mongoClientUser(MongoClient mongoClient) {
        return MongoClientDataWrapper.getInstance(User.class, mongoClient);
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
