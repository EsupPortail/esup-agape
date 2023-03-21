package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Entretien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntretienRepository extends JpaRepository <Entretien, Long> {

    Page<Entretien> findEntretiensByDossierId(Long dossierId, Pageable pageable);

}
