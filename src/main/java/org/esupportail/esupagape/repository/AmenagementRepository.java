package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.StatusAmenagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AmenagementRepository extends JpaRepository<Amenagement, Long> {

    Page<Amenagement> findByDossierId(Long dossierId, Pageable pageable);

    @Query("select a from Amenagement a where a.dossier.id = :dossierId and a.statusAmenagement = :statusAmenagement order by a.administrationDate desc")
    List<Amenagement> findByDossierIdAndStatusAmenagement(Long dossierId, StatusAmenagement statusAmenagement);

    List<Amenagement> findByStatusAmenagement(StatusAmenagement statusAmenagement);

    List<Amenagement> findByStatusAmenagementAndDossierYear(StatusAmenagement statusAmenagement, int year);

    @Query("select distinct a from Amenagement a join Dossier d on a.dossier = d " +
            "where " +
            "(:statusAmenagement is null or a.statusAmenagement = :statusAmenagement) " +
            "and (:codComposante is null or a.dossier.codComposante  = :codComposante) " +
            "and a.statusAmenagement != 'BROUILLON' and a.statusAmenagement != 'SUPPRIME'" +
            "and (:yearFilter is null or d.year = :yearFilter)")
    Page<Amenagement> findByFullTextSearch(StatusAmenagement statusAmenagement, String codComposante, Integer yearFilter, Pageable pageable);


    @Query("select a from Amenagement a join Dossier d on a.dossier = d join Individu i on d.individu = i " +
            "where (upper(i.firstName) like upper(concat('%', :fullTextSearch))) " +
            "or (upper(concat(i.name, ' ', i.firstName)) like upper(concat('%', :fullTextSearch, '%'))) " +
            "or (upper(concat(i.firstName, ' ', i.name)) like upper(concat('%', :fullTextSearch, '%'))) " +
            "or (upper(d.individu.numEtu) = upper(:fullTextSearch)) " +
            "or (upper(d.individu.codeIne) = upper(:fullTextSearch)) " +
            "and (d.year < :yearFilter) " +
            "and d.individu.desinscrit != true " +
            "and a.statusAmenagement = 'VISE_ADMINISTRATION' " +
            "and a.typeAmenagement = 'CURSUS'")
    Page<Amenagement> findByIndividuNamePortable(@Param("fullTextSearch") String fullTextSearch,
                                                 @Param("yearFilter") Integer yearFilter,
                                                 Pageable pageable);


    @Query("select count(a) from Amenagement a join Dossier d on a.dossier = d " +
            "where " +
            "a.statusAmenagement = 'VALIDE_MEDECIN' " +
            "and (:yearFilter is null or d.year = :yearFilter)")
    Long countToValidate(Integer yearFilter);

    @Query("""
            select distinct a from Amenagement a
            join Dossier d on a.dossier = d
            where (:codComposante is null or a.dossier.codComposante  = :codComposante)
            and a.statusAmenagement = 'VISE_ADMINISTRATION'
            and (d.year < :yearFilter)
            and (select count(*) from Dossier d1 where d1.individu = d.individu and a member of d1.amenagementsPortes) = 0
            and a.typeAmenagement = 'CURSUS'
            and (d.individu.desinscrit is null or d.individu.desinscrit = false)
            """)
    Page<Amenagement> findByPortable(String codComposante, Integer yearFilter, Pageable pageable);

    @Query("""
            select distinct a from Amenagement a
            join Dossier d on a.dossier = d
            where
            ((upper(a.dossier.individu.firstName) like upper(concat('%', :fullTextSearch)))
            or (upper(concat(a.dossier.individu.name, ' ', a.dossier.individu.firstName)) like upper(concat('%', :fullTextSearch, '%')))
            or (upper(a.dossier.individu.numEtu) like upper(concat('%', :fullTextSearch, '%')))
            or (upper(concat(a.dossier.individu.firstName, ' ', a.dossier.individu.name)) like upper(concat('%', :fullTextSearch, '%'))))
            and (:codComposante is null or a.dossier.codComposante  = :codComposante)
            and a.statusAmenagement = 'VISE_ADMINISTRATION'
            and (d.year < :yearFilter)
            and (select count(*) from Dossier d1 where d1.individu = d.individu and a member of d1.amenagementsPortes) = 0
            and a.typeAmenagement = 'CURSUS'
            and (d.individu.desinscrit is null or d.individu.desinscrit = false)
            """)
    Page<Amenagement> findByFullTextSearchPortable(String fullTextSearch, String codComposante, Integer yearFilter, Pageable pageable);

    @Query("""
            select count(distinct a) from Amenagement a
            left join Dossier d on a.dossier = d
            where a.statusAmenagement = 'VISE_ADMINISTRATION'
            and (d.year < :currentYear)
            and (select count(*) from Dossier d1 where d1.individu = d.individu and a member of d1.amenagementsPortes) = 0
            and a.typeAmenagement = 'CURSUS'
            and (d.individu.desinscrit is null or d.individu.desinscrit = false)
            """)
    Long countToPorte(Integer currentYear);

    @Query(value = "select a from Amenagement a " +
            "join Dossier d on a.dossier = d " +
            "join Individu i on d.individu = i " +
            "where " +
            "i = :individu " +
            "and a.statusAmenagement = 'VISE_ADMINISTRATION' " +
            "and (d.year < :yearFilter)")
    List<Amenagement> findAmenagementPrec(Individu individu, Integer yearFilter);

    @Query(value = "select a from Amenagement a " +
            "where " +
            "(:statusAmenagement is null or a.statusAmenagement = :statusAmenagement) " +
            "and (:codComposante is null or a.dossier.codComposante  = :codComposante) " +
            "order by a.administrationDate desc")
    Page<Amenagement> findByFullTextSearchAdmin(StatusAmenagement statusAmenagement, String codComposante, Pageable pageable);


    @Query("""
            select distinct a from Amenagement a join Dossier d on (a.dossier = d or a member of d.amenagementsPortes) join Individu i on d.individu = i
            where (:statusAmenagement is null or a.statusAmenagement = :statusAmenagement)
            and d.codComposante in (:codComposantes)
            and (:campus is null or a.dossier.campus = :campus)
            and (:viewedByUid is null or :viewedByUid member of a.viewByUid)
            and (:notViewedByUid is null or :notViewedByUid not member of a.viewByUid)
            and a.statusAmenagement = 'VISE_ADMINISTRATION'
            and (a.typeAmenagement = 'CURSUS' or a.typeAmenagement = 'DATE' and a.endDate >= current_date)
            and (:yearFilter is null or d.year = :yearFilter)
            """)
    Page<Amenagement> findByFullTextSearchScol(StatusAmenagement statusAmenagement, List<String> codComposantes, String campus, String viewedByUid, String notViewedByUid, Integer yearFilter, Pageable pageable);

    @Query("""
            select a from Amenagement a join Dossier d on (a.dossier = d or a member of d.amenagementsPortes) join Individu i on d.individu = i
            where ((upper(i.firstName) like upper(concat('%', :fullTextSearch)))
            or (upper(concat(i.name, ' ', i.firstName)) like upper(concat('%', :fullTextSearch, '%')))
            or (upper(i.numEtu) like upper(concat('%', :fullTextSearch, '%')))
            or (upper(concat(i.firstName, ' ', i.name)) like upper(concat('%', :fullTextSearch, '%'))))
            and (d.year = :yearFilter)
            and (d.codComposante in (:codComposantes))
            and (:campus is null or d.campus = :campus)
            and (:viewedByUid is null or :viewedByUid member of a.viewByUid)
            and (:notViewedByUid is null or :notViewedByUid not member of a.viewByUid)
            and (a.typeAmenagement = 'CURSUS' or a.typeAmenagement = 'DATE' and a.endDate >= current_date)
            and a.statusAmenagement = :statusAmenagement
            """)
    Page<Amenagement> findByIndividuNameScol(String fullTextSearch, StatusAmenagement statusAmenagement, Integer yearFilter, List<String> codComposantes, String campus, String viewedByUid, String notViewedByUid, Pageable pageable);

    @Query("""
        select a from Amenagement a where a.certificatSignatureId is not null and a.statusAmenagement = 'VISE_ADMINISTRATION' and a.individuSendDate is null and (a.typeAmenagement = 'CURSUS' or a.endDate >= :date)
        """)
    List<Amenagement> findAmenagementToResend(LocalDateTime date);
}
