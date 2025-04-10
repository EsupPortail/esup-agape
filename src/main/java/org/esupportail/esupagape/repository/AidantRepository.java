package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Aidant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AidantRepository extends JpaRepository <Aidant, Long> {

    Aidant findByNumEtuAidant(String numEtuAidant);

}
