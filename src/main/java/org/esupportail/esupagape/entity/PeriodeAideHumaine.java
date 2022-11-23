package org.esupportail.esupagape.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Month;

@Entity
public class PeriodeAideHumaine {

    public PeriodeAideHumaine() {
    }

    public PeriodeAideHumaine(Month mois) {
        this.mois = mois;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Month mois;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate registrationDate;

    @Enumerated(EnumType.STRING)
    private Month moisPaye;

    private Double nbHeures;

    private Integer cost;

    @OneToOne
    private Document feuilleHeures;

    @OneToOne
    private Document planning;

    @ManyToOne
    private AideHumaine aideHumaine;

    public AideHumaine getAideHumaine() {
        return aideHumaine;
    }

    public void setAideHumaine(AideHumaine aideHumaine) {
        this.aideHumaine = aideHumaine;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Month getMois() {
        return mois;
    }

    public void setMois(Month mois) {
        this.mois = mois;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registerDate) {
        this.registrationDate = registerDate;
    }

    public Month getMoisPaye() {
        return moisPaye;
    }

    public void setMoisPaye(Month moisPaye) {
        this.moisPaye = moisPaye;
    }

    public Double getNbHeures() {
        return nbHeures;
    }

    public void setNbHeures(Double nbHeures) {
        this.nbHeures = nbHeures;
    }

    public Integer getSemestre() {
        if(mois.getValue() == 9 || mois.getValue() == 10 || mois.getValue() == 11 || mois.getValue() == 12) {
            return 1;
        } else {
            return 2;
        }
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Document getFeuilleHeures() {
        return feuilleHeures;
    }

    public void setFeuilleHeures(Document feuilleHeure) {
        this.feuilleHeures = feuilleHeure;
    }

    public Document getPlanning() {
        return planning;
    }

    public void setPlanning(Document planning) {
        this.planning = planning;
    }

    public String getCostString() {
        return (double) cost / 100 + "";
    }

    public void setCostString(String costString) {
        this.cost = (int) (Double.parseDouble(costString) * 100);
    }
}
