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
                        UserDao dao = UserDao.getProxy(vertx);
                        dao.save(generateRandomUser(), complete -> {
                            if (complete.succeeded()) {
                                LOGGER.info("user saved with id=" + complete.result());
                            } else {
                                LOGGER.error(complete.cause().getMessage(), complete.cause());
                            }
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
