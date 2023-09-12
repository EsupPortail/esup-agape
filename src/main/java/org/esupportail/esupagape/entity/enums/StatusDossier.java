package org.esupportail.esupagape.entity.enums;

public enum StatusDossier {
    IMPORTE("rgba(125, 125, 218, 1)", "rgba(125, 125, 218, 0.3)"),
    AJOUT_MANUEL("", ""),
    RECU_PAR_LA_MEDECINE_PREVENTIVE("", ""),
    RECONDUIT("", ""),
    NON_RECONDUIT("", ""),
    ACCUEILLI("", ""),
    SUIVI("", ""),
    IMPOSSIBLE_A_CONTACTER("", ""),
    ANONYMOUS("", "");
    //DESINSCRIT

    final String badgeColor;

    final String backgroundColor;

    StatusDossier(String badgeColor, String backgroundColor) {
        this.badgeColor = badgeColor;
        this.backgroundColor = backgroundColor;
    }

    public String getBadgeColor() {
        return badgeColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }
}
