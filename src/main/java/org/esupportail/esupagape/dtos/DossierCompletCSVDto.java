package org.esupportail.esupagape.dtos;

import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.StatusDossierAmenagement;
import org.esupportail.esupagape.entity.enums.TypeIndividu;

public interface DossierCompletCSVDto {

    Long getId();
    String getYear() ;
    String getNumEtu();
    String getName();
    String getFirstName();
    TypeIndividu getType();
    StatusDossier getStatusDossier();
    StatusDossierAmenagement getStatusDossierAmenagement();
}