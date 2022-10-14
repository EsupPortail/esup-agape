package org.esupportail.esupagape.entity.enums.enquete;

public enum TypeFrmn {
    NUL("--Choisir--"), I("initiale"), C("continue");

    private final String libelle;

    private TypeFrmn(String value) {
        libelle = value;
    }

    public String getLibelle() {
        return libelle;
    }
}

