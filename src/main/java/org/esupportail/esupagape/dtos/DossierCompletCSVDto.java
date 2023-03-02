package org.esupportail.esupagape.dtos;

import org.esupportail.esupagape.entity.enums.Classification;
import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.entity.enums.Mdph;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.StatusDossierAmenagement;
import org.esupportail.esupagape.entity.enums.Taux;
import org.esupportail.esupagape.entity.enums.TypeIndividu;
import org.esupportail.esupagape.entity.enums.TypeSuiviHandisup;
import org.esupportail.esupagape.entity.enums.enquete.ModFrmn;
import org.esupportail.esupagape.entity.enums.enquete.TypeFrmn;

import java.time.LocalDate;
import java.util.Set;

public interface DossierCompletCSVDto {

    Long getId();

    Integer getYear();

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

    Set<Classification> getClassification();

    Mdph getMdph();

    Taux getTaux();

    Set<TypeSuiviHandisup> getTypeSuiviHandisup();

    String getCommentaire();

    TypeFrmn getTypeFormation();

    ModFrmn getModeFormation();

    String getSite();

    String getLibelleFormation();

    String getLibelleFormationPrec();

    String getCodComposante();

    String getComposante();

    String getFormAddress();

    String getResultatS1();

    Double getNoteS1();

    String getResultatS2();

    Double getNoteS2();

    String getResultatTotal();

    Double getNoteTotal();

    Boolean getSuiviHandisup();

}