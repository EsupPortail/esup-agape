package org.esupportail.esupagape.service;

import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.enums.StatusAmenagement;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AmenagementWorkflowService {

    private final ApplicationProperties applicationProperties;

    public AmenagementWorkflowService(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public boolean isReferentValidationEnabled() {
        return Boolean.TRUE.equals(applicationProperties.getValidationReferents());
    }

    public StatusAmenagement getPendingAdministrationStatus() {
        return StatusAmenagement.VALIDE_REFERENT;
    }

    public StatusAmenagement getPendingReferentStatus() {
        return StatusAmenagement.VALIDE_MEDECIN;
    }

    public boolean isPendingAdministrationValidation(StatusAmenagement statusAmenagement) {
        return statusAmenagement == getPendingAdministrationStatus();
    }

    public boolean isPendingAdministrationValidation(Amenagement amenagement) {
        return amenagement != null && isPendingAdministrationValidation(amenagement.getStatusAmenagement());
    }

    public boolean isPendingReferentValidation(StatusAmenagement statusAmenagement) {
        return isReferentValidationEnabled() && statusAmenagement == StatusAmenagement.VALIDE_MEDECIN;
    }

    public boolean isPendingReferentValidation(Amenagement amenagement) {
        return amenagement != null && isPendingReferentValidation(amenagement.getStatusAmenagement());
    }

    public List<StatusAmenagement> getStatusesVisibleForAdministration(boolean esupSignaturePresent) {
        List<StatusAmenagement> statuses = new ArrayList<>(Arrays.asList(StatusAmenagement.values()));
        statuses.remove(StatusAmenagement.BROUILLON);
        statuses.remove(StatusAmenagement.SUPPRIME);
        if (!esupSignaturePresent) {
            statuses.remove(StatusAmenagement.ENVOYE);
        }
        statuses.remove(StatusAmenagement.VALIDE_MEDECIN);
        return statuses;
    }

    public List<StatusAmenagement> getStatusesToSyncEsupSignature() {
        return List.of(StatusAmenagement.ENVOYE, getPendingAdministrationStatus());
    }
}
