package org.esupportail.esupagape.entity.enums.enquete;

public enum CodHd {
    NUL("--Choisir--"),
    COG("Troubles cognitifs"),
    TSA("TSA (troubles spécifiques de l'autisme)"),
    PSY("Troubles psychiques"),
    LNG("Troubles du langage et de la parole"),
    MOT("Troubles moteurs"),
    VIS("Troubles viscéraux (maladies invalidantes)"),
    VIS0("Troubles viscéraux Pathologies cancéreuses"),
    VUE("Troubles Visuels Majeurs (Cécité)"),
    VUA("Autres troubles visuels"),
    AUD("Troubles Auditifs Majeurs (Surdité sévère et profond)"),
    AUA("Autres Troubles Auditifs"),
    PTA("Plusieurs troubles majeurs associés / poly handicap"),
    AUT("Autres troubles"),
    TND("Troubles non divulgués (par l'étudiant)");

    private final String libelle;

    private CodHd(String value) {
        libelle = value;
    }

    public String getLibelle() {
        return libelle;
    }
}
