package org.esupportail.esupagape.entity.enums.enquete;

public enum CodSco {
    NUL("--Choisir--"),
    IUTIU1("IUT 1e année"),
    IUTIU2("IUT 2e année"),
    LICL1("Licence 1"),
    LICL2("Licence 2"),
    LICL3("Licence 3"),
    MASM1("Master 1"),
    MASM2("Master 2"),
    IEP1("IEP Bac+1"),
    IEP2("IEP Bac+2"),
    IEP3("IEP Bac+3"),
    IEP4("IEP Bac+4"),
    IEP5("IEP Bac+5"),
    ING1("Ingénieur prépa 1"),
    ING2("Ingénieur prépa 2"),
    ING3("Ingénieur 1e année ingénieur"),
    ING4("Ingénieur 2e année ingénieur"),
    ING5("Ingénieur 3e année ingénieur"),
    ING6("Ingénieur Bac+6 et plus"),
    SAN1A("Santé/paramédicale 1e année"),
    SAN2A("Santé/paramédicale 2e année"),
    SAN3A("Santé/paramédicale 3e année"),
    SAN4A("Santé/paramédicale 4e année"),
    SAN5A("Santé/paramédicale 5e année"),
    SAN6A("Santé/paramédicale 6e année et plus"),
    CYCX1("Autre 1e cycle"),
    CYCX2("Autre 2e cycle");

    private final String libelle;

    private CodSco(String value) {
        libelle = value;
    }

    public String getLibelle() {
        return libelle;
    }
}

