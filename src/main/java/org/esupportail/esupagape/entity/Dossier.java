package org.esupportail.esupagape.entity;

import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.entity.enums.enquete.ModFrmn;
import org.esupportail.esupagape.entity.enums.enquete.TypFrmn;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(
        uniqueConstraints={@UniqueConstraint(columnNames = {"individu_id", "year"})}
)
public class Dossier {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer year;

    @Enumerated(EnumType.STRING)
    private StatusDossier statusDossier;

    @Enumerated(EnumType.STRING)
    private Autorisation autorisation;

    @ElementCollection(targetClass = Classification.class)
    @Enumerated(EnumType.STRING)
    private Set<Classification> classifications;

    @ElementCollection(targetClass = TypeSuiviHandisup.class)
    @Enumerated(EnumType.STRING)
    private Set<TypeSuiviHandisup> typeSuiviHandisup;

    @Enumerated(EnumType.STRING)
    private Etat etat;

    @Enumerated(EnumType.STRING)
    private Mdph mdph;

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

    @Enumerated(EnumType.STRING)
    StatusDossierAmenagement statusDossierAmenagement;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    private String libelleFormation;

    private String libelleFormationPrec;

    private String niveauEtudes;

    private String secteurDisciplinaire;

    private String formAddress;

    private String codComposante;

    private String composante;

    private String resultatTotal;

    private Boolean suiviHandisup;

    private Boolean employee;

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
    private List<Amenagement> amenagements = new ArrayList<>();

    @ManyToOne
    private Amenagement amenagementPorte;

    private String mailValideurPortabilite;

    private String nomValideurPortabilite;

    @OneToMany(mappedBy = "dossier", cascade = CascadeType.REMOVE)
    private List<Document> documents = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> attachments;

    private Boolean newDossier = true;

    private LocalDate unsubscribeDate;

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

    public StatusDossierAmenagement getStatusDossierAmenagement() {
        return statusDossierAmenagement;
    }

    public void setStatusDossierAmenagement(StatusDossierAmenagement statusDossierAmenagement) {
        this.statusDossierAmenagement = statusDossierAmenagement;
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

    public List<Amenagement> getAmenagements() {
        return amenagements;
    }

    public void setAmenagements(List<Amenagement> amenagements) {
        this.amenagements = amenagements;
    }

    public Amenagement getAmenagementPorte() {
        return amenagementPorte;
    }

    public void setAmenagementPorte(Amenagement amenagementPorte) {
        this.amenagementPorte = amenagementPorte;
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

    public LocalDate getUnsubscribeDate() {
        return unsubscribeDate;
    }

    public void setUnsubscribeDate(LocalDate unsubscribeDate) {
        this.unsubscribeDate = unsubscribeDate;
    }

}
