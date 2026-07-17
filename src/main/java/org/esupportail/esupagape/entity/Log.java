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

    private String userDisplayName;

    private String entityType;

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

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
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

    public String getInitialStatusDossierLabel() {
        return cleanStatus(initialStatusDossier);
    }

    public void setInitialStatusDossier(String initialStatusDossier) {
        this.initialStatusDossier = initialStatusDossier;
    }

    public String getFinalStatusDossier() {
        return finalStatusDossier;
    }

    public String getFinalStatusDossierLabel() {
        return cleanStatus(finalStatusDossier);
    }

    public void setFinalStatusDossier(String finalStatusDossier) {
        this.finalStatusDossier = finalStatusDossier;
    }

    private String cleanStatus(String status) {
        if (status != null && status.startsWith("org.esupportail.esupagape.entity.") && status.contains("@")) {
            return "";
        }
        return status;
    }
}
