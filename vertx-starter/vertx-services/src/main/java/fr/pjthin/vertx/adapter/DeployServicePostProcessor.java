package fr.pjthin.vertx.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import fr.pjthin.vertx.container.core.DeployServiceProxy;
import fr.pjthin.vertx.container.core.ServiceContainer;

/**
 * Post-processor getting all {@link DeployServiceProxy} and adding them to
 * {@link ServiceContainer}.
 * 
 * @author Pidji
 */
public class DeployServicePostProcessor implements BeanPostProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeployServicePostProcessor.class);

	private final ServiceContainer container;

	public DeployServicePostProcessor(ServiceContainer container) {
		this.container = container;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean.getClass().isAnnotationPresent(DeployServiceProxy.class)) {
			LOGGER.debug(String.format("adding [beanName=%s, class=%s] to services to deploy", beanName,
					bean.getClass().getSimpleName()));
			container.addProxyfiedService(bean);
		}
		return bean;
	}

}
