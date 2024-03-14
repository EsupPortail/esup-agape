package org.esupportail.esupagape.exception;

public class AgapeApogeeException extends AgapeException {

    String message;

    public AgapeApogeeException(String message) {
        super(message);
        this.message = message;
    }

    public AgapeApogeeException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }
}
