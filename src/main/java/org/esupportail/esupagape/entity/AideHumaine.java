package org.esupportail.esupagape.entity;

import org.esupportail.esupagape.entity.enums.FonctionAidant;
import org.esupportail.esupagape.entity.enums.StatusAideHumaine;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
public class AideHumaine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    private Dossier dossier;

    @Enumerated(EnumType.STRING)
    private StatusAideHumaine statusAideHumaine = StatusAideHumaine.EN_COURS;

    private String numEtuAidant;

    @NotNull
    private String nameAidant;

    @NotNull
    private String firstNameAidant;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirthAidant;

    private String emailAidant;

    private String phoneAidant;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startDate;

    @ElementCollection(targetClass = FonctionAidant.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<FonctionAidant> fonctionAidants;

    @OneToMany(mappedBy = "aideHumaine", cascade = CascadeType.REMOVE)
    private List<PeriodeAideHumaine> periodeAideHumaines = new ArrayList<>();

    @OneToMany(mappedBy = "parentId", cascade = CascadeType.REMOVE)
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



    public String getNumEtuAidant() {
        return numEtuAidant;
    }

    public void setNumEtuAidant(String numEtuAidant) {
        this.numEtuAidant = numEtuAidant;
    }

    public String getNameAidant() {
        return nameAidant;
    }

    public void setNameAidant(String nameAidant) {
        this.nameAidant = nameAidant;
    }

    public String getFirstNameAidant() {
        return firstNameAidant;
    }

    public void setFirstNameAidant(String firstNameAidant) {
        this.firstNameAidant = firstNameAidant;
    }

    public LocalDate getDateOfBirthAidant() {
        return dateOfBirthAidant;
    }

    public void setDateOfBirthAidant(LocalDate dateOfBirthAidant) {
        this.dateOfBirthAidant = dateOfBirthAidant;
    }

    public String getEmailAidant() {
        return emailAidant;
    }

    public void setEmailAidant(String emailAidant) {
        this.emailAidant = emailAidant;
    }

    public String getPhoneAidant() {
        return phoneAidant;
    }

    public void setPhoneAidant(String phoneAidant) {
        this.phoneAidant = phoneAidant;
    }

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
