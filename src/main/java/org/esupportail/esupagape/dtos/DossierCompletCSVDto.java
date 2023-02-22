package org.esupportail.esupagape.dtos;

import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.StatusDossierAmenagement;
import org.esupportail.esupagape.entity.enums.TypeIndividu;

import java.time.LocalDate;

public interface DossierCompletCSVDto {

    Long getId();
    Integer getYear() ;
    String getNumEtu();
    String getCodeIne();
    Gender getGender();
    String getName();
    String getFirstName();
    LocalDate getDateOfBirth();
    String getEmailEtu();
    String getEmailPerso();
    String getFixAddress();
    String getFixCP();
    String getFixCity();
    String getFixCountry();
    TypeIndividu getType();
    StatusDossier getStatusDossier();
    StatusDossierAmenagement getStatusDossierAmenagement();
   String getFormAddress();
}