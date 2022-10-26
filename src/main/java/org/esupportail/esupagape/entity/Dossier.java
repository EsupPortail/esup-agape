package org.esupportail.esupagape.entity;

import org.esupportail.esupagape.entity.enums.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"individu_id", "year"})
})
public class Dossier {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int year;

    @Enumerated(EnumType.STRING)
    private StatusDossier statusDossier;

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

    @Enumerated(EnumType.STRING)
    private TypeIndividu type = TypeIndividu.INCONNU;

    private int taux;

    private String commentaire;

    private String libelleFormation;

    private String site;

    private String filliere;

    private String composante;

    private String resultatS1;

    private double noteS1;

    private String resultatS2;

    private double noteS2;

    private String resultatTotal;

    private double noteTotal;

    private String suiviHandisup;

    @ManyToOne
    private Individu individu;

    @OneToMany(mappedBy = "dossier", cascade = CascadeType.REMOVE)
    private List<Entretien> entretiens = new ArrayList<>();

    @OneToMany(mappedBy = "dossier", cascade = CascadeType.REMOVE)
    private List<Document> documents = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public StatusDossier getStatusDossier() {
        return statusDossier;
    }

    public void setStatusDossier(StatusDossier statusDossier) {
        this.statusDossier = statusDossier;
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

    public TypeIndividu getType() {
        return type;
    }

    public void setType(TypeIndividu type) {
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

    public String getResultatS1() {
        return resultatS1;
    }

    public void setResultatS1(String resultatS1) {
        this.resultatS1 = resultatS1;
    }

    public double getNoteS1() {
        return noteS1;
    }

    public void setNoteS1(double noteS1) {
        this.noteS1 = noteS1;
    }

    public String getResultatS2() {
        return resultatS2;
    }

    public void setResultatS2(String resultatS2) {
        this.resultatS2 = resultatS2;
    }

    public double getNoteS2() {
        return noteS2;
    }

    public void setNoteS2(double noteS2) {
        this.noteS2 = noteS2;
    }

    public String getResultatTotal() {
        return resultatTotal;
    }

    public void setResultatTotal(String resultatTotal) {
        this.resultatTotal = resultatTotal;
    }

    public double getNoteTotal() {
        return noteTotal;
    }

    public void setNoteTotal(double noteTotal) {
        this.noteTotal = noteTotal;
    }

    public String getSuiviHandisup() {
        return suiviHandisup;
    }

    public void setSuiviHandisup(String suiviHandisup) {
        this.suiviHandisup = suiviHandisup;
    }

    public Individu getIndividu() {
        return individu;
    }

    public void setIndividu(Individu individu) {
        this.individu = individu;
    }

    public List<Entretien> getEntretiens() {
        return entretiens;
    }

    public void setEntretiens(List<Entretien> entretiens) {
        this.entretiens = entretiens;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
