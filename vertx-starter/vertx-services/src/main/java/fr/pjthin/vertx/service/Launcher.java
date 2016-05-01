package fr.pjthin.vertx.service;

import fr.pjthin.vertx.service.container.ServiceContainer;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        // TODO extract configuration of manager into a file
        VertxOptions vertxOptions = new VertxOptions(
                ConfigurationUtils.loadJsonFromClassPath(ConfigurationUtils.CLUSTER_PATH))
                .setClusterManager(new HazelcastClusterManager());
        Vertx.clusteredVertx(vertxOptions, resultHandler -> {
            if (resultHandler.succeeded()) {
                startServices(resultHandler.result());
            } else {
                LOGGER.error("Failed creating Vertx clustered: " + resultHandler.cause().getMessage(),
                        resultHandler.cause());
            }
        });
    }

    private static void startServices(final Vertx vertx) {
        LOGGER.info("Starting services...");

        ConfigurationUtils.loadJsonFromClassPathAndPutConfiguration(vertx, ConfigurationUtils.MONGODB_PATH,
                ConfigurationUtils.MONGODB);

        ServiceContainer container = new ServiceContainer("fr.pjthin.vertx.service");
        vertx.deployVerticle(container, complete -> {
            if (complete.succeeded()) {
                LOGGER.info("Services started.");
            } else {
                LOGGER.error("Failed starting services: " + complete.cause().getMessage(), complete.cause());
            }
        });
    }

    private static void loadConfiguration() {
        try {
            Path path = Paths.get(Launcher.class.getResource("/mongodb.json").toURI());
            String json = FileUtils.readFileToString(path.toFile(), "UTF-8");
            JsonObject config = new JsonObject(json);
            config.toString();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
