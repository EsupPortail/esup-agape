package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.dtos.ComposanteDto;
import org.esupportail.esupagape.dtos.DossierIndividuDto;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.StatusDossierAmenagement;
import org.esupportail.esupagape.entity.enums.TypeIndividu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DossierRepository extends JpaRepository<Dossier, Long> {

    @Query("""
            select d.id as id, i.numEtu as numEtu, i.codeIne as codeIne, i.firstName as firstName, i.name as name, i.dateOfBirth as dateOfBirth,
            d.type as type, d.statusDossier as statusDossier, d.statusDossierAmenagement as statusDossierAmenagement, i.id as individuId, i.gender as gender, i.emailEtu as emailEtu, i.desinscrit as desinscrit, d.year as year
            from Dossier d join Individu i on i.id = d.individu.id
            where (:fullTextSearch is null or upper(d.individu.name) like upper(concat('%', :fullTextSearch, '%'))
            or upper(d.individu.firstName) like upper(concat('%', :fullTextSearch))
            or upper(concat(d.individu.name, ' ', d.individu.firstName)) like upper(concat('%', :fullTextSearch, '%'))
            or upper(concat(d.individu.firstName, ' ', d.individu.name)) like upper(concat('%', :fullTextSearch, '%'))
            or upper(d.individu.numEtu) = :fullTextSearch
            or upper (d.individu.codeIne) = :fullTextSearch)
            and (:typeIndividu is null or d.type = :typeIndividu)
            and (:statusDossier is null or d.statusDossier = :statusDossier)
            and (:statusDossierAmenagement is null or d.statusDossierAmenagement = :statusDossierAmenagement)
            and (:yearFilter is null or d.year = :yearFilter)
            """)
    Page<DossierIndividuDto> findByFullTextSearch(String fullTextSearch, TypeIndividu typeIndividu, StatusDossier statusDossier, StatusDossierAmenagement statusDossierAmenagement, Integer yearFilter, Pageable pageable);

    Page<Dossier> findAllByYear(Integer year, Pageable pageable);

    Optional<Dossier> findByIndividuIdAndYear(Long id, Integer year);

    @Query("select distinct year from Dossier order by year desc")
    List<Integer> findYearDistinct();

    List<Dossier> findAllByIndividuId(Long id);

    @Query("select distinct d.codComposante as cod, d.composante as libelle from Dossier d order by cod")
    List<ComposanteDto> findAllComposantes();

    @Query("select distinct d.niveauEtudes as niv from Dossier d group by d.niveauEtudes")
    List<String> findAllNiveaux();

    @Query("select distinct d.secteurDisciplinaire as sectDis from Dossier d group by d.secteurDisciplinaire")
    List<String> findAllSecteurDisciplinaire();

    @Query("select distinct d.libelleFormation as libForm from Dossier d group by d.libelleFormation")
    List<String> findAllLibelleFormation();

    @Query("select distinct d.campus from Dossier d")
    List<String> findAllCampus();

    @Query("select d.id from Dossier d")
    List<Long> findIdsAll();

}
