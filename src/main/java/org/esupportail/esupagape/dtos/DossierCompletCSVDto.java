package org.esupportail.esupagape.dtos;

import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.StatusDossierAmenagement;
import org.esupportail.esupagape.entity.enums.TypeIndividu;

import java.time.LocalDate;

public interface DossierCompletCSVDto {

    Long getId();
    String getYear() ;
    String getNumEtu();
    String getCodeIne();
    Gender getGender();
    String getName();
    String getFirstName();
    LocalDate getDateOfBirth();
    TypeIndividu getType();
    StatusDossier getStatusDossier();
    StatusDossierAmenagement getStatusDossierAmenagement();
}