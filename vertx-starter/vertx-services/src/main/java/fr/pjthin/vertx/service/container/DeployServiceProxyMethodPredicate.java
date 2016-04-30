package fr.pjthin.vertx.service.container;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.google.common.base.Predicate;

public class DeployServiceProxyMethodPredicate implements Predicate<Method> {

    @Override
    public boolean apply(Method method) {
        if (method.isAnnotationPresent(DeployServiceProxyMethod.class)) {
            return Modifier.isStatic(method.getModifiers()) && method.getParameterCount() == 1
                    && Vertx.class.equals(method.getParameters()[0].getType())
                    && MessageConsumer.class.equals(method.getReturnType());
        }
        return false;
    }

}
