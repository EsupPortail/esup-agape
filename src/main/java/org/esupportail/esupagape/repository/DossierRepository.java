package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Dossier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DossierRepository extends JpaRepository<Dossier, Long> {

    Page<Dossier> findAllByYear(Integer year, Pageable pageable);
    Optional<Dossier> findByIndividuIdAndYear(Long id, Integer year);
    List<Dossier> findAllByIndividuId(Long id);
}
