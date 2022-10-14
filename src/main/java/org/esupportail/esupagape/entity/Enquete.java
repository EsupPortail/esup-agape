package org.esupportail.esupagape.entity;

import org.esupportail.esupagape.entity.enums.enquete.CodFil;
import org.esupportail.esupagape.entity.enums.enquete.CodFmt;
import org.esupportail.esupagape.entity.enums.enquete.CodHd;
import org.esupportail.esupagape.entity.enums.enquete.CodMeaa;
import org.esupportail.esupagape.entity.enums.enquete.CodPfas;
import org.esupportail.esupagape.entity.enums.enquete.CodPfpp;
import org.esupportail.esupagape.entity.enums.enquete.CodSco;
import org.esupportail.esupagape.entity.enums.enquete.ModFrmn;
import org.esupportail.esupagape.entity.enums.enquete.Sexe;
import org.esupportail.esupagape.entity.enums.enquete.TypeFrmn;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Collection;

@Entity
public class Enquete {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nfic;

    private String numetu;

    private String an;

    @Enumerated(EnumType.STRING)
    private Sexe sexe;

    @Enumerated(EnumType.STRING)
    private TypeFrmn typefrmn;

    @Enumerated(EnumType.STRING)
    private ModFrmn modfrmn;

    @Enumerated(EnumType.STRING)
    private CodSco codsco;

    @Enumerated(EnumType.STRING)
    private CodFmt codfmt;

    @Enumerated(EnumType.STRING)
    private CodFil codfil;

    @Enumerated(EnumType.STRING)
    private CodHd codhd;

    private boolean hdtmp;

    @Column(columnDefinition = "TEXT")
    private String com;

    @Enumerated(EnumType.STRING)
    private CodPfpp codpfpp;

    @Enumerated(EnumType.STRING)
    private CodPfas codpfas;

    @ElementCollection(targetClass=Enquete.CodMeahF.class)
    @Enumerated
    private Collection<CodMeahF> codmeahF;

    private int interpH;

    private int codeurH;

    @Column(columnDefinition = "TEXT")
    private String aidhnat;

    @ElementCollection(targetClass=Enquete.CodMeae.class)
    @Enumerated
    private Collection<CodMeae> codmeae;

    @Column(columnDefinition = "TEXT")
    private String autae;

    @Enumerated(EnumType.STRING)
    private CodMeaa codmeaa;

    @Column(columnDefinition = "TEXT")
    private String autaa;

    @ElementCollection(targetClass=Enquete.CodAmL.class)
    @Enumerated
    private Collection<CodAmL> codamL;

    private String djaCop;

    private String newnum;

    private String newid;


    public enum CodMeahF {
        ahs0("aucune aide humaine spécifique"),
        ahs1("interprète"),
        ahs1e("interprète: lors des examens"),
        ahs1f("interprète: cadre de la formation suivie"),
        ahs2("codeur"),
        ahs2e("codeur : lors des examens"),
        ahs2f("codeur: cadre de la formation suivie"),
        ahs3("preneur de notes"),
        ahs3o("preneur de notes: ponctuelle"),
        ahs3p("preneur de notes: permanente ou régulière"),
        ahs4("soutien pédagogique ou tutorat en raison de la situation de handicap"),
        ahs5("autre aide humaine spécifique"),
        ahs5o("autre aide humaine spécifique: ponctuelle"),
        ahs5q("autre aide humaine spécifique: quotidienne"),
        ahs5h("autre aide humaine spécifique: hebdomadaire"),
        ahs5m("autre aide humaine spécifique: mensuelle"),
        ahs5s("autre aide humaine spécifique: semestrielle"),
        ahs5e("autre aide humaine spécifique: lors des examens");

        private final String libelle;

        private CodMeahF(String value) {
            libelle = value;
        }

        public String getLibelle() {
            return libelle;
        }
    }

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

}
