package org.esupportail.esupagape.entity;

import org.esupportail.esupagape.entity.enums.FonctionAidant;
import org.esupportail.esupagape.entity.enums.StatusAideHumaine;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class AideHumaine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Dossier dossier;

    @Enumerated(EnumType.STRING)
    private StatusAideHumaine statusAideHumaine;

    @Column(unique = true)
    private String numEtuAidant;

    @NotNull
    private String nameAidant;

    @NotNull
    private String firstNameAidant;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime dateOfBirthAidant;

    private String emailAidant;

    private String phoneAidant;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startDate;

    @Enumerated(EnumType.STRING)
    private FonctionAidant fonctionAidant;

    @OneToMany(mappedBy = "aideHumaine", cascade = CascadeType.REMOVE)
    private List<PeriodeAideHumaine> periodeAideHumaines = new ArrayList<>();

    @OneToOne
    private Document ficheRenseignement;

    @OneToOne
    private Document contrat;

    @OneToOne
    private Document annexe;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Dossier getDossier() {
        return dossier;
    }

    public void setDossier(Dossier dossier) {
        this.dossier = dossier;
    }

    public StatusAideHumaine getStatusAideHumaine() {
        return statusAideHumaine;
    }

    public void setStatusAideHumaine(StatusAideHumaine statusAideHumaine) {
        this.statusAideHumaine = statusAideHumaine;
    }

    public String getNumEtuAidant() {
        return numEtuAidant;
    }

    public void setNumEtuAidant(String numEtuAidant) {
        this.numEtuAidant = numEtuAidant;
    }

    public String getNameAidant() {
        return nameAidant;
    }

    public void setNameAidant(String nameAidant) {
        this.nameAidant = nameAidant;
    }

    public String getFirstNameAidant() {
        return firstNameAidant;
    }

    public void setFirstNameAidant(String firstNameAidant) {
        this.firstNameAidant = firstNameAidant;
    }

    public LocalDateTime getDateOfBirthAidant() {
        return dateOfBirthAidant;
    }

    public void setDateOfBirthAidant(LocalDateTime dateOfBirthAidant) {
        this.dateOfBirthAidant = dateOfBirthAidant;
    }

    public String getEmailAidant() {
        return emailAidant;
    }

    public void setEmailAidant(String emailAidant) {
        this.emailAidant = emailAidant;
    }

    public String getPhoneAidant() {
        return phoneAidant;
    }

    public void setPhoneAidant(String phoneAidant) {
        this.phoneAidant = phoneAidant;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public FonctionAidant getFonctionAidant() {
        return fonctionAidant;
    }

    public void setFonctionAidant(FonctionAidant fonctionAidant) {
        this.fonctionAidant = fonctionAidant;
    }

    public List<PeriodeAideHumaine> getPeriodeAideHumaines() {
        return periodeAideHumaines;
    }

    public void setPeriodeAideHumaines(List<PeriodeAideHumaine> periodeAideHumaines) {
        this.periodeAideHumaines = periodeAideHumaines;
    }

    public Document getFicheRenseignement() {
        return ficheRenseignement;
    }

    public void setFicheRenseignement(Document ficheRenseignement) {
        this.ficheRenseignement = ficheRenseignement;
    }

    public Document getContrat() {
        return contrat;
    }

    public void setContrat(Document contrat) {
        this.contrat = contrat;
    }

    public Document getAnnexe() {
        return annexe;
    }

    public void setAnnexe(Document annexe) {
        this.annexe = annexe;
    }
}
