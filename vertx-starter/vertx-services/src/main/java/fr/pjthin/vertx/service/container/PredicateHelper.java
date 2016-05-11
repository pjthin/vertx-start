package fr.pjthin.vertx.service.container;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.Vertx;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.reflections.ReflectionUtils;

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

    @SuppressWarnings("unchecked")
    static boolean isInterfaceWithProxyGenAnnotationAndAddressConstante(Class<?> clazz) {
        return clazz.isAnnotationPresent(ProxyGen.class)
                && ReflectionUtils.getAllFields(clazz, PredicateHelper::isFieldConstanteStringNamedAddress).stream()
                        .count() == 1;

    }

    static boolean isConstructorWithVertxArgument(Constructor<?> constructor) {
        int paramterCount = constructor.getParameterCount();
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        return paramterCount == 1 && Vertx.class.equals(parameterTypes[0]);
    }
}
