package fr.pjthin.vertx.service.container;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;

/**
 * Class representation of proxyfied service.
 * 
 * @author Pidji
 */
public class ProxyfiedService {

    /** bean service */
    private Object service;
    /** interface implemented by bean service and @ProxyGen */
    private Class<?> interfaze;
    /** adress listening on evenbus */
    private String address;
    /** reference to deployed message consumer */
    private MessageConsumer<JsonObject> deployedServiceConsumer;

    public Object getService() {
        return service;
    }

    @Fluent
    public ProxyfiedService setService(Object service) {
        this.service = service;
        return this;
    }

    @SuppressWarnings("rawtypes")
    public Class getInterfaze() {
        return interfaze;
    }

    @Fluent
    public ProxyfiedService setInterfaze(Class<?> interfaze) {
        this.interfaze = interfaze;
        return this;
    }

    public String getAddress() {
        return address;
    }

    @Fluent
    public ProxyfiedService setAddress(String address) {
        this.address = address;
        return this;
    }

    public MessageConsumer<JsonObject> getDeployedServiceConsumer() {
        return deployedServiceConsumer;
    }

    @Fluent
    public ProxyfiedService setDeployedServiceConsumer(MessageConsumer<JsonObject> deployedServiceConsumer) {
        this.deployedServiceConsumer = deployedServiceConsumer;
        return this;
    }
}
