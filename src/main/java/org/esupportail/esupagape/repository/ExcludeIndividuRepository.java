package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.ExcludeIndividu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExcludeIndividuRepository extends JpaRepository<ExcludeIndividu, Long> {
    ExcludeIndividu findByNumEtuHash(String numEtuHash);
}
