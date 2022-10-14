package org.esupportail.esupagape.entity.enums.enquete;

public enum Sexe {
    NUL("--Choisir--"),F("féminin"), M("masculin");

    private final String libelle;

    private Sexe(String value) {
        libelle = value;
    }

    public String getLibelle() {
        return libelle;
    }
}

