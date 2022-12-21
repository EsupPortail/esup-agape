package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.dtos.ComposanteDto;
import org.esupportail.esupagape.dtos.DossierIndividuDto;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.TypeIndividu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DossierRepository extends JpaRepository<Dossier, Long> {

    @Query("select distinct d.id as id, i.numEtu as numEtu, i.firstName as firstName, i.name as name, i.dateOfBirth as dateOfBirth, i.gender as gender, " +
            "d.type as type, d.statusDossier as statusDossier, i.id as individuId, a.statusAmenagement as statusAmenagement " +
            "from Dossier d join Individu i on i.id = d.individu.id left join Amenagement a on a.dossier = d " +
            "where (:fullTextSearch is null or upper(d.individu.name) like upper(concat('%', :fullTextSearch, '%')) " +
            "or upper(d.individu.firstName) like upper(concat('%', :fullTextSearch)) " +
            "or upper(concat(d.individu.name, ' ', d.individu.firstName)) like upper(concat('%', :fullTextSearch, '%')) " +
            "or upper(concat(d.individu.firstName, ' ', d.individu.name)) like upper(concat('%', :fullTextSearch, '%')) " +
            "or upper(d.individu.numEtu) = :fullTextSearch) " +
            "and (:typeIndividu is null or d.type = : typeIndividu) " +
            "and (:statusDossier is null or d.statusDossier = :statusDossier) " +
            "and (:yearFilter is null or d.year = :yearFilter)")
    Page<DossierIndividuDto> findByFullTextSearch(String fullTextSearch, TypeIndividu typeIndividu, StatusDossier statusDossier, Integer yearFilter, Pageable pageable);

    Page<Dossier> findAllByYear(Integer year, Pageable pageable);

    Optional<Dossier> findByIndividuIdAndYear(Long id, Integer year);

    @Query("select distinct year from Dossier order by year desc")
    List<Integer> findYearDistinct();

    List<Dossier> findAllByIndividuId(Long id);

    @Query("select distinct d.codComposante as cod, trim(d.composante) as libelle from Dossier d order by cod")
    List<ComposanteDto> findAllComposantes();

}
