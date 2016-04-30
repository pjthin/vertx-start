package fr.pjthin.vertx.service.container;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceContainer extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceContainer.class);

    private List<MessageConsumer<JsonObject>> deployedServices;
    private DeployServiceProxyMethodPredicate deployServiceProxyMethodPredicate;
    private String[] packageToScans;

    public ServiceContainer(String... packageToScans) {
        super();
        checkPackage(packageToScans);
        this.packageToScans = packageToScans;
        this.deployedServices = new ArrayList<>();
        this.deployServiceProxyMethodPredicate = new DeployServiceProxyMethodPredicate();
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

    @SuppressWarnings("unchecked")
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        try {
            // get all classes with DeployServiceProxy annotation in packages
            new Reflections(packageToScans, new SubTypesScanner(), new TypeAnnotationsScanner())
                    .getTypesAnnotatedWith(DeployServiceProxy.class).stream()
                    // filter only interface
                    .filter((clazz) -> {
                        return Modifier.isInterface(clazz.getModifiers());
                    })
                    // for each
                    .forEach((clazz) -> {
                        // get annoted method
                            Set<Method> methods = ReflectionUtils.getMethods(clazz, deployServiceProxyMethodPredicate);
                            // must be an annoted method otherwise bad configuration
                            if (methods.isEmpty() || methods.size() != 1) {
                                throw new ServiceContainerException(
                                        String.format(
                                                "[%s] No or too many method signature with @%s %s<JsonObject> name(%s param) found",
                                                clazz.getSimpleName(), DeployServiceProxyMethod.class,
                                                MessageConsumer.class, Vertx.class));
                            }
                            // should be only one now
                            methods.stream().forEach((method) -> {
                                try {
                                    LOGGER.info(String.format("[%s] service deploying...", clazz.getSimpleName()));
                                    // call deploy method and store result
                                    deployedServices.add((MessageConsumer<JsonObject>) method.invoke(null, vertx));
                                    loggingMethodsService(clazz);
                                    LOGGER.info(String.format("[%s] service deployed.", clazz.getSimpleName()));
                                } catch (Exception e) {
                                    throw new ServiceContainerException(String.format(
                                            "[%s] failed invoking deployed method", clazz.getSimpleName()), e);
                                }
                            });
                        });

            startFuture.complete();
        } catch (Exception e) {
            startFuture.fail(e);
        }
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
