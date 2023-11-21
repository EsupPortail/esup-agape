package org.esupportail.esupagape.dtos.forms;

import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.TypeIndividu;

import java.time.LocalDate;

public class DossierIndividuForm {

    Long id;

    String name;

    String firstName;

    LocalDate dateOfBirth;

    Gender gender;

    String numEtu;

    TypeIndividu type;

    StatusDossier statusDossier;

    String nationalite;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getNumEtu() {
        return numEtu;
    }

    public void setNumEtu(String numEtu) {
        this.numEtu = numEtu;
    }

    public TypeIndividu getType() {
        return type;
    }

    public void setType(TypeIndividu type) {
        this.type = type;
    }

    public StatusDossier getStatusDossier() {
        return statusDossier;
    }

    public void setStatusDossier(StatusDossier statusDossier) {
        this.statusDossier = statusDossier;
    }

    public String getNationalite() {
        return nationalite;
    }

    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }
}
