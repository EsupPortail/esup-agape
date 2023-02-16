package org.esupportail.esupagape.service.interfaces.importindividu;

import org.esupportail.esupagape.entity.enums.Classification;

import java.time.LocalDate;

public class IndividuInfos {

    String eppn;
    String codeIne;
    String name;
    String firstName;
    String genre;
    LocalDate dateOfBirth;
    String nationalite;
    String emailEtu;
    String emailPerso;
    String fixPhone;
    String contactPhone;
    String fixAddress;
    String fixCP;
    String fixCity;
    String fixCountry;
    String currentAddress;
    String currentCP;
    String currentCity;
    String currentCountry;
    String photoId;
    Classification handicap;

    public String getEppn() {
        return eppn;
    }

    public void setEppn(String eppn) {
        this.eppn = eppn;
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNationalite() {
        return nationalite;
    }

    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
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

    public String getFixAddress() {
        return fixAddress;
    }

    public void setFixAddress(String fixAddress) {
        this.fixAddress = fixAddress;
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

    public String getCurrentCountry() {
        return currentCountry;
    }

    public void setCurrentCountry(String currentCountry) {
        this.currentCountry = currentCountry;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public Classification getHandicap() {
        return handicap;
    }

    public void setHandicap(Classification handicap) {
        this.handicap = handicap;
    }
}
