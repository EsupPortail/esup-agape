package org.esupportail.esupagape.entity;

import org.esupportail.esupagape.entity.enums.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Amenagement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Dossier dossier;

    @Enumerated(EnumType.STRING)
    private StatusAmenagement statusAmenagement = StatusAmenagement.BROUILLON;

    private Boolean mailIndividu = false;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private TypeAmenagement typeAmenagement;

    @ElementCollection(targetClass = TypeEpreuve.class)
    @Enumerated(EnumType.STRING)
    private List<TypeEpreuve> typeEpreuve = new ArrayList<>();

    private String autresTypeEpreuve;

    @Enumerated(EnumType.STRING)
    private TempMajore tempMajore;

    private String autresTempMajore;

    @Column(columnDefinition = "TEXT")
    private String amenagement;

    @Enumerated(EnumType.STRING)
    private Classification classification;

    private String mailMedecin;

    private String nomMedecin;

    private String mailValideur;

    private String nomValideur;

    @Column(columnDefinition = "TEXT")
    private String motifRefus;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createDate = LocalDateTime.now();

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime valideMedecinDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime administrationDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime deleteDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Dossier getDossier() {
        return dossier;
    }

    public void setDossier(Dossier dossier) {
        this.dossier = dossier;
    }

    public StatusAmenagement getStatusAmenagement() {
        return statusAmenagement;
    }

    public void setStatusAmenagement(StatusAmenagement statusAmenagement) {
        this.statusAmenagement = statusAmenagement;
    }

    public Boolean getMailIndividu() {
        return mailIndividu;
    }

    public void setMailIndividu(Boolean mailIndividu) {
        this.mailIndividu = mailIndividu;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public TypeAmenagement getTypeAmenagement() {
        return typeAmenagement;
    }

    public void setTypeAmenagement(TypeAmenagement typeAmenagement) {
        this.typeAmenagement = typeAmenagement;
    }

    public List<TypeEpreuve> getTypeEpreuve() {
        return typeEpreuve;
    }

    public void setTypeEpreuve(List<TypeEpreuve> typeEpreuve) {
        this.typeEpreuve = typeEpreuve;
    }

    public String getAutresTypeEpreuve() {
        return autresTypeEpreuve;
    }

    public void setAutresTypeEpreuve(String autresTypeEpreuve) {
        this.autresTypeEpreuve = autresTypeEpreuve;
    }

    public TempMajore getTempMajore() {
        return tempMajore;
    }

    public void setTempMajore(TempMajore tempMajore) {
        this.tempMajore = tempMajore;
    }

    public String getAutresTempMajore() {
        return autresTempMajore;
    }

    public void setAutresTempMajore(String autresTempMajore) {
        this.autresTempMajore = autresTempMajore;
    }

    public String getAmenagement() {
        return amenagement;
    }

    public void setAmenagement(String amenagement) {
        this.amenagement = amenagement;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public String getMailMedecin() {
        return mailMedecin;
    }

    public void setMailMedecin(String mailMedecin) {
        this.mailMedecin = mailMedecin;
    }

    public String getNomMedecin() {
        return nomMedecin;
    }

    public void setNomMedecin(String nomMedecin) {
        this.nomMedecin = nomMedecin;
    }

    public String getMailValideur() {
        return mailValideur;
    }

    public void setMailValideur(String mailValideur) {
        this.mailValideur = mailValideur;
    }

    public String getNomValideur() {
        return nomValideur;
    }

    public void setNomValideur(String nomValideur) {
        this.nomValideur = nomValideur;
    }

    public String getMotifRefus() {
        return motifRefus;
    }

    public void setMotifRefus(String motifRefus) {
        this.motifRefus = motifRefus;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getValideMedecinDate() {
        return valideMedecinDate;
    }

    public void setValideMedecinDate(LocalDateTime valideMedecinDate) {
        this.valideMedecinDate = valideMedecinDate;
    }

    public LocalDateTime getAdministrationDate() {
        return administrationDate;
    }

    public void setAdministrationDate(LocalDateTime administrationDate) {
        this.administrationDate = administrationDate;
    }

    public LocalDateTime getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(LocalDateTime deleteDate) {
        this.deleteDate = deleteDate;
    }
}
