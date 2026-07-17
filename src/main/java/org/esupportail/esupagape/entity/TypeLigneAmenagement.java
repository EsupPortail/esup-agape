package org.esupportail.esupagape.entity;

import jakarta.persistence.*;

@Entity
public class TypeLigneAmenagement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String libelle;

    private Integer ordre;

    private Integer year;

    private boolean champLibre = false;

    private boolean actif = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public boolean isChampLibre() {
        return champLibre;
    }

    public void setChampLibre(boolean champLibre) {
        this.champLibre = champLibre;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }
}
