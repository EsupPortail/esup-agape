package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.enums.StatusAmenagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmenagementRepository extends JpaRepository <Amenagement, Long> {

    Page<Amenagement> findByDossierId(Long dossierId, Pageable pageable);

    List<Amenagement> findByDossierIdAndStatusAmenagement(Long dossierId, StatusAmenagement statusAmenagement);

}
