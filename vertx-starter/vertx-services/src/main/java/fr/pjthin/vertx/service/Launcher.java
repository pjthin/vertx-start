package fr.pjthin.vertx.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import fr.pjthin.vertx.container.core.ServiceContainer;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class Launcher {
	private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

	private Vertx vertx;

	private ServiceContainer container;

	public Launcher(Vertx vertx, ServiceContainer container) {
		this.vertx = vertx;
		this.container = container;
	}

	public static void main(String[] args) throws InterruptedException {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);

		// TODO how to externalize shutdown
		// context.close();
	}

	public void start() {
		LOGGER.info("Starting Application...");
		container.start();
	}

	public void stop() throws InterruptedException {
		LOGGER.info("Shuting down Application...");

		if (vertx != null) {
			LOGGER.info("Shutting vertx...");

			// object synchronized to get result of close operation
			final JsonObject done = new JsonObject();

			vertx.close((handler) -> {
				if (handler.succeeded()) {
					done.put("result", "DONE");
				}
				else {
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
