package org.esupportail.esupagape.entity.enums;

public enum StatusDossier {
    IMPORTE("Importé"),
    ACCUEILLI_ENTRETIEN_PHYSIQUE("Accueilli en entretien physique"),
    ACCUEILLI_ENTRETIEN_TELEPHONIQUE_MAIL("Accueilli en entretien à distance"),
    SUIVI("Suivi"),
    IMPOSSIBLE_A_CONTACTER("Impossible à contacter"),
    RECU_PAR_LA_MEDECINE_PREVENTIVE("Reçu par la médecine préventive");

    private final String displayValue;

    private StatusDossier(String displayValue){
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
