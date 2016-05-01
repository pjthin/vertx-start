package fr.pjthin.vertx.service.dao;

import fr.pjthin.vertx.service.ConfigurationUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoClient;

public abstract class AbstractDaoSupport extends AbstractVerticle {

    protected MongoClient mongoClient;

    public AbstractDaoSupport(Vertx vertx) {
        vertx.deployVerticle(this);
    }

    @Override
    public void start() throws Exception {
        mongoClient = MongoClient.createShared(vertx,
                ConfigurationUtils.getConfiguration(vertx, ConfigurationUtils.MONGODB), "vertx-services-pool");
    }
}
