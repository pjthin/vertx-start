package fr.pjthin.vertx.container.core;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ProxyHelper;

/**
 * Container launching {@link ProxyfiedService}.
 * 
 * @author Pidji
 */
public class ServiceContainer {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceContainer.class);

	private final ListProxyfiedService toDeployServices;
	private final Vertx vertx;

	public ServiceContainer(Vertx vertx) {
		this.vertx = vertx;
		this.toDeployServices = new ListProxyfiedService();
	}

	public void start() {
		LOGGER.info("ServiceContainer.start: Starting...");
		toDeployServices.getProxyfiedServices().forEach(this::deployService);
		LOGGER.info("ServiceContainer.start: Done.");
	}

	@SuppressWarnings("unchecked")
	protected void deployService(ProxyfiedService service) {

		// deploying verticle if needed
		if (service.isAutoDeployVerticle() && service.getService() instanceof AbstractVerticle) {
			LOGGER.info(String.format("[%s] auto-deploy verticle...", service));
			vertx.deployVerticle(((AbstractVerticle) service.getService()), handler -> {
				// TODO synchronized deploying verticle
			});
		}

		LOGGER.info(String.format("[%s] service deploying...", service));
		// call deploy method and store result
		service.setDeployedServiceConsumer(
				ProxyHelper.registerService(service.getInterfaze(), vertx, service.getService(), service.getAddress()));
		loggingMethodsService(service);
		LOGGER.info(String.format("[%s] service deployed.", service));
	}

	private void loggingMethodsService(final ProxyfiedService service) {
		Stream.of(service.getService().getClass().getMethods()).filter(PredicateHelper::isMethodPublicAndNotStatic)
				.forEach((method) -> {
					LOGGER.info(String.format("[%s] provide : %s(..)", service, method.getName()));
				});
	}

	public void stop() {
		LOGGER.info("ServiceContainer.start: Stoping...");
		// unregister all services
		toDeployServices.getProxyfiedServices().stream().map(ProxyfiedService::getDeployedServiceConsumer)
				.forEach((consumer) -> {
					if (consumer.isRegistered()) {
						consumer.unregister();
					}
				});
		LOGGER.info("ServiceContainer.start: Done.");
	}

	public void addProxyfiedService(Object bean) {
		toDeployServices.addProxyfiedService(bean);
	}
}
