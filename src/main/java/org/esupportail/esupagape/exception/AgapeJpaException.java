package org.esupportail.esupagape.exception;

public class AgapeJpaException extends AgapeRuntimeException {

    String message;

    public AgapeJpaException(String message) {
        super(message);
        this.message = message;
    }

    public AgapeJpaException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }
}
