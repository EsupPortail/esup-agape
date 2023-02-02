package org.esupportail.esupagape.exception;

public class AgapeYearException extends AgapeException {

    String message;

    public AgapeYearException() {
        super("Impossible de modifier un dossier d'une année précédente");
        this.message = super.getMessage();
    }

}
