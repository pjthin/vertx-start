package fr.pjthin.vertx.mongo;

import io.vertx.core.json.JsonObject;

/**
 * Wrapper to transform POJO into {@link JsonObject} and vice-vers-ça.
 * 
 * @author Pidji
 *
 * @param <T>
 *            POJO
 */
interface DataWrapper<T> {

    T toData(JsonObject json);

    JsonObject toJson(T data);

}
