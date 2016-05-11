package fr.pjthin.vertx.service.container;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ProxyHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceContainer extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceContainer.class);

    private List<MessageConsumer<JsonObject>> deployedServices;
    private String[] packageToScans;

    public ServiceContainer(String... packageToScans) {
        super();
        checkPackage(packageToScans);
        this.packageToScans = packageToScans;
        this.deployedServices = new ArrayList<>();
    }

    private void checkPackage(String[] packageToScans) {
        if (packageToScans == null || packageToScans.length == 0) {
            throw new IllegalArgumentException("packageToScans must be set");
        }
        for (String packageToScan : packageToScans) {
            if (!Pattern.matches("([a-z]+\\.)*[a-z]+", packageToScan)) {
                throw new ServiceContainerException(String.format(
                        "Bad format for package '%s'. Expected package like com.company.package", packageToScan));
            }
        }
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        try {
            // get all classes with DeployServiceProxy annotation in packages
            new Reflections(packageToScans, new SubTypesScanner(), new TypeAnnotationsScanner())
                    .getTypesAnnotatedWith(DeployServiceProxy.class).stream()
                    // filter only class
                    .filter(PredicateHelper::isNotInterface)
                    // for each
                    .forEach(this::deployService);
            startFuture.complete();
        } catch (Exception e) {
            startFuture.fail(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected void deployService(Class<?> clazz) {
        // getting :
        // @ProxyGen
        // public interface MyService {
        // public static final String ADDRESS = "address.of.service.for.evenbus";
        // ...
        // }
        Optional<Class<?>> optionalInterfaceClass = Stream.of(clazz.getInterfaces())
                .filter(PredicateHelper::isInterfaceWithProxyGenAnnotationAndAddressConstante).findFirst();
        @SuppressWarnings("rawtypes")
        Class interfaceClass = optionalInterfaceClass
                .orElseThrow(() -> {
                    return new ServiceContainerException(
                            String.format(
                                    "Class[%s] is not well formed. Please check that this class needs to implements interface annoted ProxyGen AND that this interface must define ADDRESS constante String",
                                    clazz));
                });

        // getting ADDRESS (field has already been checked by
        // PredicateHelper::isInterfaceWithProxyGenAnnotationAndAddressConstante)
        String address;
        try {
            address = (String) interfaceClass.getField(PredicateHelper.ADDRESS_NAME).get(null);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new ServiceContainerException(String.format("Class[%s] failed getting %s String constante", clazz,
                    PredicateHelper.ADDRESS_NAME), e);
        }

        // creating serviceImpl :
        // @DeployServiceProxy
        // public class MyServiceImpl implements MyService {
        // // authorized constructor
        // public MyServiceImpl(Vertx vertx){}
        // ...
        // }
        Constructor<?> constructor = Stream
                .of(clazz.getConstructors())
                .filter(PredicateHelper::isConstructorWithVertxArgument)
                .findFirst()
                .orElseThrow(
                        () -> {
                            return new ServiceContainerException(
                                    String.format(
                                            "Class[%s] is not well formed. Please check that this class needs public constructor with unique parameter Vertx",
                                            clazz));
                        });
        Object service;
        try {
            service = constructor.newInstance(vertx);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ServiceContainerException(
                    String.format("Class[%s] failed calling constructor with Vertx paramater"), e);
        }

        LOGGER.info(String.format("[%s] service deploying...", clazz.getSimpleName()));
        // call deploy method and store result
        deployedServices.add(ProxyHelper.registerService(interfaceClass, vertx, service, address));
        loggingMethodsService(clazz);
        LOGGER.info(String.format("[%s] service deployed.", clazz.getSimpleName()));
    }

    @SuppressWarnings("unchecked")
    private void loggingMethodsService(Class<?> clazz) {
        ReflectionUtils.getMethods(clazz, (method) -> {
            return Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers());
        }).stream().forEach((method) -> {
            LOGGER.info(String.format("[%s] provide : %s(..)", clazz.getSimpleName(), method.getName()));
        });
    }

    @Override
    public void stop() throws Exception {
        // unregister all services
        deployedServices.stream().forEach((consumer) -> {
            if (consumer.isRegistered()) {
                consumer.unregister();
            }
        });
    }
}
