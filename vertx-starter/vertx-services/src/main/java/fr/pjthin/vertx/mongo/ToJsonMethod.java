package fr.pjthin.vertx.mongo;

import io.vertx.core.json.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

class ToJsonMethod<T> implements Function<T, JsonObject> {

    private Method method;

    public ToJsonMethod(Class<T> klass) {
        Optional<Method> method = getToJsonMethod(klass);
        this.method = method.orElseThrow(() -> {
            throw new RuntimeException(String
                    .format("Fail to find method type 'JsonObject methodName()' for %s", klass));
        });
    }

    private Optional<Method> getToJsonMethod(Class<T> klass) {
        return Stream.of(klass.getMethods()).filter(method -> {
            return
            // non-static method
                !Modifier.isStatic(method.getModifiers())
                // no parameter
                        && method.getParameterTypes().length == 0
                        // return JsonObject
                        && JsonObject.class.equals(method.getReturnType());
            }).findFirst();
    }

    private JsonObject callMethod(T t) {
        try {
            return (JsonObject) method.invoke(t);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(String.format("Fail to call method %s.%s for %s", method.getDeclaringClass(),
                    method, t), e);
        }
    }

    @Override
    public JsonObject apply(T t) {
        return callMethod(t);
    }

}
