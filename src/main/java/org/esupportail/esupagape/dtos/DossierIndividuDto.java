package org.esupportail.esupagape.dtos;

import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.TypeIndividu;

import java.time.LocalDate;

public interface DossierIndividuDto {

    Long getId();

    String getNumEtu();

    String getFirstName();

    String getName();

    LocalDate getDateOfBirth();

    String getSex();

    TypeIndividu getType();

    StatusDossier getStatusDossier();
}
