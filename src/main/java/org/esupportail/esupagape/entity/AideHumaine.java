package org.esupportail.esupagape.entity;

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

    @Enumerated(EnumType.STRING)
    private StatusAideHumaine statusAideHumaine;

    @Column(unique = true)
    private String numEtuAide;

    @NotNull
    private String name;

    @NotNull
    private String firstName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime dateOfBirth;

    private String email;

    private String phone;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startDate;

    private String function;

    @OneToMany(cascade = CascadeType.REMOVE)
    private List<FeuilleHeure> feuilleHeures = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatusAideHumaine getStatusAideHumaine() {
        return statusAideHumaine;
    }

    public void setStatusAideHumaine(StatusAideHumaine statusAideHumaine) {
        this.statusAideHumaine = statusAideHumaine;
    }

    public String getNumEtuAide() {
        return numEtuAide;
    }

    public void setNumEtuAide(String numEtuAide) {
        this.numEtuAide = numEtuAide;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public List<FeuilleHeure> getFeuilleHeures() {
        return feuilleHeures;
    }

    public void setFeuilleHeures(List<FeuilleHeure> feuilleHeures) {
        this.feuilleHeures = feuilleHeures;
    }
}
