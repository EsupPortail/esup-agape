package org.esupportail.esupagape.entity;

import org.esupportail.esupagape.entity.enums.Gender;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Individu {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String numEtu;

    @Column(unique = true)
    private String codeIne;

    @NotEmpty(message = "Le nom doit être renseigné")
    private String name;

    @NotEmpty(message = "Le prénom doit être renseigné")
    private String firstName;

    private String sex;

    private String nationalite;

    @NotNull(message = "La date de naissance doit être renseignée")
    @Past(message = "La date de naissance doit être dans le passé.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String eppn;

    private String photoId;

    private String emailEtu;

    private String emailPerso;

    private String currentAddress;

    private String currentCP;

    private String currentCity;

    private String fixAddress;

    private String fixCP;

    private String fixCity;

    private String fixCountry;

    private String fixPhone;

    private String contactPhone;


    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToMany(mappedBy = "individu", orphanRemoval = true)
    private List<Dossier> dossiers = new ArrayList<>();

    public Individu() {

    }

    public Individu(String numEtu, String name, String firstName, String sex, LocalDate dateOfBirth) {
        this.numEtu = numEtu;
        this.name = name;
        this.firstName = firstName;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNationalite() {
        return nationalite;
    }

    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEppn() {
        return eppn;
    }

    public void setEppn(String eppn) {
        this.eppn = eppn;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getEmailEtu() {
        return emailEtu;
    }

    public void setEmailEtu(String emailEtu) {
        this.emailEtu = emailEtu;
    }

    public String getEmailPerso() {
        return emailPerso;
    }

    public void setEmailPerso(String emailPerso) {
        this.emailPerso = emailPerso;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getCurrentCP() {
        return currentCP;
    }

    public void setCurrentCP(String currentCP) {
        this.currentCP = currentCP;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }

    public String getFixAddress() {
        return fixAddress;
    }

    public void setFixAddress(String fixAdress) {
        this.fixAddress = fixAdress;
    }

    public String getFixCP() {
        return fixCP;
    }

    public void setFixCP(String fixCP) {
        this.fixCP = fixCP;
    }

    public String getFixCity() {
        return fixCity;
    }

    public void setFixCity(String fixCity) {
        this.fixCity = fixCity;
    }

    public String getFixCountry() {
        return fixCountry;
    }

    public void setFixCountry(String fixCountry) {
        this.fixCountry = fixCountry;
    }

    public String getFixPhone() {
        return fixPhone;
    }

    public void setFixPhone(String fixPhone) {
        this.fixPhone = fixPhone;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
       this.gender = gender;
    }


    public List<Dossier> getDossiers() {
        return dossiers;
    }

    public void setDossiers(List<Dossier> dossiers) {
        this.dossiers = dossiers;
    }

}
