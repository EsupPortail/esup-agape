package org.esupportail.esupagape.dtos.csvs;

import org.esupportail.esupagape.entity.enums.Classification;
import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.entity.enums.Mdph;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.StatusDossierAmenagement;
import org.esupportail.esupagape.entity.enums.Taux;
import org.esupportail.esupagape.entity.enums.TypeIndividu;
import org.esupportail.esupagape.entity.enums.TypeSuiviHandisup;
import org.esupportail.esupagape.entity.enums.enquete.ModFrmn;
import org.esupportail.esupagape.entity.enums.enquete.TypFrmn;

import java.util.Set;

public interface DossierCompletCSVDto {

    Gender getGender();

    Integer getYearOfBirth();

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

    TypFrmn getTypeFormation();

    ModFrmn getModeFormation();

    String getSite();

    String getLibelleFormation();

    String getLibelleFormationPrec();

    String getCodComposante();

    String getComposante();

    String getFormAddress();

    String getResultatTotal();

}