package fr.pjthin.vertx.consumer;

import fr.pjthin.vertx.service.constante.Gender;
import fr.pjthin.vertx.service.dao.UserDao;
import fr.pjthin.vertx.service.data.User;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleRandomUserDaoConsummer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleRandomUserDaoConsummer.class);

    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions().setClustered(true).setClusterManager(new HazelcastClusterManager()),
                resultHandler -> {
                    if (resultHandler.succeeded()) {

                        Vertx vertx = resultHandler.result();
                        // get a proxy everywhere on the evenbus
                UserDao dao = UserDao.getProxy(vertx);

                // every 500ms save a random user
                final long periodicId = vertx.setPeriodic(500, h -> {
                    dao.save(generateRandomUser(), complete -> {
                        if (complete.succeeded()) {
                            LOGGER.info("user saved with id=" + complete.result());
                        } else {
                            LOGGER.error(complete.cause().getMessage(), complete.cause());
                        }
                    });
                });

                // after 10s cancel saving random user, show all inserted user, end vertx
                vertx.setTimer(10000, h -> {
                    vertx.cancelTimer(periodicId);
                    dao.findAll(complete -> {
                        if (complete.succeeded()) {
                            complete.result().stream().map(User::toString).forEach(LOGGER::info);
                        } else {
                            LOGGER.error(complete.cause().getMessage(), complete.cause());
                        }
                        vertx.close();
                    });
                });
            } else {
                LOGGER.error(resultHandler.cause().getMessage(), resultHandler.cause());
            }
        });
    }

    private static Random R = new Random();

    private static User generateRandomUser() {
        return new User().setId(Math.abs(R.nextInt())).setGender(R.nextInt() % 2 == 0 ? Gender.MALE : Gender.FEMALE)
                .setLogin("user" + Math.abs(R.nextInt()))
                .setCryptedPasswd(RandomStringUtils.randomAlphanumeric(Math.abs(R.nextInt()) % 13));
    }
}
