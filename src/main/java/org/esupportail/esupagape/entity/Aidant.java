package org.esupportail.esupagape.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"num_etu_aidant"})})
public class Aidant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", allocationSize = 1)
    private Long id;

    private String numEtuAidant;

    @NotNull
    private String nameAidant;

    @NotNull
    private String firstNameAidant;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirthAidant;

    private String emailAidant;

    private String phoneAidant;

    @OneToMany(mappedBy = "aidant", cascade = CascadeType.DETACH)
    private List<AideHumaine> aideHumaines = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getDateOfBirthAidant() {
        return dateOfBirthAidant;
    }

    public void setDateOfBirthAidant(LocalDate dateOfBirthAidant) {
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

    public List<AideHumaine> getAideHumaines() {
        return aideHumaines;
    }

    public void setAideHumaines(List<AideHumaine> aideHumaines) {
        this.aideHumaines = aideHumaines;
    }
}
