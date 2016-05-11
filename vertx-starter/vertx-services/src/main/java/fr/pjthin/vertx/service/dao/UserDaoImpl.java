package fr.pjthin.vertx.service.dao;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.pjthin.vertx.client.UserDao;
import fr.pjthin.vertx.client.UserDaoVertxEBProxy;
import fr.pjthin.vertx.client.UserDaoVertxProxyHandler;
import fr.pjthin.vertx.client.data.User;
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
    public void findAll(Handler<AsyncResult<List<User>>> complete) {
        LOGGER.debug("findAll(..)");
        mongoClient.find(
                USER_COLLECTION,
                User.ALL,
                (h) -> {
                    if (h.succeeded()) {
                        complete.handle(Future.succeededFuture(h.result().stream().map((json) -> new User(json))
                                .collect(Collectors.toList())));
                    } else {
                        complete.handle(Future.failedFuture(h.cause()));
                    }
                });
    }

    @Override
    public void close() {
        LOGGER.info("closing...");
    }

}
