package org.esupportail.esupagape.entity;

import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Date;

@Entity
public class FeuilleHeures {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private Date mois;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate registerDate;

    private Date moisPaye;

    private Date semestre;

    private Double cost;

    public FeuilleHeures() {

    }

    public FeuilleHeures(Long id, Date mois, LocalDate registerDate, Date moisPaye, Date semestre, Double cost) {
        this.id = id;
        this.mois = mois;
        this.registerDate = registerDate;
        this.moisPaye = moisPaye;
        this.semestre = semestre;
        this.cost = cost;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getMois() {
        return mois;
    }

    public void setMois(Date mois) {
        this.mois = mois;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDate registerDate) {
        this.registerDate = registerDate;
    }

    public Date getMoisPaye() {
        return moisPaye;
    }

    public void setMoisPaye(Date moisPaye) {
        this.moisPaye = moisPaye;
    }

    public Date getSemestre() {
        return semestre;
    }

    public void setSemestre(Date semestre) {
        this.semestre = semestre;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}
