package org.esupportail.esupagape.entity;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Enquete {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nfic;

    private String numetu;

    private String an;

    @Enumerated
    private Sexe sexe;

    @Enumerated
    private TypeFrmn typefrmn;

    @Enumerated
    private ModFrmn modfrmn;

    @Enumerated
    private CodSco codsco;

    @Enumerated
    private CodFmt codfmt;

    @Enumerated
    private CodFil codfil;

    @Enumerated
    private CodHd codhd;

    private boolean hdtmp;

    @Column(columnDefinition = "TEXT")
    private String com;

    @Enumerated
    private CodPfpp codpfpp;

    @Enumerated
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

    @Enumerated
    private CodMeaa codmeaa;

    @Column(columnDefinition = "TEXT")
    private String autaa;

    @ElementCollection(targetClass=Enquete.CodAmL.class)
    @Enumerated
    private Collection<CodAmL> codamL;

    private String djaCop;

    private String newnum;

    private String newid;

    public enum Sexe {
        nul("--Choisir--"), f("féminin"), m("masculin");

        private final String libelle;

        private Sexe(String value) {
            libelle = value;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum TypeFrmn {
        nul("--Choisir--"), i("initiale"), c("continue");

        private final String libelle;

        private TypeFrmn(String value) {
            libelle = value;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum ModFrmn {
        nul("--Choisir--"), p("présentiel"), d("distance"), a("alternance");

        private final String libelle;

        private ModFrmn(String value) {
            libelle = value;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum CodSco {
        nul("--Choisir--"),
        iutiu1("IUT 1e année"),
        iutiu2("IUT 2e année"),
        licL1("Licence 1"),
        licL2("Licence 2"),
        licL3("Licence 3"),
        masM1("Master 1"),
        masM2("Master 2"),
        iep1("IEP Bac+1"),
        iep2("IEP Bac+2"),
        iep3("IEP Bac+3"),
        iep4("IEP Bac+4"),
        iep5("IEP Bac+5"),
        ing1("Ingénieur prépa 1"),
        ing2("Ingénieur prépa 2"),
        ing3("Ingénieur 1e année ingénieur"),
        ing4("Ingénieur 2e année ingénieur"),
        ing5("Ingénieur 3e année ingénieur"),
        ing6("Ingénieur Bac+6 et plus"),
        san1a("Santé/paramédicale 1e année"),
        san2a("Santé/paramédicale 2e année"),
        san3a("Santé/paramédicale 3e année"),
        san4a("Santé/paramédicale 4e année"),
        san5a("Santé/paramédicale 5e année"),
        san6a("Santé/paramédicale 6e année et plus"),
        cycx1("Autre 1e cycle"),
        cycx2("Autre 2e cycle");

        private final String libelle;

        private CodSco(String value) {
            libelle = value;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum CodFmt {
        nul("--Choisir--"),
        CA("Capacité en droit"),
        DaA("DAEU A"),
        DaB("DAEU B"),
        iut("DUT"),
        L("Licence"),
        Lp("Licence Pro"),
        M("Master"),
        Mf("Master MEEF"),
        iep("IEP"),
        ing("Ingénieur"),
        D("Doctorat"),
        HDR("HDR"),
        DU("DU"),
        pa("PACES"),
        med("Médecine"),
        odo("Odontologie"),
        pha("Pharmacie"),
        mai("Sage-femme"),
        aup("Audioprothésiste"),
        inf("Infirmier"),
        ane("Infirmier anesthésiste"),
        ped("Pédicure-podologue"),
        psm("Psychomotricien"),
        orh("Orthophoniste"),
        ort("Orthoptiste"),
        bim("Biologie médicale"),
        kin("Masseur-kinésithérapeuteé"),
        mem("Manipulateur d'électroradiologie médicale"),
        erg("Ergothérapeute"),
        afp("Paramédicale Autre"),
        x("Autres formations");

        private final String libelle;

        private CodFmt(String value) {
            libelle = value;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum CodFil {
        nul("--Choisir--"),
        lsh("Lettres, Sciences humaines et sociales, Langues"),
        dsg("Droit, sciences Eco, gestion, AES"),
        sci("Sciences"),
        sta("STAPS"),
        san("Santé (médecine,odontologie,sage-femme,pharmacie)"),
        par("Paramédicale (psychomotricien,orthophoniste,kinésithérapeute,…)");

        private final String libelle;

        private CodFil(String value) {
            libelle = value;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum CodHd {
        nul("--Choisir--"),
        cog("Troubles cognitifs"),
        tsa("TSA (troubles spécifiques de l'autisme)"),
        psy("Troubles psychiques"),
        lng("Troubles du langage et de la parole"),
        mot("Troubles moteurs"),
        vis("Troubles viscéraux (maladies invalidantes)"),
        vis0("Troubles viscéraux Pathologies cancéreuses"),
        vue("Troubles Visuels Majeurs (Cécité)"),
        vuA("Autres troubles visuels"),
        aud("Troubles Auditifs Majeurs (Surdité sévère et profond)"),
        auA("Autres Troubles Auditifs"),
        pta("Plusieurs troubles majeurs associés / poly handicap"),
        aut("Autres troubles"),
        tnd("Troubles non divulgués (par l'étudiant)");

        private final String libelle;

        private CodHd(String value) {
            libelle = value;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum CodPfpp {
        nul("--Choisir--"),
        pp0("pas d'aide, ni d'aménagement mis en place dans le cadre de la formation suivie durant la présente année universitaire"),
        mh1("plan d'accompagnement formalisé communiqué à la MDPH"),
        mh0("plan d'accompagnement formalisé non communiqué à la MDPH"),
        pp2("plan d'accompagnement non encore formalisé");

        private final String libelle;

        private CodPfpp(String value) {
            libelle = value;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum CodPfas {
        nul("--Choisir--"),
        as0("pas d'aménagement spécifique du cursus ou du parcours en raison du handicap"),
        as1("cursus ou parcours aménagé en raison du handicap");

        private final String libelle;

        private CodPfas(String value) {
            libelle = value;
        }

        public String getLibelle() {
            return libelle;
        }
    }

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

    public enum CodMeaa {
        nul("--Choisir--"),
        aa1("l'étudiant est reçu ponctuellement par la structure ou le responsable handicap de l’établissement"),
        aa2("l'étudiant est suivi par la structure ou le responsable handicap de l’établissement"),
        aa3("recours à un matériel ou support spécialement adapté au besoin de l'étudiant handicapé"),
        aao("autre (à préciser)");

        private final String libelle;

        private CodMeaa(String value) {
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
