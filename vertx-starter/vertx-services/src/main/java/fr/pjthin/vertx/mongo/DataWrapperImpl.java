package fr.pjthin.vertx.mongo;

import io.vertx.core.json.JsonObject;

import java.util.function.Function;

/**
 * Implementation.
 * 
 * @author Pidji
 *
 * @param <T>
 */
class DataWrapperImpl<T> implements DataWrapper<T> {

    private Function<JsonObject, T> dataSupplier;
    private Function<T, JsonObject> jsonSupplier;

    public DataWrapperImpl(Class<T> klass) {
        this.dataSupplier = new ContructorJsonObject<T>(klass);
        this.jsonSupplier = new ToJsonMethod<T>(klass);
    }

    @Override
    public T toData(JsonObject json) {
        return dataSupplier.apply(json);
    }

    @Override
    public JsonObject toJson(T data) {
        return jsonSupplier.apply(data);
    }
}
