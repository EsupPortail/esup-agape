package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Individu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndividuRepository extends JpaRepository<Individu, Long> {

    Individu findByNumEtu(String numEtu);

    Page<Individu> findAllByNameContainsIgnoreCase(String name, Pageable pageable);

}
