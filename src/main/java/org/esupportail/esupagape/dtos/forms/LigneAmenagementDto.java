package org.esupportail.esupagape.dtos.forms;

import org.esupportail.esupagape.entity.enums.StatutLigneAmenagement;

public class LigneAmenagementDto {

    private Long id;
    private Long typeLigneAmenagementId;
    private boolean selected;
    private String libelleLibre;
    private String libelle;
    private boolean champLibre;
    private StatutLigneAmenagement statut;
    private String commentairePrecision;
    private String commentaireValidation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTypeLigneAmenagementId() {
        return typeLigneAmenagementId;
    }

    public void setTypeLigneAmenagementId(Long typeLigneAmenagementId) {
        this.typeLigneAmenagementId = typeLigneAmenagementId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getLibelleLibre() {
        return libelleLibre;
    }

    public void setLibelleLibre(String libelleLibre) {
        this.libelleLibre = libelleLibre;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public boolean isChampLibre() {
        return champLibre;
    }

    public void setChampLibre(boolean champLibre) {
        this.champLibre = champLibre;
    }

    public StatutLigneAmenagement getStatut() {
        return statut;
    }

    public void setStatut(StatutLigneAmenagement statut) {
        this.statut = statut;
    }

    public String getCommentairePrecision() {
        return commentairePrecision;
    }

    public void setCommentairePrecision(String commentairePrecision) {
        this.commentairePrecision = commentairePrecision;
    }

    public String getCommentaireValidation() {
        return commentaireValidation;
    }

    public void setCommentaireValidation(String commentaireValidation) {
        this.commentaireValidation = commentaireValidation;
    }
}