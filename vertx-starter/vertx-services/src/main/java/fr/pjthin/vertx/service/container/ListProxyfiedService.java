package fr.pjthin.vertx.service.container;

import io.vertx.codegen.annotations.Fluent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * List of {@link ProxyfiedService}.
 * <p>
 * Giving a way to add new service with {@link ListProxyfiedService#addProxyfiedService(Object)}.
 * 
 * @author Pidji
 */
public class ListProxyfiedService {

    private List<ProxyfiedService> proxyfiedServices;

    public ListProxyfiedService() {
        proxyfiedServices = new ArrayList<>();
    }

    public List<ProxyfiedService> getProxyfiedServices() {
        return proxyfiedServices;
    }

    /**
     * Adding service to the list.
     * 
     * @param service
     * @return this
     * @throws ServiceContainerException
     *             if failed to add service
     */
    @Fluent
    public ListProxyfiedService addProxyfiedService(Object proxyfiedService) {
        Class<?> clazz = proxyfiedService.getClass();

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

        proxyfiedServices.add(new ProxyfiedService().setAddress(address).setService(proxyfiedService)
                .setInterfaze(interfaceClass));

        return this;
    }

}
