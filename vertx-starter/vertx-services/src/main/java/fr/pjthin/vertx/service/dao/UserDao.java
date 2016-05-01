package fr.pjthin.vertx.service.dao;

import fr.pjthin.vertx.service.container.DeployServiceProxy;
import fr.pjthin.vertx.service.container.DeployServiceProxyMethod;
import fr.pjthin.vertx.service.data.User;
import io.vertx.codegen.annotations.ProxyClose;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ProxyHelper;

import java.util.List;

/**
 * Dao.
 * 
 * @author Pidji
 */
@ProxyGen
@DeployServiceProxy
public interface UserDao {
    String USER_DAO_ADDRESS = "fr.pjthin.service.userdao";

    /**
     * Return a proxy dao witch can be used everywhere on evenbus.
     * 
     * @param vertx
     * @return proxy dao
     */
    static UserDao getProxy(Vertx vertx) {
        return ProxyHelper.createProxy(UserDao.class, vertx, USER_DAO_ADDRESS);
    }

    /**
     * Register on evenbus the dao.
     * 
     * @param vertx
     * @return the service consumer
     */
    @DeployServiceProxyMethod
    static MessageConsumer<JsonObject> register(Vertx vertx) {
        return ProxyHelper.registerService(UserDao.class, vertx, new UserDaoImpl(vertx), USER_DAO_ADDRESS);
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
