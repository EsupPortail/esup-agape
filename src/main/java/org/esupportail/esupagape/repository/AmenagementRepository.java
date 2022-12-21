package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.enums.StatusAmenagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AmenagementRepository extends JpaRepository <Amenagement, Long> {

    Page<Amenagement> findByDossierId(Long dossierId, Pageable pageable);

    List<Amenagement> findByDossierIdAndStatusAmenagement(Long dossierId, StatusAmenagement statusAmenagement);

    @Query(value = "select a from Amenagement a " +
            "where " +
            "(:statusAmenagement is null or a.statusAmenagement = :statusAmenagement) " +
            "and (:codComposante is null or a.dossier.codComposante  = :codComposante)")

    Page<Amenagement> findByFullTextSearch(StatusAmenagement statusAmenagement, String codComposante, Pageable pageable);
}