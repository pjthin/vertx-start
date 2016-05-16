package fr.pjthin.vertx.service.container;

import io.vertx.codegen.annotations.Fluent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Post-processor getting all {@link DeployServiceProxy} and adding them to {@link ListProxyfiedService}.
 * 
 * @author Pidji
 */
public class DeployServicePostProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeployServicePostProcessor.class);

    @Autowired
    private ListProxyfiedService toDeployServices;

    @Fluent
    public DeployServicePostProcessor setToDeployServices(ListProxyfiedService toDeployServices) {
        this.toDeployServices = toDeployServices;
        return this;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(DeployServiceProxy.class)) {
            LOGGER.debug(String.format("adding [beanName=%s, class=%s] to services to deploy", beanName, bean
                    .getClass().getSimpleName()));
            toDeployServices.addProxyfiedService(bean);
        }
        return bean;
    }

}
