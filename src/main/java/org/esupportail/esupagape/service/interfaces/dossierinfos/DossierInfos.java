package org.esupportail.esupagape.service.interfaces.dossierinfos;

import java.util.ArrayList;
import java.util.List;

public class DossierInfos {

    String codComposante;
    String composante;
    String niveauEtudes;
    String secteurDisciplinaire;
    String typeDiplome;
    String libelleFormation;
    String libelleFormationPrec;
    String etablissement;
    List<String> campus = new ArrayList<>();
    String formAddress;
    String noteS1;
    String resultatS1;
    String noteS2;
    String resultatS2;
    String noteAnn;
    String resultatAnn;
    Boolean hasScholarship;


    public String getCodComposante() {
        return codComposante;
    }

    public void setCodComposante(String codComposante) {
        this.codComposante = codComposante;
    }

    public String getComposante() {
        return composante;
    }

    public void setComposante(String composante) {
        this.composante = composante;
    }

    public String getNiveauEtudes() {
        return niveauEtudes;
    }

    public void setNiveauEtudes(String niveauEtudes) {
        this.niveauEtudes = niveauEtudes;
    }

    public String getSecteurDisciplinaire() {
        return secteurDisciplinaire;
    }

    public void setSecteurDisciplinaire(String secteurDisciplinaire) {
        this.secteurDisciplinaire = secteurDisciplinaire;
    }

    public String getTypeDiplome() {
        return typeDiplome;
    }

    public void setTypeDiplome(String typeDiplome) {
        this.typeDiplome = typeDiplome;
    }

    public String getLibelleFormation() {
        return libelleFormation;
    }

    public void setLibelleFormation(String libelleFormation) {
        this.libelleFormation = libelleFormation;
    }

    public String getLibelleFormationPrec() {
        return libelleFormationPrec;
    }

    public void setLibelleFormationPrec(String libelleFormationPrec) {
        this.libelleFormationPrec = libelleFormationPrec;
    }

    public String getEtablissement() {
        return etablissement;
    }

    public void setEtablissement(String etablissement) {
        this.etablissement = etablissement;
    }

    public List<String> getCampus() {
        return campus;
    }

    public void setCampus(List<String> campus) {
        this.campus = campus;
    }

    public String getFormAddress() {
        return formAddress;
    }

    public void setFormAddress(String formAddress) {
        this.formAddress = formAddress;
    }

    public String getNoteS1() {
        return noteS1;
    }

    public void setNoteS1(String noteS1) {
        this.noteS1 = noteS1;
    }

    public String getResultatS1() {
        return resultatS1;
    }

    public void setResultatS1(String resultatS1) {
        this.resultatS1 = resultatS1;
    }

    public String getNoteS2() {
        return noteS2;
    }

    public void setNoteS2(String noteS2) {
        this.noteS2 = noteS2;
    }

    public String getResultatS2() {
        return resultatS2;
    }

    public void setResultatS2(String resultatS2) {
        this.resultatS2 = resultatS2;
    }

    public String getNoteAnn() {
        return noteAnn;
    }

    public void setNoteAnn(String noteAnn) {
        this.noteAnn = noteAnn;
    }

    public String getResultatAnn() {
        return resultatAnn;
    }

    public void setResultatAnn(String resultatAnn) {
        this.resultatAnn = resultatAnn;
    }

    public Boolean getHasScholarship() {
        return hasScholarship;
    }

    public void setHasScholarship(Boolean hasScholarship) {
        this.hasScholarship = hasScholarship;
    }
}
