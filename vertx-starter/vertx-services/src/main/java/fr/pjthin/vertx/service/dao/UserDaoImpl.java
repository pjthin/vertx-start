package fr.pjthin.vertx.service.dao;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.pjthin.vertx.service.Launcher;
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
public class UserDaoImpl extends AbstractDaoSupport implements UserDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);
    private static final String USER_COLLECTION = "users";

    public UserDaoImpl(Vertx vertx) {
        super(vertx);
    }

    @Override
    public void save(final User newUser, Handler<AsyncResult<String>> complete) {
        LOGGER.debug(String.format("Saving %s", newUser.toJson()));
        mongoClient.save(USER_COLLECTION, newUser.toJson(), complete);
    }

    @Override
    public void close() {
        LOGGER.info("closing...");
    }
}
