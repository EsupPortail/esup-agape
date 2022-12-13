package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.enums.StatusAmenagement;
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
    public void create(Amenagement amenagement) {
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
    }
}


