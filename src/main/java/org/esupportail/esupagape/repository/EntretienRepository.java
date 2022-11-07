package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Entretien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntretienRepository extends JpaRepository <Entretien, Long> {

    List<Entretien> findEntretienByDossierId(Long dossierId);

    Page<Entretien> findAll(Pageable pageable);
}
