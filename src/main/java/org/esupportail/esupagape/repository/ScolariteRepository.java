package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.enums.StatusAmenagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScolariteRepository extends JpaRepository <Amenagement, Long>{

    @Query("select distinct a from Amenagement a join Dossier d on a.dossier = d join Individu i on d.individu = i  " +
            "where  " +
            "(:statusAmenagement is null or a.statusAmenagement = :statusAmenagement) " +
            "and a.dossier.codComposante  in :codComposantes " +
            "and a.statusAmenagement = 'VISE_ADMINISTRATION'" +
            "and (a.typeAmenagement = 'CURSUS' or a.typeAmenagement = 'DATE' and a.endDate >= current_date)" +
            "and (:yearFilter is null or d.year = :yearFilter)")
    Page<Amenagement> findByFullTextSearchScol(StatusAmenagement statusAmenagement, List<String> codComposantes, Integer yearFilter, Pageable pageable);

    @Query("select a from Amenagement a join Dossier d on a.dossier = d join Individu i on d.individu = i " +
            "where ((upper(i.firstName) like upper(concat('%', :fullTextSearch))) " +
            "or (upper(concat(i.name, ' ', i.firstName)) like upper(concat('%', :fullTextSearch, '%'))) " +
            "or (upper(concat(i.firstName, ' ', i.name)) like upper(concat('%', :fullTextSearch, '%')))) " +
            "and (d.year = :yearFilter) " +
            "and (a.dossier.codComposante  in :codComposantes) " +
            "and (a.typeAmenagement = 'CURSUS' or a.typeAmenagement = 'DATE' and a.endDate >= current_date)" +
            "and a.statusAmenagement = :statusAmenagement")
    Page<Amenagement> findByIndividuNameScol(String fullTextSearch, StatusAmenagement statusAmenagement, Integer yearFilter, List<String> codComposantes, Pageable pageable);

}
