package fr.pjthin.vertx.service.dao;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fr.pjthin.vertx.client.UserDao;
import fr.pjthin.vertx.client.UserDaoVertxEBProxy;
import fr.pjthin.vertx.client.UserDaoVertxProxyHandler;
import fr.pjthin.vertx.client.data.User;
import fr.pjthin.vertx.mongo.MongoClientDataWrapper;
import fr.pjthin.vertx.service.Launcher;
import fr.pjthin.vertx.service.container.DeployServiceProxy;

/**
 * Dao implementation.
 * <p>
 * <b>WARNING!</b> if you modify this class you have to delete manually and re-generate {@link UserDaoVertxEBProxy} and
 * {@link UserDaoVertxProxyHandler} (mvn compile) because the processor would not re-generate it if class already
 * exists.
 * 
 * @author Pidji
 */
@DeployServiceProxy
public class UserDaoImpl extends AbstractVerticle implements UserDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);
    private static final String USER_COLLECTION = "users";

    @Autowired
    private MongoClientDataWrapper<User> mongoClientUser;

    @Override
    public void save(final User newUser, Handler<AsyncResult<String>> complete) {
        LOGGER.debug(String.format("Saving %s", newUser.toJson()));
        mongoClientUser.save(USER_COLLECTION, newUser, complete);
    }

    @Override
    public void findAll(Handler<AsyncResult<List<User>>> complete) {
        LOGGER.debug("findAll(..)");
        mongoClientUser.find(USER_COLLECTION, User.ALL, complete);
    }

    @Override
    public void findUserByLogin(String login, Handler<AsyncResult<User>> complete) {
        LOGGER.debug("findUserByLogin(..)");
        // set fields to null for getting all data
        mongoClientUser.findOne(USER_COLLECTION, new JsonObject().put("login", login), null, complete);
    }

    @Override
    public void deleteByLogin(String login, Handler<AsyncResult<Void>> complete) {
        LOGGER.debug("deleteByLogin(..)");
        mongoClientUser.removeOne(USER_COLLECTION, new JsonObject().put("login", login), complete);
    }

    @Override
    public void close() {
        LOGGER.info("closing...");
    }

}
