package fr.pjthin.vertx.service.dao;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public abstract class AbstractDaoSupport extends AbstractVerticle {

    protected MongoClient mongoClient;

    public AbstractDaoSupport(Vertx vertx) {
        vertx.deployVerticle(this);
    }

    @Override
    public void start() throws Exception {
        // TODO passing configuration in file : @see
        // http://vertx.io/docs/vertx-mongo-client/java/#_configuring_the_client
        mongoClient = MongoClient.createShared(
                vertx,
                new JsonObject().put("db_name", "vertx-services-db").put("connection_string",
                        "mongodb://localhost:27017"), "vertx-services-pool");
    }
}
