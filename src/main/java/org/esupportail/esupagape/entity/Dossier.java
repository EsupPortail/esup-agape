package org.esupportail.esupagape.entity;

import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.entity.enums.enquete.ModFrmn;
import org.esupportail.esupagape.entity.enums.enquete.TypeFrmn;

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
    private Autorisation autorisation;

    @Enumerated(EnumType.STRING)
    private Classification classification;

    @ElementCollection(targetClass = TypeSuiviHandisup.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<TypeSuiviHandisup> typeSuiviHandisup;

    @Enumerated(EnumType.STRING)
    private Etat etat;

    @Enumerated(EnumType.STRING)
    private Mdph mdph;

    @Enumerated(EnumType.STRING)
    private RentreeProchaine rentreeProchaine;

    @Enumerated(EnumType.STRING)
    private TypeIndividu type = TypeIndividu.INCONNU;

    @Enumerated(EnumType.STRING)
    private Taux taux;

    @Enumerated(EnumType.STRING)
    private TypeFrmn typeFormation;

    @Enumerated(EnumType.STRING)
    private ModFrmn modeFormation;

    private String commentaire;

    private String libelleFormation;

    private String site;

    private String formAddress;

    private String composante;

    private String resultatS1;

    private double noteS1;

    private String resultatS2;

    private double noteS2;

    private String resultatTotal;

    private double noteTotal;

    private Boolean suiviHandisup;

    @ManyToOne
    private Individu individu;

    @OneToMany(mappedBy = "dossier", cascade = CascadeType.REMOVE)
    private List<Entretien> entretiens = new ArrayList<>();

    @OneToMany(mappedBy = "dossier", cascade = CascadeType.REMOVE)
    private List<AideMaterielle> aidesMaterielles = new ArrayList<>();

    @OneToMany(mappedBy = "dossier", cascade = CascadeType.REMOVE)
    private List<AideHumaine> aidesHumaines = new ArrayList<>();

    @OneToMany(mappedBy = "dossier", cascade = CascadeType.REMOVE)
    private List<Amenagement> amenagements = new ArrayList<>();

    @OneToMany(mappedBy = "dossier", cascade = CascadeType.REMOVE)
    private List<Document> documents = new ArrayList<>();

    @OneToOne(cascade = CascadeType.REMOVE)
    private Enquete enquete;

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

    public Autorisation getAutorisation() {
        return autorisation;
    }

    public void setAutorisation(Autorisation autorisation) {
        this.autorisation = autorisation;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public List<TypeSuiviHandisup> getTypeSuiviHandisup() {
        return typeSuiviHandisup;
    }

    public void setTypeSuiviHandisup(List<TypeSuiviHandisup> detailSuiviHandisup) {
        this.typeSuiviHandisup = detailSuiviHandisup;
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

    public Taux getTaux() {
        return taux;
    }

    public void setTaux(Taux taux) {
        this.taux = taux;
    }

    public TypeFrmn getTypeFormation() {
        return typeFormation;
    }

    public void setTypeFormation(TypeFrmn typeFormation) {
        this.typeFormation = typeFormation;
    }

    public ModFrmn getModeFormation() {
        return modeFormation;
    }

    public void setModeFormation(ModFrmn modeFormation) {
        this.modeFormation = modeFormation;
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

    public String getFormAddress() {
        return formAddress;
    }

    public void setFormAddress(String formAddress) {
        this.formAddress = formAddress;
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

    public Boolean getSuiviHandisup() {
        return suiviHandisup;
    }

    public void setSuiviHandisup(Boolean suiviHandisup) {
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

    public List<AideMaterielle> getAidesMaterielles() {
        return aidesMaterielles;
    }

    public void setAidesMaterielles(List<AideMaterielle> aidesMaterielles) {
        this.aidesMaterielles = aidesMaterielles;
    }

    public List<AideHumaine> getAidesHumaines() {
        return aidesHumaines;
    }

    public void setAidesHumaines(List<AideHumaine> aidesHumaines) {
        this.aidesHumaines = aidesHumaines;
    }

    public List<Amenagement> getAmenagements() {
        return amenagements;
    }

    public void setAmenagements(List<Amenagement> amenagements) {
        this.amenagements = amenagements;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public Enquete getEnquete() {
        return enquete;
    }

    public void setEnquete(Enquete enquete) {
        this.enquete = enquete;
    }
}
