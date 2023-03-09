package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Enquete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnqueteRepository extends JpaRepository <Enquete, Long> {
       Optional<Enquete> findByDossierId(Long id);

       List<Enquete> findAllByDossierYear(int year);
}
