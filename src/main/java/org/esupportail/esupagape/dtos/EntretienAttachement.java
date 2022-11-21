package org.esupportail.esupagape.dtos;

import org.esupportail.esupagape.entity.enums.TypeContact;

import java.time.LocalDateTime;

public interface EntretienAttachement {

    Long getId();

    LocalDateTime getDate();

    TypeContact getTypeContact();

    String getInterlocuteur();

    String getCompteRendu();

    Integer getNumberOfAttachements();
}
