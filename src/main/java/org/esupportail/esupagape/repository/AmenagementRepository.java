package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Amenagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenagementRepository extends JpaRepository <Amenagement, Long> {

    Page<Amenagement> findByDossierId(Long dossierId, Pageable pageable);

}
