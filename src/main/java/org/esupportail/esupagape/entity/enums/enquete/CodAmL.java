package org.esupportail.esupagape.entity.enums.enquete;

public enum CodAmL {

    am00("Ne sais pas si l'étudiant en bénéficie"),
    am1("Accompagnement par un établissement ou un service médico-social"),
    am10("non"),
    am11("oui"),
    am1x("ne sais pas"),
    am2("Accompagnement par un auxiliaire de vie (PCH ou ATP)"),
    am20("non"),
    am21("oui"),
    am2x("ne sais pas"),
    am3("Recours à un mode de transport spécifique"),
    am30("non"),
    am31("oui"),
    am3x("ne sais pas"),
    am4("Reconnaissance de la qualité de travailleur handicapé (RQTH)"),
    am40("non"),
    am41("oui"),
    am4x("ne sais pas"),
    am5("Autre mesure relevant d’une décision de la CDA PH (carte d'invalidité, de stationnement,...)"),
    am50("non"),
    am51("oui"),
    am5x("ne sais pas"),
    am6("Accompagnement par un établissement ou un service sanitaire [mesure non CDA PH]"),
    am60("non"),
    am61("oui"),
    am6x("ne sais pas"),
    am7("Autre(s) intervenant(s) [mesure non CDA PH]"),
    am70("non"),
    am71("oui"),
    am7x("ne sais pas"),
    am8("Carte d'invalidité ou AAH"),
    am80("non"),
    am81("oui"),
    am8x("ne sais pas");

    private final String libelle;

    private CodAmL(String value) {
        libelle = value;
    }

    public String getLibelle() {
        return libelle;
    }

}
