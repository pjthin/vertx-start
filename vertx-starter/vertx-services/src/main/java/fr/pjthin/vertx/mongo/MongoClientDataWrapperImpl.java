package fr.pjthin.vertx.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.mongo.WriteOption;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation.
 * 
 * @author Pidji
 *
 * @param <T>
 *            Class POJO with {@link JsonObject} constructor (like POJO annoted {@link DataObject})
 */
class MongoClientDataWrapperImpl<T> implements MongoClientDataWrapper<T> {

    private MongoClient mongoClient;
    private Function<T, JsonObject> toJson;
    private Function<JsonObject, T> fromJson;

    public MongoClientDataWrapperImpl(MongoClient mongoClient, Function<T, JsonObject> toJson,
            Function<JsonObject, T> fromJson) {
        this.mongoClient = mongoClient;
        this.toJson = toJson;
        this.fromJson = fromJson;
    }

    @Override
    public MongoClient save(String collection, T document, Handler<AsyncResult<String>> resultHandler) {
        return mongoClient.save(collection, toJson.apply(document), resultHandler);
    }

    @Override
    public MongoClient saveWithOptions(String collection, T document, WriteOption writeOption,
            Handler<AsyncResult<String>> resultHandler) {
        return mongoClient.saveWithOptions(collection, toJson.apply(document), writeOption, resultHandler);
    }

    @Override
    public MongoClient insert(String collection, T document, Handler<AsyncResult<String>> resultHandler) {
        return mongoClient.insert(collection, toJson.apply(document), resultHandler);
    }

    @Override
    public MongoClient insertWithOptions(String collection, T document, WriteOption writeOption,
            Handler<AsyncResult<String>> resultHandler) {
        return mongoClient.insertWithOptions(collection, toJson.apply(document), writeOption, resultHandler);
    }

    @Override
    public MongoClient update(String collection, JsonObject query, JsonObject update,
            Handler<AsyncResult<Void>> resultHandler) {
        return mongoClient.update(collection, query, update, resultHandler);
    }

    @Override
    public MongoClient updateWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options,
            Handler<AsyncResult<Void>> resultHandler) {
        return mongoClient.updateWithOptions(collection, query, update, options, resultHandler);
    }

    @Override
    public MongoClient replace(String collection, JsonObject query, T replace, Handler<AsyncResult<Void>> resultHandler) {
        return mongoClient.replace(collection, query, toJson.apply(replace), resultHandler);
    }

    @Override
    public MongoClient replaceWithOptions(String collection, JsonObject query, T replace, UpdateOptions options,
            Handler<AsyncResult<Void>> resultHandler) {
        return mongoClient.replaceWithOptions(collection, query, toJson.apply(replace), options, resultHandler);
    }

    @Override
    public MongoClient find(String collection, JsonObject query, Handler<AsyncResult<List<T>>> resultHandler) {
        return mongoClient.find(
                collection,
                query,
                h -> {
                    if (h.succeeded()) {
                        resultHandler.handle(Future.succeededFuture(h.result().stream().map(fromJson)
                                .collect(Collectors.toList())));
                    } else {
                        resultHandler.handle(Future.failedFuture(h.cause()));
                    }
                });
    }

    @Override
    public MongoClient findBatch(String collection, JsonObject query, Handler<AsyncResult<T>> resultHandler) {
        return mongoClient.findBatch(collection, query, h -> {
            if (h.succeeded()) {
                resultHandler.handle(Future.succeededFuture(fromJson.apply(h.result())));
            } else {
                resultHandler.handle(Future.failedFuture(h.cause()));
            }
        });
    }

    @Override
    public MongoClient findWithOptions(String collection, JsonObject query, FindOptions options,
            Handler<AsyncResult<List<T>>> resultHandler) {
        return mongoClient.findWithOptions(
                collection,
                query,
                options,
                h -> {
                    if (h.succeeded()) {
                        resultHandler.handle(Future.succeededFuture(h.result().stream().map(fromJson)
                                .collect(Collectors.toList())));
                    } else {
                        resultHandler.handle(Future.failedFuture(h.cause()));
                    }
                });
    }

    @Override
    public MongoClient findBatchWithOptions(String collection, JsonObject query, FindOptions options,
            Handler<AsyncResult<T>> resultHandler) {
        return mongoClient.findBatchWithOptions(collection, query, options, h -> {
            if (h.succeeded()) {
                resultHandler.handle(Future.succeededFuture(fromJson.apply(h.result())));
            } else {
                resultHandler.handle(Future.failedFuture(h.cause()));
            }
        });
    }

    @Override
    public MongoClient findOne(String collection, JsonObject query, JsonObject fields,
            Handler<AsyncResult<T>> resultHandler) {
        return mongoClient.findOne(collection, query, fields, h -> {
            if (h.succeeded()) {
                resultHandler.handle(Future.succeededFuture(fromJson.apply(h.result())));
            } else {
                resultHandler.handle(Future.failedFuture(h.cause()));
            }
        });
    }

    @Override
    public MongoClient count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler) {
        return mongoClient.count(collection, query, resultHandler);
    }

    @Override
    public MongoClient remove(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler) {
        return mongoClient.remove(collection, query, resultHandler);
    }

    @Override
    public MongoClient removeWithOptions(String collection, JsonObject query, WriteOption writeOption,
            Handler<AsyncResult<Void>> resultHandler) {
        return mongoClient.removeWithOptions(collection, query, writeOption, resultHandler);
    }

    @Override
    public MongoClient removeOne(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler) {
        return mongoClient.removeOne(collection, query, resultHandler);
    }

    @Override
    public MongoClient removeOneWithOptions(String collection, JsonObject query, WriteOption writeOption,
            Handler<AsyncResult<Void>> resultHandler) {
        return mongoClient.removeOneWithOptions(collection, query, writeOption, resultHandler);
    }

    @Override
    public MongoClient createCollection(String collectionName, Handler<AsyncResult<Void>> resultHandler) {
        return mongoClient.createCollection(collectionName, resultHandler);
    }

    @Override
    public MongoClient getCollections(Handler<AsyncResult<List<String>>> resultHandler) {
        return mongoClient.getCollections(resultHandler);
    }

    @Override
    public MongoClient dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
        return mongoClient.dropCollection(collection, resultHandler);
    }

    @Override
    public MongoClient runCommand(String commandName, JsonObject command, Handler<AsyncResult<T>> resultHandler) {
        return mongoClient.runCommand(commandName, command, h -> {
            if (h.succeeded()) {
                resultHandler.handle(Future.succeededFuture(fromJson.apply(h.result())));
            } else {
                resultHandler.handle(Future.failedFuture(h.cause()));
            }
        });
    }

    @Override
    public MongoClient distinct(String collection, String fieldName, String resultClassname,
            Handler<AsyncResult<JsonArray>> resultHandler) {
        return mongoClient.distinct(collection, fieldName, resultClassname, resultHandler);
    }

    @Override
    public MongoClient distinctBatch(String collection, String fieldName, String resultClassname,
            Handler<AsyncResult<T>> resultHandler) {
        return mongoClient.distinctBatch(collection, fieldName, resultClassname, h -> {
            if (h.succeeded()) {
                resultHandler.handle(Future.succeededFuture(fromJson.apply(h.result())));
            } else {
                resultHandler.handle(Future.failedFuture(h.cause()));
            }
        });
    }

    @Override
    public void close() {
        mongoClient.close();
    }

}