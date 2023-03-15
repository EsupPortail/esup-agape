package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Dossier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatistiquesRepository extends JpaRepository <Dossier, Long> {
}
