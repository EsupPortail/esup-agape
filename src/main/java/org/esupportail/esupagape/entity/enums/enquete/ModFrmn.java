package org.esupportail.esupagape.entity.enums.enquete;

public enum ModFrmn {
    NUL("--Choisir--"), P("présentiel"), D("distance"), A("alternance");
    private final String libelle;

    private ModFrmn(String value) {
        libelle = value;
    }

    public String getLibelle() {
        return libelle;
    }
}

