package fr.pjthin.vertx.client;

import fr.pjthin.vertx.client.data.User;
import io.vertx.codegen.annotations.ProxyClose;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ProxyHelper;

import java.util.List;

/**
 * Dao.
 * 
 * @author Pidji
 */
@ProxyGen
public interface UserDao {
    String ADDRESS = "fr.pjthin.service.userdao";

    /**
     * Return a proxy dao witch can be used everywhere on evenbus.
     * 
     * @param vertx
     * @return proxy dao
     */
    static UserDao getProxy(Vertx vertx) {
        return ProxyHelper.createProxy(UserDao.class, vertx, ADDRESS);
    }

    /**
     * Save a new user.
     * 
     * @param newUser
     *            user to save
     * @param complete
     *            handler when save has been done
     */
    void save(User newUser, Handler<AsyncResult<String>> complete);

    void findAll(Handler<AsyncResult<List<User>>> complete);

    /**
     * Close method
     */
    @ProxyClose
    void close();
}
