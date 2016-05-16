package fr.pjthin.vertx.service.container;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * Annotation helping class to deploy service.
 * <p>
 * Note: linked with @Component Spring annotation to add directly the bean into Spring context.
 * 
 * @author Pidji
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface DeployServiceProxy {

}
