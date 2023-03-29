package org.esupportail.esupagape.dtos;

import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.StatusDossierAmenagement;
import org.esupportail.esupagape.entity.enums.TypeIndividu;

import java.time.LocalDate;

public class DossierIndividuClassDto implements DossierIndividuDto {

    private Long id;

    private String numEtu;

    private String codeIne;

    private String firstName;

    private String name;

    private LocalDate dateOfBirth;

    private TypeIndividu type;

    private StatusDossier statusDossier;

    private StatusDossierAmenagement statusDossierAmenagement;

    private Long individuId;

    private Gender gender;

    public DossierIndividuClassDto() {
    }

    public DossierIndividuClassDto(Long id, String numEtu, String codeIne, String firstName, String name, LocalDate dateOfBirth, TypeIndividu type, StatusDossier statusDossier, StatusDossierAmenagement statusDossierAmenagement, Long individuId, Gender gender) {
        this.id = id;
        this.numEtu = numEtu;
        this.codeIne = codeIne;
        this.firstName = firstName;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.type = type;
        this.statusDossier = statusDossier;
        this.statusDossierAmenagement = statusDossierAmenagement;
        this.individuId = individuId;
        this.gender = gender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumEtu() {
        return numEtu;
    }

    public void setNumEtu(String numEtu) {
        this.numEtu = numEtu;
    }

    public String getCodeIne() {
        return codeIne;
    }

    public void setCodeIne(String codeIne) {
        this.codeIne = codeIne;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    public StatusDossierAmenagement getStatusDossierAmenagement() {
        return statusDossierAmenagement;
    }

    public void setStatusDossierAmenagement(StatusDossierAmenagement statusDossierAmenagement) {
        this.statusDossierAmenagement = statusDossierAmenagement;
    }

    public Long getIndividuId() {
        return individuId;
    }

    public void setIndividuId(Long individuId) {
        this.individuId = individuId;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
