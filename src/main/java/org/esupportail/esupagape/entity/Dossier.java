package org.esupportail.esupagape.entity;

import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.entity.enums.enquete.ModFrmn;
import org.esupportail.esupagape.entity.enums.enquete.TypFrmn;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"individu_id", "year"})})
public class Dossier {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", allocationSize = 1)
    private Long id;

    private Integer year;

    @Enumerated(EnumType.STRING)
    private StatusDossier statusDossier;

    @Enumerated(EnumType.STRING)
    private Autorisation autorisation;

    @ElementCollection(targetClass = Classification.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Classification> classifications = new HashSet<>();

    @ElementCollection(targetClass = TypeSuiviHandisup.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<TypeSuiviHandisup> typeSuiviHandisup = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Etat etat;

    @ElementCollection(targetClass = Mdph.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
//TODO plusieurs possible + synchro avec l'enquete
    private Set<Mdph> mdphs = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private RentreeProchaine rentreeProchaine;

    @Enumerated(EnumType.STRING)
    private TypeIndividu type;

    @Enumerated(EnumType.STRING)
    private Taux taux;

    @Enumerated(EnumType.STRING)
    private TypFrmn typeFormation;

    @Enumerated(EnumType.STRING)
    private ModFrmn modeFormation;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    private String libelleFormation;

    private String libelleFormationPrec;

    private String niveauEtudes;

    private String secteurDisciplinaire;

    private String formAddress;

    private String campus;

    private String codComposante;

    private String composante;

    private String resultatTotal;

    private Boolean suiviHandisup;

    private Boolean employee;

    private Boolean alternance;

    private Boolean hasScholarship;

    private Boolean atypie;
    @ManyToOne
    private Individu individu;

    @OneToOne(mappedBy = "dossier", cascade = CascadeType.REMOVE)
    private Enquete enquete;

    @OneToMany(mappedBy = "dossier", cascade = CascadeType.REMOVE)
    private List<Entretien> entretiens = new ArrayList<>();

    @OneToMany(mappedBy = "dossier", cascade = CascadeType.REMOVE)
    private List<AideMaterielle> aidesMaterielles = new ArrayList<>();

    @OneToMany(mappedBy = "dossier", cascade = CascadeType.REMOVE)
    private List<AideHumaine> aidesHumaines = new ArrayList<>();

    @OneToMany(mappedBy = "dossier", cascade = CascadeType.REMOVE)
    private List<Document> documents = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> attachments;

    private Boolean newDossier = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
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

    public Set<Classification> getClassifications() {
        return classifications;
    }

    public void setClassifications(Set<Classification> classifications) {
        this.classifications = classifications;
    }

    public Set<TypeSuiviHandisup> getTypeSuiviHandisup() {
        return typeSuiviHandisup;
    }

    public void setTypeSuiviHandisup(Set<TypeSuiviHandisup> typeSuiviHandisup) {
        this.typeSuiviHandisup = typeSuiviHandisup;
    }

    public Etat getEtat() {
        return etat;
    }

    public void setEtat(Etat etat) {
        this.etat = etat;
    }

    public Set<Mdph> getMdphs() {
        return mdphs;
    }

    public void setMdphs(Set<Mdph> mdphs) {
        this.mdphs = mdphs;
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

    public TypFrmn getTypeFormation() {
        return typeFormation;
    }

    public void setTypeFormation(TypFrmn typeFormation) {
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

    public String getLibelleFormationPrec() {
        return libelleFormationPrec;
    }

    public void setLibelleFormationPrec(String libelleFormationPrec) {
        this.libelleFormationPrec = libelleFormationPrec;
    }

    public String getNiveauEtudes() {
        return niveauEtudes;
    }

    public void setNiveauEtudes(String niveauEtudes) {
        this.niveauEtudes = niveauEtudes;
    }

    public String getSecteurDisciplinaire() {
        return secteurDisciplinaire;
    }

    public void setSecteurDisciplinaire(String secteurDisciplinaire) {
        this.secteurDisciplinaire = secteurDisciplinaire;
    }

    public String getFormAddress() {
        return formAddress;
    }

    public void setFormAddress(String formAddress) {
        this.formAddress = formAddress;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getCodComposante() {
        return codComposante;
    }

    public void setCodComposante(String codComposante) {
        this.codComposante = codComposante;
    }

    public String getComposante() {
        return composante;
    }

    public void setComposante(String composante) {
        this.composante = composante;
    }

    public String getResultatTotal() {
        return resultatTotal;
    }

    public void setResultatTotal(String resultatTotal) {
        this.resultatTotal = resultatTotal;
    }

    public Boolean getSuiviHandisup() {
        return suiviHandisup;
    }

    public void setSuiviHandisup(Boolean suiviHandisup) {
        this.suiviHandisup = suiviHandisup;
    }

    public Boolean getEmployee() {
        return employee;
    }

    public void setEmployee(Boolean employee) {
        this.employee = employee;
    }

    public Boolean getAlternance() {
        return alternance;
    }

    public void setAlternance(Boolean alternance) {
        this.alternance = alternance;
    }

    public Boolean getHasScholarship() {
        return hasScholarship;
    }

    public void setHasScholarship(Boolean hasScholarship) {
        this.hasScholarship = hasScholarship;
    }

    public Boolean getAtypie() {
        return atypie;
    }

    public void setAtypie(Boolean atypie) {
        this.atypie = atypie;
    }

    public Individu getIndividu() {
        return individu;
    }

    public void setIndividu(Individu individu) {
        this.individu = individu;
    }

    public Enquete getEnquete() {
        return enquete;
    }

    public void setEnquete(Enquete enquete) {
        this.enquete = enquete;
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

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<Document> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Document> attachments) {
        this.attachments = attachments;
    }

    public Boolean getNewDossier() {
        return newDossier;
    }

    public void setNewDossier(Boolean newDossier) {
        this.newDossier = newDossier;
    }

}
