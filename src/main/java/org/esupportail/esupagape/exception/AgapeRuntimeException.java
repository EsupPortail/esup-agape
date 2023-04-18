package org.esupportail.esupagape.exception;

public class AgapeRuntimeException extends RuntimeException {

    String message;

    public AgapeRuntimeException(String message) {
        super(message);
        this.message = message;
    }

    public AgapeRuntimeException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }
}
