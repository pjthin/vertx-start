package fr.pjthin.vertx.service;

import fr.pjthin.vertx.service.constante.Gender;
import fr.pjthin.vertx.service.container.ServiceContainer;
import fr.pjthin.vertx.service.dao.UserDao;
import fr.pjthin.vertx.service.data.User;
import io.vertx.core.Vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("Starting services...");
        final Vertx vertx = Vertx.vertx();
        ServiceContainer container = new ServiceContainer("fr.pjthin.vertx.service");
        vertx.deployVerticle(container, complete -> {
            if (complete.succeeded()) {
                LOGGER.info("Services started.");
                consumeServices(vertx);
            } else {
                LOGGER.error("Failed starting services: " + complete.cause().getMessage(), complete.cause());
            }
        });
    }

    private static void consumeServices(Vertx vertx) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        UserDao dao = UserDao.getProxy(vertx);
        dao.save(createUser(), done -> {
            if (done.succeeded()) {
                LOGGER.info("Succeed : " + done.result());
            } else {
                LOGGER.error(done.cause().getMessage(), done.cause());
            }
        });
    }

    private static User createUser() {
        return new User().setId(1).setLogin("pjthin").setGender(Gender.MALE).setCryptedPasswd("secret");
    }
}
