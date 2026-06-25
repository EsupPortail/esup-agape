package org.esupportail.esupagape.service;

import jakarta.persistence.EntityNotFoundException;
import org.esupportail.esupagape.entity.TypeLigneAmenagement;
import org.esupportail.esupagape.repository.TypeLigneAmenagementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TypeLigneAmenagementService {

    private final TypeLigneAmenagementRepository typeLigneAmenagementRepository;

    public TypeLigneAmenagementService(TypeLigneAmenagementRepository typeLigneAmenagementRepository) {
        this.typeLigneAmenagementRepository = typeLigneAmenagementRepository;
    }

    @Transactional(readOnly = true)
    public List<TypeLigneAmenagement> getByYear(Integer year) {
        return typeLigneAmenagementRepository.findByYearOrderByOrdreAsc(year);
    }

    @Transactional(readOnly = true)
    public List<TypeLigneAmenagement> getActifsByYear(Integer year) {
        return typeLigneAmenagementRepository.findByYearAndActifTrueOrderByOrdreAsc(year);
    }

    @Transactional
    public TypeLigneAmenagement save(TypeLigneAmenagement typeLigneAmenagement) {
        return typeLigneAmenagementRepository.save(typeLigneAmenagement);
    }

    @Transactional
    public void setActif(Long id, boolean actif) {
        TypeLigneAmenagement type = typeLigneAmenagementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TypeLigneAmenagement introuvable : " + id));
        type.setActif(actif);
    }
}