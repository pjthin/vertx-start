package fr.pjthin.vertx.service.container;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.Vertx;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

import org.springframework.context.ApplicationContext;

public class PredicateHelper {

    static final String ADDRESS_NAME = "ADDRESS";

    static boolean isNotInterface(Class<?> clazz) {
        return !Modifier.isInterface(clazz.getModifiers());
    }

    static boolean isFieldConstanteStringNamedAddress(Field field) {
        int modifier = field.getModifiers();
        return Modifier.isPublic(modifier) && Modifier.isStatic(modifier) && Modifier.isFinal(modifier)
                && String.class.equals(field.getType()) && ADDRESS_NAME.equals(field.getName());
    }

    static boolean isInterfaceWithProxyGenAnnotationAndAddressConstante(Class<?> clazz) {
        return clazz.isAnnotationPresent(ProxyGen.class)
                && Stream.of(clazz.getFields()).filter(PredicateHelper::isFieldConstanteStringNamedAddress).count() == 1;
    }

    static boolean isConstructorWithVertxArgument(Constructor<?> constructor) {
        int paramterCount = constructor.getParameterCount();
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        return paramterCount == 2 && Vertx.class.equals(parameterTypes[0])
                && ApplicationContext.class.equals(parameterTypes[1]);
    }

    static boolean isMethodPublicAndNotStatic(Method method) {
        return Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers());
    }
}
