package org.esupportail.esupagape.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    private Long dossierId;

    private String eppn;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime date;

    private String initialStatusDossier;

    private String finalStatusDossier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDossierId() {
        return dossierId;
    }

    public void setDossierId(Long dossierId) {
        this.dossierId = dossierId;
    }

    public String getEppn() {
        return eppn;
    }

    public void setEppn(String eppn) {
        this.eppn = eppn;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getInitialStatusDossier() {
        return initialStatusDossier;
    }

    public void setInitialStatusDossier(String initialStatusDossier) {
        this.initialStatusDossier = initialStatusDossier;
    }

    public String getFinalStatusDossier() {
        return finalStatusDossier;
    }

    public void setFinalStatusDossier(String finalStatusDossier) {
        this.finalStatusDossier = finalStatusDossier;
    }
}
