package fr.pjthin.vertx.service;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class Launcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

    private Vertx vertx;

    private ApplicationContext context;

    public static void main(String[] args) throws InterruptedException {
        Launcher l = new Launcher();
        l.start();

        // TODO how to externalize shutdown
        // l.close();
    }

    public void start() {
        LOGGER.info("Starting Application...");
        this.context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
        this.vertx = context.getBean(Vertx.class);
    }

    public void close() throws InterruptedException {
        LOGGER.info("Shuting down Application...");

        // closing context
        LOGGER.info("Shuting down context...");
        ((AbstractApplicationContext) context).close();
        LOGGER.info("Shuting down context...DONE");

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

            // waiting asynchronous thread 20 seconds
            synchronized (done) {
                done.wait(20 * 1000);
            }
            LOGGER.info("Shutting vertx..." + done.getString("result"));
        }
    }
}
