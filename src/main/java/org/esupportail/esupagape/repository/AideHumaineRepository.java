package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.AideHumaine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AideHumaineRepository extends JpaRepository <AideHumaine, Long> {

    Page<AideHumaine> findByDossierId(Long dossierId, Pageable pageable);

}
