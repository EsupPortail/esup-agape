package org.esupportail.esupagape.entity.enums.enquete;

public enum CodFil {

    NUL("--Choisir--"),
    LSH("Lettres, Sciences humaines et sociales, Langues"),
    DSG("Droit, sciences Eco, gestion, AES"),
    SCI("Sciences"),
    STA("STAPS"),
    SAN("Santé (médecine,odontologie,sage-femme,pharmacie)"),
    PAR("Paramédicale (psychomotricien,orthophoniste,kinésithérapeute,…)");

    private final String libelle;

    private CodFil(String value) {
        libelle = value;
    }

    public String getLibelle() {
        return libelle;
    }
}
