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
            return
            // static method
            Modifier.isStatic(method.getModifiers())
            // one param
                    && method.getParameterCount() == 1
                    // object Vertx
                    && Vertx.class.equals(method.getParameterTypes()[0])
                    // and return MessageConsumer
                    && MessageConsumer.class.equals(method.getReturnType());
        }
        return false;
    }

}
