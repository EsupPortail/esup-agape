package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.repository.AmenagementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AmenagementService {

    private final AmenagementRepository amenagementRepository;


    public AmenagementService(AmenagementRepository amenagementRepository) {
        this.amenagementRepository = amenagementRepository;
    }

    public Amenagement getById(Long id) {
        return amenagementRepository.findById(id).orElseThrow();
    }

    public Page<Amenagement> findByDossier(Dossier dossier) {
        return amenagementRepository.findByDossierId(dossier.getId(), Pageable.unpaged());
    }
}
