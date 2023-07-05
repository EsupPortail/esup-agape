package org.esupportail.esupagape.dtos;

import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.StatusDossierAmenagement;
import org.esupportail.esupagape.entity.enums.TypeIndividu;

import java.time.LocalDate;

public interface DossierIndividuDto {

    Long getId();

    String getNumEtu();

    String getCodeIne();

    String getFirstName();

    String getName();

    LocalDate getDateOfBirth();

    TypeIndividu getType();

    StatusDossier getStatusDossier();

    StatusDossierAmenagement getStatusDossierAmenagement();

    Long getIndividuId();

    Gender getGender();

    String getEmailEtu();

    Boolean getDesinscrit();

}
