package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.AideMaterielle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AideMaterielleRepository extends JpaRepository <AideMaterielle, Long> {

    Page<AideMaterielle> findByDossierId(Long dossierId, Pageable pageable);
}
