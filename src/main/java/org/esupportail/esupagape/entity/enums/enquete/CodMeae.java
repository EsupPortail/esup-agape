package org.esupportail.esupagape.entity.enums.enquete;

public enum CodMeae {

    ae0("aucun aménagement des examens"),
    ae1("mise à disposition de matériel adapté"),
    ae2("documents adaptés"),
    ae3("secrétaire"),
    ae4("épreuves aménagées"),
    ae5("interprètes, codeurs, autre aide à la communication"),
    ae6("salle particulière"),
    ae7("temps majoré"),
    ae8("temps de pause"),
    aeo("autre aménagement (à préciser)");

    private final String libelle;

    private CodMeae(String value) {
        libelle = value;
    }

    public String getLibelle() {
        return libelle;
    }

}
