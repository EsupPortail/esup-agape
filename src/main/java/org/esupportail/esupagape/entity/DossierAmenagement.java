package org.esupportail.esupagape.entity;

import jakarta.persistence.*;
import org.esupportail.esupagape.entity.enums.StatusDossierAmenagement;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
public class DossierAmenagement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", allocationSize = 1)
    private Long id;

    private Integer lastYear;

    @ManyToOne
    private Dossier dossier;

    @ManyToOne
    private Amenagement amenagement;

    @Enumerated(EnumType.STRING)
    private StatusDossierAmenagement statusDossierAmenagement;

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
        return statusDossierAmenagement;
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
