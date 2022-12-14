package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.enums.Autorisation;
import org.esupportail.esupagape.entity.enums.Classification;
import org.esupportail.esupagape.entity.enums.StatusAmenagement;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.repository.AmenagementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AmenagementService {

    private final AmenagementRepository amenagementRepository;
    private final DossierService dossierService;

    public AmenagementService(AmenagementRepository amenagementRepository, DossierService dossierService) {
        this.amenagementRepository = amenagementRepository;
        this.dossierService = dossierService;
    }

    public Amenagement getById(Long id) throws AgapeJpaException {
        Optional<Amenagement> optionalAmenagement = amenagementRepository.findById(id);
        if (optionalAmenagement.isPresent()) {
            return optionalAmenagement.get();
        } else {
            throw new AgapeJpaException("Je n'ai pas trouvé cet aménagement");
        }
    }

    public Page<Amenagement> findByDossier(Dossier dossier) {
        return amenagementRepository.findByDossierId(dossier.getId(), Pageable.unpaged());
    }

    public boolean isAmenagementValid(Long dossierId) {
        return amenagementRepository.findByDossierIdAndStatusAmenagement(dossierId, StatusAmenagement.VISER_ADMINISTRATION).size() > 0;
    }

    @Transactional
    public void create(Amenagement amenagement, Long idDossier) {
        Dossier dossier = dossierService.getById(idDossier);
        if (dossier.getStatusDossier().equals(StatusDossier.IMPORTE)) {
            dossier.setStatusDossier(StatusDossier.RECU_PAR_LA_MEDECINE_PREVENTIVE);
        }
        amenagement.setDossier(dossier);
        updateClassification(amenagement);
        amenagementRepository.save(amenagement);
    }

    @Transactional
    public void deleteAmenagement(Long amenagementId) {
        amenagementRepository.deleteById(amenagementId);
    }

    @Transactional
    public void update(Long amenagementId, Amenagement amenagement) throws AgapeJpaException {
        Amenagement amenagementToUpdate = getById(amenagementId);
        amenagementToUpdate.setTypeAmenagement(amenagement.getTypeAmenagement());
        amenagementToUpdate.setAmenagementText(amenagement.getAmenagementText());
        amenagementToUpdate.setAutorisation(amenagement.getAutorisation());
        amenagementToUpdate.setClassification(amenagement.getClassification());
        amenagementToUpdate.setTypeEpreuve(amenagement.getTypeEpreuve());
        amenagementToUpdate.setAutresTypeEpreuve(amenagement.getAutresTypeEpreuve());
        amenagementToUpdate.setEndDate(amenagement.getEndDate());
        updateClassification(amenagementToUpdate);
    }

    private static void updateClassification(Amenagement amenagement) {
        if (amenagement.getDossier().getStatusDossier().equals(StatusDossier.RECU_PAR_LA_MEDECINE_PREVENTIVE)) {
            if (amenagement.getAutorisation().equals(Autorisation.OUI)) {
                amenagement.getDossier().setClassification(amenagement.getClassification());
            }
            if (amenagement.getAutorisation().equals(Autorisation.NON)) {
                amenagement.getDossier().getClassification().clear();
                amenagement.getDossier().getClassification().add(Classification.REFUS);
            }
            if (amenagement.getAutorisation().equals(Autorisation.NC)) {
                amenagement.getDossier().getClassification().clear();
                amenagement.getDossier().getClassification().add(Classification.NON_COMMUNIQUE);
            }
        }
    }
}


