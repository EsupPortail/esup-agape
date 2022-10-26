package org.esupportail.esupagape.exception;

public class AgapeIOException extends AgapeException {

    String message;

    public AgapeIOException(String message) {
        super(message);
        this.message = message;
    }

    public AgapeIOException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }
}
