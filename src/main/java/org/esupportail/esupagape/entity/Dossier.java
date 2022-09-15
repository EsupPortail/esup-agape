package org.esupportail.esupagape.entity;

import org.esupportail.esupagape.entity.enums.Classification;
import org.esupportail.esupagape.entity.enums.DetailSuiviHandisup;
import org.esupportail.esupagape.entity.enums.Etat;
import org.esupportail.esupagape.entity.enums.Mdph;
import org.esupportail.esupagape.entity.enums.RentreeProchaine;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Dossier {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)

    private Long id;

    @Enumerated(EnumType.STRING)
    private Classification classification;

    @Enumerated(EnumType.STRING)
    private DetailSuiviHandisup detailSuiviHandisup;

    @Enumerated(EnumType.STRING)
    private Etat etat;

    @Enumerated(EnumType.STRING)
    private Mdph mdph;

    @Enumerated(EnumType.STRING)
    private RentreeProchaine rentreeProchaine;

    private int year;

    private String type;

    private int taux;

    private String commentaire;

    private String libelleFormation;

    private String site;

    private String filliere;

    private String composante;

    private double resultatS1;

    private double noteS1;

    private double resultatS2;

    private double noteS2;

    private double resultatTotal;

    private double noteTotal;

    private String suiviHandisup;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idIndividu")
    private Individu individu;

    public Dossier(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @ManyToMany()
    @JoinTable(name = "dossier_contact",
            joinColumns = @JoinColumn(name = "dossier_id"),
            inverseJoinColumns = @JoinColumn(name = "contact_id")
    )
    private List<Contact> contacts = new ArrayList<>();

    public Dossier(Individu individu) {
        this.individu = individu;
    }

    public Individu getIndividu() {
        return individu;
    }

    public void setIndividu(Individu individu) {
        this.individu = individu;
    }

    public Dossier() {

    }

    public Dossier(Long id, Classification classification, DetailSuiviHandisup detailSuiviHandisup, Etat etat, Mdph mdph, RentreeProchaine rentreeProchaine, int year, String type, int taux, String commentaire, String libelleFormation, String site, String filliere, String composante, double resultatS1, double noteS1, double resultatS2, double noteS2, double resultatTotal, double noteTotal, String suiviHandisup) {
        this.id = id;
        this.classification = classification;
        this.detailSuiviHandisup = detailSuiviHandisup;
        this.etat = etat;
        this.mdph = mdph;
        this.rentreeProchaine = rentreeProchaine;
        this.year = year;
        this.type = type;
        this.taux = taux;
        this.commentaire = commentaire;
        this.libelleFormation = libelleFormation;
        this.site = site;
        this.filliere = filliere;
        this.composante = composante;
        this.resultatS1 = resultatS1;
        this.noteS1 = noteS1;
        this.resultatS2 = resultatS2;
        this.noteS2 = noteS2;
        this.resultatTotal = resultatTotal;
        this.noteTotal = noteTotal;
        this.suiviHandisup = suiviHandisup;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public DetailSuiviHandisup getDetailSuiviHandisup() {
        return detailSuiviHandisup;
    }

    public void setDetailSuiviHandisup(DetailSuiviHandisup detailSuiviHandisup) {
        this.detailSuiviHandisup = detailSuiviHandisup;
    }

    public Etat getEtat() {
        return etat;
    }

    public void setEtat(Etat etat) {
        this.etat = etat;
    }

    public Mdph getMdph() {
        return mdph;
    }

    public void setMdph(Mdph mdph) {
        this.mdph = mdph;
    }

    public RentreeProchaine getRentreeProchaine() {
        return rentreeProchaine;
    }

    public void setRentreeProchaine(RentreeProchaine rentreeProchaine) {
        this.rentreeProchaine = rentreeProchaine;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTaux() {
        return taux;
    }

    public void setTaux(int taux) {
        this.taux = taux;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getLibelleFormation() {
        return libelleFormation;
    }

    public void setLibelleFormation(String libelleFormation) {
        this.libelleFormation = libelleFormation;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getFilliere() {
        return filliere;
    }

    public void setFilliere(String filliere) {
        this.filliere = filliere;
    }

    public String getComposante() {
        return composante;
    }

    public void setComposante(String composante) {
        this.composante = composante;
    }

    public double getResultatS1() {
        return resultatS1;
    }

    public void setResultatS1(double resultatS1) {
        this.resultatS1 = resultatS1;
    }

    public double getNoteS1() {
        return noteS1;
    }

    public void setNoteS1(double noteS1) {
        this.noteS1 = noteS1;
    }
}
