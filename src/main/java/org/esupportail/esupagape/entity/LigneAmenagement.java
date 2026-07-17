package org.esupportail.esupagape.entity;

import jakarta.persistence.*;
import org.esupportail.esupagape.entity.enums.StatutLigneAmenagement;

@Entity
public class LigneAmenagement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amenagement_id")
    private Amenagement amenagement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_ligne_amenagement_id")
    private TypeLigneAmenagement typeLigneAmenagement;

    @Column(columnDefinition = "TEXT")
    private String libelleLibre;

    @Enumerated(EnumType.STRING)
    private StatutLigneAmenagement statut;

    @Column(columnDefinition = "TEXT")
    private String commentairePrecision;

    @Column(columnDefinition = "TEXT")
    private String commentaireValidation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Amenagement getAmenagement() {
        return amenagement;
    }

    public void setAmenagement(Amenagement amenagement) {
        this.amenagement = amenagement;
    }

    public TypeLigneAmenagement getTypeLigneAmenagement() {
        return typeLigneAmenagement;
    }

    public void setTypeLigneAmenagement(TypeLigneAmenagement typeLigneAmenagement) {
        this.typeLigneAmenagement = typeLigneAmenagement;
    }

    public String getLibelleLibre() {
        return libelleLibre;
    }

    public void setLibelleLibre(String libelleLibre) {
        this.libelleLibre = libelleLibre;
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