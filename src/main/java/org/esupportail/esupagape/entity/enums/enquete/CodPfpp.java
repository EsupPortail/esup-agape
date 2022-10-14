package org.esupportail.esupagape.entity.enums.enquete;

public enum CodPfpp {
    NUL("--Choisir--"),
    PP0("pas d'aide, ni d'aménagement mis en place dans le cadre de la formation suivie durant la présente année universitaire"),
    MH1("plan d'accompagnement formalisé communiqué à la MDPH"),
    MH0("plan d'accompagnement formalisé non communiqué à la MDPH"),
    PP2("plan d'accompagnement non encore formalisé");

    private final String libelle;

    private CodPfpp(String value) {
        libelle = value;
    }

    public String getLibelle() {
        return libelle;
    }
}
