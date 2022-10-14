package org.esupportail.esupagape.entity.enums.enquete;

public enum CodFmt {
    NUL("--Choisir--"),
    CA("Capacité en droit"),
    DAA("DAEU A"),
    DAB("DAEU B"),
    IUT("DUT"),
    L("Licence"),
    LP("Licence Pro"),
    M("Master"),
    MF("Master MEEF"),
    IEP("IEP"),
    ING("Ingénieur"),
    D("Doctorat"),
    HDR("HDR"),
    DU("DU"),
    PA("PACES"),
    MED("Médecine"),
    ODO("Odontologie"),
    PHA("Pharmacie"),
    MAI("Sage-femme"),
    AUP("Audioprothésiste"),
    INF("Infirmier"),
    ANE("Infirmier anesthésiste"),
    PED("Pédicure-podologue"),
    PSM("Psychomotricien"),
    ORH("Orthophoniste"),
    ORT("Orthoptiste"),
    BIM("Biologie médicale"),
    KIN("Masseur-kinésithérapeuteé"),
    MEM("Manipulateur d'électroradiologie médicale"),
    ERG("Ergothérapeute"),
    AFP("Paramédicale Autre"),
    X("Autres formations");


    private final String libelle;

    private CodFmt(String value) {
        libelle = value;
    }

    public String getLibelle() {
        return libelle;

    }

}