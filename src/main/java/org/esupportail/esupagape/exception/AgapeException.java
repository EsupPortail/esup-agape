package org.esupportail.esupagape.exception;

public class AgapeException extends RuntimeException {

    String message;

    public AgapeException(String message) {
        super(message);
        this.message = message;
    }

    public AgapeException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }
}
