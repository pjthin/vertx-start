package fr.pjthin.vertx.service;

import fr.pjthin.vertx.service.container.ServiceContainer;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        LOGGER.info("Starting services...");
        Vertx.clusteredVertx(new VertxOptions().setClustered(true).setClusterManager(new HazelcastClusterManager()),
                resultHandler -> {
                    if (resultHandler.succeeded()) {
                        final Vertx vertx = resultHandler.result();
                        ServiceContainer container = new ServiceContainer("fr.pjthin.vertx.service");
                        vertx.deployVerticle(container, complete -> {
                            if (complete.succeeded()) {
                                LOGGER.info("Services started.");
                            } else {
                                LOGGER.error("Failed starting services: " + complete.cause().getMessage(),
                                        complete.cause());
                            }
                        });
                    } else {
                        LOGGER.error("Failed creating Vertx clustered: " + resultHandler.cause().getMessage(),
                                resultHandler.cause());
                    }
                });
    }

}
