package org.esupportail.esupagape.entity.enums.enquete;

public enum CodPfas {
    NUL("--Choisir--"),
    AS0("pas d'aménagement spécifique du cursus ou du parcours en raison du handicap"),
    AS1("cursus ou parcours aménagé en raison du handicap");

    private final String libelle;

    private CodPfas(String value) {
        libelle = value;
    }

    public String getLibelle() {
        return libelle;
    }
}
