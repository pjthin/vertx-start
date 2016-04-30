package fr.pjthin.vertx.service.dao;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.pjthin.vertx.service.Main;
import fr.pjthin.vertx.service.data.User;

/**
 * Dao implementation.
 * <p>
 * <b>WARNING!</b> if you modify this class you have to delete manually and re-generate {@link UserDaoVertxEBProxy} and
 * {@link UserDaoVertxProxyHandler} (mvn compile) because the processor would not re-generate it if class already
 * exists.
 * 
 * @author Pidji
 */
public class UserDaoImpl implements UserDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private Vertx vertx;

    public UserDaoImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void save(final User newUser, Handler<AsyncResult<Void>> complete) {
        vertx.executeBlocking(res -> {
            try {
                LOGGER.debug("start save user: " + newUser.toJson());
                // save in DB
                // TODO implement db-save
                Thread.sleep(10000);
                LOGGER.debug("end save user: " + newUser.toJson());
                res.complete();
            } catch (Exception e) {
                res.fail(e);
            }
        }, complete);
    }

    @Override
    public void close() {
        LOGGER.info("closing...");
    }
}
