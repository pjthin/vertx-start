package fr.pjthin.vertx.mongo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import io.vertx.core.json.JsonObject;

class FromJsonConstructor<T> implements Function<JsonObject, T> {

    private Constructor<T> constructor;

    @SuppressWarnings("unchecked")
    public FromJsonConstructor(Class<T> klass) {
        Optional<Constructor<?>> constructor = getJsonConstructor(klass);
        this.constructor = (Constructor<T>) constructor.orElseThrow(() -> 
            new RuntimeException(String.format("Fail to find constructor for %s", klass))
        );
    }

    private Optional<Constructor<?>> getJsonConstructor(Class<T> persistentClass) {
        return Stream.of(persistentClass.getConstructors()).filter(constructor -> {
            Class<?>[] params = constructor.getParameterTypes();
            return params.length == 1 && JsonObject.class.equals(params[0]);
        }).findFirst();
    }

    private T callJsonConstructor(JsonObject json) {
        try {
            return constructor.newInstance(json);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(String.format("Fail to call constructor %s.%s for %s",
                    constructor.getDeclaringClass(), constructor, json), e);
        }
    }

    @Override
    public T apply(JsonObject t) {
        return callJsonConstructor(t);
    }

}
