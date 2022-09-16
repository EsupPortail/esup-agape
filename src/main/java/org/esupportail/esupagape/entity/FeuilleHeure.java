package org.esupportail.esupagape.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class FeuilleHeure {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int mois;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate registerDate;

    private int moisPaye;

    private int semestre;

    private int cost;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMois() {
        return mois;
    }

    public void setMois(int mois) {
        this.mois = mois;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDate registerDate) {
        this.registerDate = registerDate;
    }

    public int getMoisPaye() {
        return moisPaye;
    }

    public void setMoisPaye(int moisPaye) {
        this.moisPaye = moisPaye;
    }

    public int getSemestre() {
        return semestre;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getCostString() {
        return (double) cost / 100 + "";
    }

    public void setCostString(String costString) {
        this.cost = (int) (Double.parseDouble(costString) * 100);
    }
}
