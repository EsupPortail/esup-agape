package org.esupportail.esupagape.entity;

import jakarta.persistence.*;
import org.esupportail.esupagape.entity.enums.FonctionAidant;
import org.esupportail.esupagape.entity.enums.StatusAideHumaine;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class AideHumaine {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @ManyToOne(optional = false)
    private Dossier dossier;

    @Enumerated(EnumType.STRING)
    private StatusAideHumaine statusAideHumaine = StatusAideHumaine.EN_COURS;

    @ManyToOne
    private Aidant aidant = new Aidant();

    @Transient
    transient private String numAidant;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startDate;

    @ElementCollection(targetClass = FonctionAidant.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<FonctionAidant> fonctionAidants = new HashSet<>();

    @OneToMany(mappedBy = "aideHumaine", cascade = CascadeType.REMOVE)
    private List<PeriodeAideHumaine> periodeAideHumaines = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Document> piecesJointes;

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

    public StatusAideHumaine getStatusAideHumaine() {
        return statusAideHumaine;
    }

    public void setStatusAideHumaine(StatusAideHumaine statusAideHumaine) {
        this.statusAideHumaine = statusAideHumaine;
    }

    public Aidant getAidant() {
        return aidant;
    }

    public void setAidant(Aidant aidant) {
        this.aidant = aidant;
    }

    public String getNumAidant() {
        if(aidant != null && StringUtils.hasText(aidant.getNumEtuAidant())) return aidant.getNumEtuAidant();
        return numAidant;
    }

    public void setNumAidant(String numAidant) {
        this.numAidant = numAidant;
    }

    public String getNumEtuAidant() {
        return aidant.getNumEtuAidant();
    }
//
//    public void setNumEtuAidant(String numEtuAidant) {
//        this.numEtuAidant = numEtuAidant;
//    }

    public String getNameAidant() {
        return aidant.getNameAidant();
    }

//    public void setNameAidant(String nameAidant) {
//        this.nameAidant = nameAidant;
//    }

    public String getFirstNameAidant() {
        return aidant.getFirstNameAidant();
    }

//    public void setFirstNameAidant(String firstNameAidant) {
//        this.firstNameAidant = firstNameAidant;
//    }

    public LocalDate getDateOfBirthAidant() {
        return aidant.getDateOfBirthAidant();
    }

//    public void setDateOfBirthAidant(LocalDate dateOfBirthAidant) {
//        this.dateOfBirthAidant = dateOfBirthAidant;
//    }

    public String getEmailAidant() {
        return aidant.getEmailAidant();
    }

//    public void setEmailAidant(String emailAidant) {
//        this.emailAidant = emailAidant;
//    }

    public String getPhoneAidant() {
        return aidant.getPhoneAidant();
    }

//    public void setPhoneAidant(String phoneAidant) {
//        this.phoneAidant = phoneAidant;
//    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public Set<FonctionAidant> getFonctionAidants() {
        return fonctionAidants;
    }

    public void setFonctionAidants(Set<FonctionAidant> fonctionAidant) {
        this.fonctionAidants = fonctionAidant;
    }

    public List<PeriodeAideHumaine> getPeriodeAideHumaines() {
        return periodeAideHumaines;
    }

    public void setPeriodeAideHumaines(List<PeriodeAideHumaine> periodeAideHumaines) {
        this.periodeAideHumaines = periodeAideHumaines;
    }

    public List<Document> getPiecesJointes() {
        return piecesJointes;
    }

    public void setPiecesJointes(List<Document> piecesJointes) {
        this.piecesJointes = piecesJointes;
    }
}
