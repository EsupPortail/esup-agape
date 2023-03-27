package org.esupportail.esupagape.dtos.forms;

import org.esupportail.esupagape.entity.enums.Classification;
import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.entity.enums.Mdph;
import org.esupportail.esupagape.entity.enums.StatusAmenagement;
import org.esupportail.esupagape.entity.enums.enquete.ModFrmn;
import org.esupportail.esupagape.entity.enums.enquete.TypFrmn;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class DossierFilters {

    private Boolean newDossier;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String fixCP;
    private StatusAmenagement statusAmenagement;
    private Mdph mdph;
    private Boolean suiviHandisup;
    private Boolean finished;
    private TypFrmn typFrmn;
    private ModFrmn modFrmn;
    private Set<Classification> classification = new HashSet<>();
    private String codComposante;
    String codFil;
    private String codFmt;
    private String codSco;
    private String resultatTotal;

    public Boolean getNewDossier() {
        return newDossier;
    }

    public void setNewDossier(Boolean newDossier) {
        this.newDossier = newDossier;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFixCP() {
        return fixCP;
    }

    public void setFixCP(String fixCP) {
        this.fixCP = fixCP;
    }

    public StatusAmenagement getStatusAmenagement() {
        return statusAmenagement;
    }

    public void setStatusAmenagement(StatusAmenagement statusAmenagement) {
        this.statusAmenagement = statusAmenagement;
    }

    public Mdph getMdph() {
        return mdph;
    }

    public void setMdph(Mdph mdph) {
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

    public TypFrmn getTypFrmn() {
        return typFrmn;
    }

    public void setTypFrmn(TypFrmn typFrmn) {
        this.typFrmn = typFrmn;
    }

    public ModFrmn getModFrmn() {
        return modFrmn;
    }

    public void setModFrmn(ModFrmn modFrmn) {
        this.modFrmn = modFrmn;
    }

    public Set<Classification> getClassification() {
        return classification;
    }

    public void setClassification(Set<Classification> classification) {
        this.classification = classification;
    }

    public String getCodComposante() {
        return codComposante;
    }

    public void setCodComposante(String codComposante) {
        this.codComposante = codComposante;
    }

    public String getCodFil() {
        return codFil;
    }

    public void setCodFil(String codFil) {
        this.codFil = codFil;
    }

    public String getCodFmt() {
        return codFmt;
    }

    public void setCodFmt(String codFmt) {
        this.codFmt = codFmt;
    }

    public String getCodSco() {
        return codSco;
    }

    public void setCodSco(String codSco) {
        this.codSco = codSco;
    }

    public String getResultatTotal() {
        return resultatTotal;
    }

    public void setResultatTotal(String resultatTotal) {
        this.resultatTotal = resultatTotal;
    }
}
