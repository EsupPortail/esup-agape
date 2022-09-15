package org.esupportail.esupagape.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Individu", uniqueConstraints = {
        @UniqueConstraint(name = "uc_individu_numetu", columnNames = {"numEtu"})
})
public class Individu {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true)
    private int numEtu;
    private String name;
    private String firstName;
    private String sex;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateOfBirth;

    private Long eppn;

    private String emailEtu;

    private String emailPerso;

    private String currentAddress;

    private int currentCP;

    private String currentCity;

    private String fixAdress;

    private int fixCP;

    private String fixCity;

    private int mobilPhone;

    private int fixPhone;

    private int contactPhone;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Dossier> dossiers = new ArrayList<>();

    public Individu(List<Dossier> dossiers) {
        this.dossiers = dossiers;
    }

    public List<Dossier> getDossiers() {
        return dossiers;
    }

    public void setDossiers(List<Dossier> dossiers) {
        this.dossiers = dossiers;
    }

    public Individu() {

    }

    public Individu(Long id, int numEtu, String name, String firstName, String sex, LocalDate dateOfBirth, Long eppn, String emailEtu, String emailPerso, String currentAddress, int currentCP, String currentCity, String fixAdress, int fixCP, String fixCity, int mobilPhone, int fixPhone, int contactPhone) {
        this.id = id;
        this.numEtu = numEtu;
        this.name = name;
        this.firstName = firstName;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.eppn = eppn;
        this.emailEtu = emailEtu;
        this.emailPerso = emailPerso;
        this.currentAddress = currentAddress;
        this.currentCP = currentCP;
        this.currentCity = currentCity;
        this.fixAdress = fixAdress;
        this.fixCP = fixCP;
        this.fixCity = fixCity;
        this.mobilPhone = mobilPhone;
        this.fixPhone = fixPhone;
        this.contactPhone = contactPhone;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumEtu() {
        return numEtu;
    }

    public void setNumEtu(int numEtu) {
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Long getEppn() {
        return eppn;
    }

    public void setEppn(Long eppn) {
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

    public int getCurrentCP() {
        return currentCP;
    }

    public void setCurrentCP(int currentCP) {
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

    public int getFixCP() {
        return fixCP;
    }

    public void setFixCP(int fixCP) {
        this.fixCP = fixCP;
    }

    public String getFixCity() {
        return fixCity;
    }

    public void setFixCity(String fixCity) {
        this.fixCity = fixCity;
    }

    public int getMobilPhone() {
        return mobilPhone;
    }

    public void setMobilPhone(int mobilPhone) {
        this.mobilPhone = mobilPhone;
    }

    public int getFixPhone() {
        return fixPhone;
    }

    public void setFixPhone(int fixPhone) {
        this.fixPhone = fixPhone;
    }

    public int getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(int contactPhone) {
        this.contactPhone = contactPhone;
    }
}
