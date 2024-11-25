package org.esupportail.esupagape.entity;

import jakarta.persistence.*;
import org.esupportail.esupagape.entity.enums.StatusDossierAmenagement;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class DossierAmenagement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", allocationSize = 1)
    private Long id;

    private Integer lastYear;

    @ManyToOne(cascade = CascadeType.ALL)
    private Dossier dossier;

    @ManyToOne(cascade = CascadeType.ALL)
    private Amenagement amenagement;

    @Enumerated(EnumType.STRING)
    private StatusDossierAmenagement statusDossierAmenagement = StatusDossierAmenagement.NON;

    private String mailValideurPortabilite;

    private String nomValideurPortabilite;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime lastUpdate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLastYear() {
        return lastYear;
    }

    public void setLastYear(Integer lastYear) {
        this.lastYear = lastYear;
    }

    public Dossier getDossier() {
        return dossier;
    }

    public void setDossier(Dossier dossier) {
        this.dossier = dossier;
    }

    public Amenagement getAmenagement() {
        return amenagement;
    }

    public void setAmenagement(Amenagement amenagement) {
        this.amenagement = amenagement;
    }

    public StatusDossierAmenagement getStatusDossierAmenagement() {
        return Objects.requireNonNullElse(statusDossierAmenagement, StatusDossierAmenagement.NON);
    }

    public void setStatusDossierAmenagement(StatusDossierAmenagement statusDossierAmenagement) {
        this.statusDossierAmenagement = statusDossierAmenagement;
    }

    public String getMailValideurPortabilite() {
        return mailValideurPortabilite;
    }

    public void setMailValideurPortabilite(String mailValideurPortabilite) {
        this.mailValideurPortabilite = mailValideurPortabilite;
    }

    public String getNomValideurPortabilite() {
        return nomValideurPortabilite;
    }

    public void setNomValideurPortabilite(String nomValideurPortabilite) {
        this.nomValideurPortabilite = nomValideurPortabilite;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
