package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Enquete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnqueteRepository extends JpaRepository <Enquete, Long> {
    List<Enquete> findEnquetesByDossierId(Long dossierId);
}
