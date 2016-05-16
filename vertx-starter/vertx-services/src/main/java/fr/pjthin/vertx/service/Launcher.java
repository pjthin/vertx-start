package fr.pjthin.vertx.service;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import fr.pjthin.vertx.service.container.ServiceContainer;

public class Launcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

    @Autowired
    private ServiceContainer serviceContainer;

    @Autowired
    private Vertx vertx;

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
        Launcher launcher = context.getBean(Launcher.class);
        launcher.start();
    }

    @PostConstruct
    public void start() {
        LOGGER.info("Starting services...");
    }

    @PreDestroy
    public void close() throws InterruptedException {
        LOGGER.info("Shuting services...");
        if (vertx != null) {
            LOGGER.info("Shutting vertx...");

            // object synchronized to get result of close operation
            final JsonObject done = new JsonObject();

            vertx.close((handler) -> {
                if (handler.succeeded()) {
                    done.put("result", "DONE");
                } else {
                    done.put("result", "FAILED");
                    LOGGER.error("An error occurred while shuting done...", handler.cause());
                }
                // go back to main thread
                synchronized (done) {
                    done.notifyAll();
                }
            });

            // waiting asynchronous thread 10 seconds
            synchronized (done) {
                done.wait(10 * 1000);
            }
            LOGGER.info("Shutting vertx..." + done.getString("result"));
        }
        if (serviceContainer != null) {
            LOGGER.info("Shutting serviceContainer...");
            serviceContainer.stop();
        }
    }
}
