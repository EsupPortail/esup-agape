package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.enums.StatusAmenagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScolariteRepository extends JpaRepository <Amenagement, Long>{
    @Query("select distinct a from Amenagement a join Dossier d on a.dossier = d join Individu i on d.individu = i  " +
            "where  " +
            "(:statusAmenagement is null or a.statusAmenagement = :statusAmenagement) " +
            "and (:codComposante is null or a.dossier.codComposante  = :codComposante) " +
            "and a.statusAmenagement = 'VISE_ADMINISTRATION'" +
            "and (a.typeAmenagement = 'CURSUS' or a.typeAmenagement = 'DATE' and a.endDate >= current_date)" +
            "and (:yearFilter is null or d.year = :yearFilter)")

    Page<Amenagement> findByFullTextSearchScol(StatusAmenagement statusAmenagement, String codComposante, Integer yearFilter, Pageable pageable);

    @Query("select distinct a from Amenagement a join Dossier d on a.dossier = d join Individu i on d.individu = i  " +
            "where  " +
            "(:statusAmenagement is null or a.statusAmenagement = :statusAmenagement) " +
            "and (:codComposante is null or a.dossier.codComposante  = :codComposante) " +
            "and a.statusAmenagement = 'VISE_ADMINISTRATION'" +
            "and (a.typeAmenagement = 'CURSUS' or a.typeAmenagement = 'DATE' and a.endDate >= current_date)" +
            "and (:yearFilter is null or d.year = :yearFilter)")
    List<Amenagement> findByFullTextSearchScol(StatusAmenagement statusAmenagement, String codComposante, Integer yearFilter);

    @Query("select a from Amenagement a join Dossier d on a.dossier = d join Individu i on d.individu = i " +
            "where ((upper(i.firstName) like upper(concat('%', :fullTextSearch))) " +
            "or (upper(concat(i.name, ' ', i.firstName)) like upper(concat('%', :fullTextSearch, '%'))) " +
            "or (upper(concat(i.firstName, ' ', i.name)) like upper(concat('%', :fullTextSearch, '%')))) " +
            "and (d.year = :yearFilter) " +
            "and (a.dossier.codComposante  = :codComposante) " +
            "and (a.typeAmenagement = 'CURSUS' or a.typeAmenagement = 'DATE' and a.endDate >= current_date)" +
            "and a.statusAmenagement = :statusAmenagement")
    Page<Amenagement> findByIndividuNameScol(@Param("fullTextSearch") String fullTextSearch,
                                             StatusAmenagement statusAmenagement,
                                             @Param("yearFilter") Integer yearFilter,
                                                 @Param("codComposante") String codComposante,
                                                 Pageable pageable);

}
