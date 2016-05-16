package fr.pjthin.vertx.service.container;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ProxyHelper;

import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Container launching {@link ProxyfiedService}.
 * 
 * @author Pidji
 */
public class ServiceContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceContainer.class);

    @Autowired
    private ListProxyfiedService toDeployServices;

    @Autowired
    private Vertx vertx;

    private boolean autoDeployVerticle = false;

    @Fluent
    public ServiceContainer setAutoDeployVerticle(boolean autoDeployVerticle) {
        this.autoDeployVerticle = autoDeployVerticle;
        return this;
    }

    @Fluent
    public ServiceContainer setToDeployServices(ListProxyfiedService toDeployServices) {
        this.toDeployServices = toDeployServices;
        return this;
    }

    @Fluent
    public ServiceContainer setVertx(Vertx vertx) {
        this.vertx = vertx;
        return this;
    }

    @PostConstruct
    public void start() throws Exception {
        toDeployServices.getProxyfiedServices().forEach(this::deployService);
    }

    @SuppressWarnings("unchecked")
    protected void deployService(ProxyfiedService service) {

        // deploying verticle if needed
        if (autoDeployVerticle && service.getService() instanceof AbstractVerticle) {
            LOGGER.info(String.format("[%s] auto-deploy verticle...", service));
            vertx.deployVerticle(((AbstractVerticle) service.getService()), (handler) -> {
                // TODO synchronized deploying verticle
                });
        }

        LOGGER.info(String.format("[%s] service deploying...", service));
        // call deploy method and store result
        service.setDeployedServiceConsumer(ProxyHelper.registerService(service.getInterfaze(), vertx,
                service.getService(), service.getAddress()));
        loggingMethodsService(service);
        LOGGER.info(String.format("[%s] service deployed.", service));
    }

    private void loggingMethodsService(final ProxyfiedService service) {
        Stream.of(service.getService().getClass().getMethods()).filter(PredicateHelper::isMethodPublicAndNotStatic)
                .forEach((method) -> {
                    LOGGER.info(String.format("[%s] provide : %s(..)", service, method.getName()));
                });
    }

    @PreDestroy
    public void stop() {
        // unregister all services
        toDeployServices.getProxyfiedServices().stream().map(ProxyfiedService::getDeployedServiceConsumer)
                .forEach((consumer) -> {
                    if (consumer.isRegistered()) {
                        consumer.unregister();
                    }
                });
    }
}
