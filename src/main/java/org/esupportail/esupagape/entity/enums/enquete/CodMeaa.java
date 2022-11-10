package org.esupportail.esupagape.entity.enums.enquete;

public enum CodMeaa {

    AA1("l'étudiant est reçu ponctuellement par la structure ou le responsable handicap de l’établissement"),
    AA2("l'étudiant est suivi par la structure ou le responsable handicap de l’établissement"),
    AA3("recours à un matériel ou support spécialement adapté au besoin de l'étudiant handicapé"),
    AAo("autre (à préciser)");

    private final String libelle;

    private CodMeaa(String value) {
        libelle = value;
    }

    public String getLibelle() {
        return libelle;
    }
}
