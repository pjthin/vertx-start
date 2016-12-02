package fr.pjthin.vertx.container.core;

public class ServiceContainerException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ServiceContainerException() {
        super();
    }

    public ServiceContainerException(String err) {
        super(err);
    }

    public ServiceContainerException(String err, Throwable t) {
        super(err, t);
    }

}
