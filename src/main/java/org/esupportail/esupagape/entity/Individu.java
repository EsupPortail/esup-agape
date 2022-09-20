package org.esupportail.esupagape.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Individu {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String numEtu;

    private String name;

    private String firstName;

    private String sex;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime dateOfBirth;

    private String eppn;

    private String emailEtu;

    private String emailPerso;

    private String currentAddress;

    private String currentCP;

    private String currentCity;

    private String fixAdress;

    private String fixCP;

    private String fixCity;

    private String mobilPhone;

    private String fixPhone;

    private String contactPhone;

    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Dossier> dossiers = new ArrayList<>();

    public Individu() {

    }

    public Individu(String numEtu, String name, String firstName, String sex, LocalDateTime dateOfBirth) {
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

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEppn() {
        return eppn;
    }

    public void setEppn(String eppn) {
        this.eppn = eppn;
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

    public String getFixAdress() {
        return fixAdress;
    }

    public void setFixAdress(String fixAdress) {
        this.fixAdress = fixAdress;
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

    public String getMobilPhone() {
        return mobilPhone;
    }

    public void setMobilPhone(String mobilPhone) {
        this.mobilPhone = mobilPhone;
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

    public List<Dossier> getDossiers() {
        return dossiers;
    }

    public void setDossiers(List<Dossier> dossiers) {
        this.dossiers = dossiers;
    }
}
