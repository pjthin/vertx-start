package fr.pjthin.vertx.consumer;

import fr.pjthin.vertx.client.UserDao;
import fr.pjthin.vertx.client.data.Gender;
import fr.pjthin.vertx.client.data.User;
import io.vertx.core.AsyncResult;
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
                SimpleRandomUserDaoConsummer::handle);
    }

    static void handle(AsyncResult<Vertx> resultHandler) {
        if (resultHandler.succeeded()) {

            Vertx vertx = resultHandler.result();

            // get a proxy everywhere on the evenbus
            UserDao dao = UserDao.getProxy(vertx);

            // save a default user that we will find later
            dao.save(getDefaultUser(), complete -> {
                if (complete.succeeded()) {
                    LOGGER.info("default user saved with id=" + complete.result());
                } else {
                    LOGGER.error(complete.cause().getMessage(), complete.cause());
                }
            });

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

            // look for default user inserted
            dao.findUserByLogin(DEFAULT_LOGIN_USER, complete -> {
                if (complete.succeeded()) {
                    LOGGER.info("Succeeded ! Default user was found !");
                } else {
                    LOGGER.error(complete.cause().getMessage(), complete.cause());
                }
            });

            // after 10s cancel saving random user, show all inserted user, end vertx
            vertx.setTimer(10000, h -> {
                vertx.cancelTimer(periodicId);
                dao.findAll(complete -> {
                    if (complete.succeeded()) {
                        complete.result().stream().map(user -> {
                            // remove user one by one
                                dao.deleteByLogin(user.getLogin(), done -> {
                                    if (done.failed()) {
                                        LOGGER.error(done.cause().getMessage(), done.cause());
                                    }
                                });
                                return user.toString();
                            }).forEach(LOGGER::info);
                    } else {
                        LOGGER.error(complete.cause().getMessage(), complete.cause());
                    }
                    vertx.close();
                });
            });
        } else {
            LOGGER.error(resultHandler.cause().getMessage(), resultHandler.cause());
        }

    }

    private static Random R = new Random();

    private static User generateRandomUser() {
        return new User().setId(Math.abs(R.nextInt())).setGender(R.nextInt() % 2 == 0 ? Gender.MALE : Gender.FEMALE)
                .setLogin("user" + Math.abs(R.nextInt()))
                .setCryptedPasswd(RandomStringUtils.randomAlphanumeric(Math.abs(R.nextInt()) % 13));
    }

    private static final String DEFAULT_LOGIN_USER = "myLogin";

    private static User getDefaultUser() {
        return new User().setId(1).setGender(Gender.MALE).setLogin(DEFAULT_LOGIN_USER)
                .setCryptedPasswd("myCryptedPasswd");
    }
}
