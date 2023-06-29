package org.esupportail.esupagape.dtos.forms;

import org.esupportail.esupagape.entity.enums.Classification;
import org.esupportail.esupagape.entity.enums.FonctionAidant;
import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.entity.enums.Mdph;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.StatusDossierAmenagement;
import org.esupportail.esupagape.entity.enums.TypeAideMaterielle;
import org.esupportail.esupagape.entity.enums.TypeIndividu;
import org.esupportail.esupagape.entity.enums.enquete.ModFrmn;
import org.esupportail.esupagape.entity.enums.enquete.TypFrmn;

import java.util.List;

public class DossierFilter {

    private List<Integer> year;
    private Boolean newDossier;
    private List<TypeIndividu> type;
    private List<Gender> gender;
    private List<Integer> yearOfBirth;
    private List<String> fixCP;
    private List<StatusDossier> statusDossier;
    private List<StatusDossierAmenagement> statusDossierAmenagement;
    private List<Mdph> mdph;
    private Boolean suiviHandisup;
    private Boolean finished;
    private List<TypFrmn> typFrmn;
    private List<ModFrmn> modFrmn;
    private List<Classification> classifications;
    private List<String> composante;
    private List<String> secteurDisciplinaire;
    private List<String> libelleFormation;
    private List<String> niveauEtudes;
    private String resultatTotal;
    private List<TypeAideMaterielle> typeAideMaterielle;
    private List<FonctionAidant> fonctionAidants;

    public List<Integer> getYear() {
        return year;
    }

    public void setYear(List<Integer> year) {
        this.year = year;
    }

    public Boolean getNewDossier() {
        return newDossier;
    }

    public void setNewDossier(Boolean newDossier) {
        this.newDossier = newDossier;
    }

    public List<TypeIndividu> getType() {
        return type;
    }

    public void setType(List<TypeIndividu> type) {
        this.type = type;
    }

    public List<Gender> getGender() {
        return gender;
    }

    public void setGender(List<Gender> gender) {
        this.gender = gender;
    }

    public List<Integer> getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(List<Integer> yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public List<String> getFixCP() {
        return fixCP;
    }

    public void setFixCP(List<String> fixCP) {
        this.fixCP = fixCP;
    }

    public List<StatusDossier> getStatusDossier() {
        return statusDossier;
    }

    public void setStatusDossier(List<StatusDossier> statusDossier) {
        this.statusDossier = statusDossier;
    }

    public List<StatusDossierAmenagement> getStatusDossierAmenagement() {
        return statusDossierAmenagement;
    }

    public void setStatusDossierAmenagement(List<StatusDossierAmenagement> statusDossierAmenagement) {
        this.statusDossierAmenagement = statusDossierAmenagement;
    }

    public List<Mdph> getMdph() {
        return mdph;
    }

    public void setMdph(List<Mdph> mdph) {
        this.mdph = mdph;
    }

    public Boolean getSuiviHandisup() {
        return suiviHandisup;
    }

    public void setSuiviHandisup(Boolean suiviHandisup) {
        this.suiviHandisup = suiviHandisup;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public List<TypFrmn> getTypFrmn() {
        return typFrmn;
    }

    public void setTypFrmn(List<TypFrmn> typFrmn) {
        this.typFrmn = typFrmn;
    }

    public List<ModFrmn> getModFrmn() {
        return modFrmn;
    }

    public void setModFrmn(List<ModFrmn> modFrmn) {
        this.modFrmn = modFrmn;
    }

    public List<Classification> getClassifications() {
        return classifications;
    }

    public void setClassifications(List<Classification> classifications) {
        this.classifications = classifications;
    }

    public List<String> getComposante() {
        return composante;
    }

    public void setComposante(List<String> composante) {
        this.composante = composante;
    }

    public List<String> getSecteurDisciplinaire() {
        return secteurDisciplinaire;
    }

    public void setSecteurDisciplinaire(List<String> secteurDisciplinaire) {
        this.secteurDisciplinaire = secteurDisciplinaire;
    }

    public List<String> getLibelleFormation() {
        return libelleFormation;
    }

    public void setLibelleFormation(List<String> libelleFormation) {
        this.libelleFormation = libelleFormation;
    }

    public List<String> getNiveauEtudes() {
        return niveauEtudes;
    }

    public void setNiveauEtudes(List<String> niveauEtudes) {
        this.niveauEtudes = niveauEtudes;
    }

    public String getResultatTotal() {
        return resultatTotal;
    }

    public void setResultatTotal(String resultatTotal) {
        this.resultatTotal = resultatTotal;
    }

    public List<TypeAideMaterielle> getTypeAideMaterielle() {
        return typeAideMaterielle;
    }

    public void setTypeAideMaterielle(List<TypeAideMaterielle> typeAideMaterielle) {
        this.typeAideMaterielle = typeAideMaterielle;
    }

    public List<FonctionAidant> getFonctionAidants() {
        return fonctionAidants;
    }

    public void setFonctionAidants(List<FonctionAidant> fonctionAidants) {
        this.fonctionAidants = fonctionAidants;
    }
}
