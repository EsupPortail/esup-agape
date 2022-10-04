package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Dossier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DossierRepository extends JpaRepository<Dossier, Long> {

    @Query("select d from Dossier d where " +
            "upper(d.individu.name) like upper(concat('%', :fullTextSearch)) " +
            "or upper(d.individu.firstName) like upper(concat('%', :fullTextSearch)) " +
            "or upper(concat(d.individu.name, ' ', d.individu.firstName)) like upper(concat('%', :fullTextSearch)) " +
            "or upper(concat(d.individu.firstName, ' ', d.individu.name)) like upper(concat('%', :fullTextSearch)) " +
            "or upper(d.individu.numEtu) = :fullTextSearch")
    Page<Dossier> findByFullTextSearch(String fullTextSearch, Pageable pageable);
    Page<Dossier> findAllByYear(Integer year, Pageable pageable);
    Optional<Dossier> findByIndividuIdAndYear(Long id, Integer year);
    List<Dossier> findAllByIndividuId(Long id);
}
