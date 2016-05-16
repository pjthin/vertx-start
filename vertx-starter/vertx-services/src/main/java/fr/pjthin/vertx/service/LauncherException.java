package fr.pjthin.vertx.service;

public class LauncherException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public LauncherException(String error, Throwable cause) {
        super(error, cause);
    }

    public LauncherException(String error) {
        super(error);
    }

}
