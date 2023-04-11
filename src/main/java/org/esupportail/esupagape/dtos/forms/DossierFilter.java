package org.esupportail.esupagape.dtos.forms;

import org.esupportail.esupagape.entity.enums.Classification;
import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.entity.enums.Mdph;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.StatusDossierAmenagement;
import org.esupportail.esupagape.entity.enums.TypeIndividu;
import org.esupportail.esupagape.entity.enums.enquete.ModFrmn;
import org.esupportail.esupagape.entity.enums.enquete.TypFrmn;

import java.time.LocalDate;
import java.util.List;

public class DossierFilter {

    private Integer yearFilter;
    private Boolean newDossier;
    private List<TypeIndividu> type;
    private List<Gender> gender;
    private LocalDate dateOfBirth;
    private String fixCP;
    private List<StatusDossier> statusDossier;
    private List<StatusDossierAmenagement> statusDossierAmenagement;
    private Mdph mdph;
    private Boolean suiviHandisup;
    private Boolean finished;
    private List<TypFrmn> typFrmn;
    private List<ModFrmn> modFrmn;
    private List<Classification> classifications;
    private String codComposante;
    private String codFil;
    private String codFmt;
    private String codSco;
    private String resultatTotal;

    public Integer getYearFilter() {
        return yearFilter;
    }

    public void setYearFilter(Integer yearFilter) {
        this.yearFilter = yearFilter;
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
