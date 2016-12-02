package fr.pjthin.vertx.container.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation helping class to deploy service.
 * 
 * @author Pidji
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DeployServiceProxy {

	/** set to true for deploying Verticle automatically */
	boolean autoDeployVerticle() default false;

}
