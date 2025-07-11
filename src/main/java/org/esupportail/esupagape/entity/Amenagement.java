package org.esupportail.esupagape.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.esupportail.esupagape.entity.enums.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Entity
public class Amenagement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @OneToMany(mappedBy = "amenagement", fetch = FetchType.EAGER)
    Set<DossierAmenagement> dossierAmenagements;

    @Enumerated(EnumType.STRING)
    private Autorisation autorisation;

    @Enumerated(EnumType.STRING)
    private StatusAmenagement statusAmenagement = StatusAmenagement.BROUILLON;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private TypeAmenagement typeAmenagement;

    @ElementCollection(targetClass = TypeEpreuve.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<TypeEpreuve> typeEpreuves = new HashSet<>();

    private String autresTypeEpreuve;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TempsMajore tempsMajore;

    private String autresTempsMajores;

    @Column(columnDefinition = "TEXT")
    private String amenagementText;

    @ElementCollection(targetClass = Classification.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Classification> classification = new HashSet<>();

    private String mailMedecin;

    private String nomMedecin;

    private String uidValideur;

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
    private LocalDateTime individuSendDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime deleteDate;

    private String avisSignatureId;

    @Enumerated(EnumType.STRING)
    private SignatureStatus avisSignatureStatus;

    private String certificatSignatureId;

    @Enumerated(EnumType.STRING)
    private SignatureStatus certificatSignatureStatus;

    @OneToOne(fetch = FetchType.LAZY)
    private Document avis;

    @OneToOne(fetch = FetchType.LAZY)
    private Document certificat;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    private Set<String> viewByUid = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<DossierAmenagement> getDossierAmenagements() {
        return dossierAmenagements;
    }

    public void setDossierAmenagements(Set<DossierAmenagement> dossierAmenagements) {
        this.dossierAmenagements = dossierAmenagements;
    }

    public Autorisation getAutorisation() {
        return autorisation;
    }

    public void setAutorisation(Autorisation autorisation) {
        this.autorisation = autorisation;
    }

    public StatusAmenagement getStatusAmenagement() {
        return statusAmenagement;
    }

    public void setStatusAmenagement(StatusAmenagement statusAmenagement) {
        this.statusAmenagement = statusAmenagement;
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

    public Set<TypeEpreuve> getTypeEpreuves() {
        return typeEpreuves;
    }

    public void setTypeEpreuves(Set<TypeEpreuve> typeEpreuves) {
        this.typeEpreuves = typeEpreuves;
    }

    public String getAutresTypeEpreuve() {
        return autresTypeEpreuve;
    }

    public void setAutresTypeEpreuve(String autresTypeEpreuve) {
        this.autresTypeEpreuve = autresTypeEpreuve;
    }

    public TempsMajore getTempsMajore() {
        return tempsMajore;
    }

    public void setTempsMajore(TempsMajore tempsMajore) {
        this.tempsMajore = tempsMajore;
    }

    public String getAutresTempsMajores() {
        return autresTempsMajores;
    }

    public void setAutresTempsMajores(String autresTempsMajores) {
        this.autresTempsMajores = autresTempsMajores;
    }

    public String getAmenagementText() {
        return amenagementText;
    }

    public void setAmenagementText(String amenagement) {
        this.amenagementText = amenagement;
    }

    public Set<Classification> getClassification() {
        return classification;
    }

    public void setClassification(Set<Classification> classification) {
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

    public String getUidValideur() {
        return uidValideur;
    }

    public void setUidValideur(String uidValideur) {
        this.uidValideur = uidValideur;
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

    public LocalDateTime getIndividuSendDate() {
        return individuSendDate;
    }

    public void setIndividuSendDate(LocalDateTime individuSendDate) {
        this.individuSendDate = individuSendDate;
    }

    public LocalDateTime getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(LocalDateTime deleteDate) {
        this.deleteDate = deleteDate;
    }

    public String getAvisSignatureId() {
        return avisSignatureId;
    }

    public void setAvisSignatureId(String avisSignatureId) {
        this.avisSignatureId = avisSignatureId;
    }

    public SignatureStatus getAvisSignatureStatus() {
        return avisSignatureStatus;
    }

    public void setAvisSignatureStatus(SignatureStatus avisSignatureStatus) {
        this.avisSignatureStatus = avisSignatureStatus;
    }

    public String getCertificatSignatureId() {
        return certificatSignatureId;
    }

    public void setCertificatSignatureId(String certificatSignatureId) {
        this.certificatSignatureId = certificatSignatureId;
    }

    public SignatureStatus getCertificatSignatureStatus() {
        return certificatSignatureStatus;
    }

    public void setCertificatSignatureStatus(SignatureStatus certificatSignatureStatus) {
        this.certificatSignatureStatus = certificatSignatureStatus;
    }

    public Document getAvis() {
        return avis;
    }

    public void setAvis(Document avis) {
        this.avis = avis;
    }

    public Document getCertificat() {
        return certificat;
    }

    public void setCertificat(Document certificat) {
        this.certificat = certificat;
    }

    public Set<String> getViewByUid() {
        return viewByUid;
    }

    public void setViewByUid(Set<String> viewByUid) {
        this.viewByUid = viewByUid;
    }

    public Dossier getDossierByYear(int year) {
        Optional<DossierAmenagement> dossierAmenagement = dossierAmenagements.stream().filter(d -> d.getLastYear() == year).findFirst();
        return dossierAmenagement.map(DossierAmenagement::getDossier).orElse(null);
    }

    public DossierAmenagement getLastDossierAmenagement() {
        return dossierAmenagements.stream().max(Comparator.comparingInt(DossierAmenagement::getLastYear)).get();
    }

}
