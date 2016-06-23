package fr.pjthin.vertx.mongo;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.mongo.WriteOption;

import java.util.List;

/**
 * Wrapper of {@link MongoClient} which use &lt;T> instead of {@link JsonObject} when it's possible.
 * 
 * @author Pidji
 *
 * @param <T>
 *            POJO
 */
public interface MongoClientDataWrapper<T> {

    static <S> MongoClientDataWrapper<S> getInstance(Class<S> klass, MongoClient mongoClient) {
        return new MongoClientDataWrapperImpl<S>(klass, mongoClient);
    }

    MongoClient save(String collection, T document, Handler<AsyncResult<String>> resultHandler);

    MongoClient saveWithOptions(String collection, T document, WriteOption writeOption,
            Handler<AsyncResult<String>> resultHandler);

    MongoClient insert(String collection, T document, Handler<AsyncResult<String>> resultHandler);

    MongoClient insertWithOptions(String collection, T document, WriteOption writeOption,
            Handler<AsyncResult<String>> resultHandler);

    MongoClient update(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<Void>> resultHandler);

    MongoClient updateWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options,
            Handler<AsyncResult<Void>> resultHandler);

    MongoClient replace(String collection, JsonObject query, T replace, Handler<AsyncResult<Void>> resultHandler);

    MongoClient replaceWithOptions(String collection, JsonObject query, T replace, UpdateOptions options,
            Handler<AsyncResult<Void>> resultHandler);

    MongoClient find(String collection, JsonObject query, Handler<AsyncResult<List<T>>> resultHandler);

    MongoClient findBatch(String collection, JsonObject query, Handler<AsyncResult<T>> resultHandler);

    MongoClient findWithOptions(String collection, JsonObject query, FindOptions options,
            Handler<AsyncResult<List<T>>> resultHandler);

    MongoClient findBatchWithOptions(String collection, JsonObject query, FindOptions options,
            Handler<AsyncResult<T>> resultHandler);

    MongoClient findOne(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<T>> resultHandler);

    MongoClient count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler);

    MongoClient remove(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler);

    MongoClient removeWithOptions(String collection, JsonObject query, WriteOption writeOption,
            Handler<AsyncResult<Void>> resultHandler);

    MongoClient removeOne(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler);

    MongoClient removeOneWithOptions(String collection, JsonObject query, WriteOption writeOption,
            Handler<AsyncResult<Void>> resultHandler);

    MongoClient createCollection(String collectionName, Handler<AsyncResult<Void>> resultHandler);

    MongoClient getCollections(Handler<AsyncResult<List<String>>> resultHandler);

    MongoClient dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler);

    MongoClient runCommand(String commandName, JsonObject command, Handler<AsyncResult<T>> resultHandler);

    MongoClient distinct(String collection, String fieldName, String resultClassname,
            Handler<AsyncResult<JsonArray>> resultHandler);

    MongoClient distinctBatch(String collection, String fieldName, String resultClassname,
            Handler<AsyncResult<T>> resultHandler);

    void close();
}
