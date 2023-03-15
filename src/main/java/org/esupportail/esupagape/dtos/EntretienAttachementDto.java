package org.esupportail.esupagape.dtos;

import org.esupportail.esupagape.entity.enums.TypeContact;

import java.time.LocalDateTime;

public interface EntretienAttachementDto {

    Long getId();

    LocalDateTime getDate();

    TypeContact getTypeContact();

    String getInterlocuteur();

    String getCompteRendu();

    Integer getNumberOfAttachements();
}
