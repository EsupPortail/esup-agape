package org.esupportail.esupagape.entity.enums;

public enum TypeIndividu {

    LYCEEN("Lycéen"), ETUDIANT("Etudiant"), HORS_UNIV("Hors université");

    private final String displayValue;

    private TypeIndividu(String displayValue){
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
