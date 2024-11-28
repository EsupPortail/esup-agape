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

    @Query("select da.amenagement from DossierAmenagement da where da.dossier.id = :dossierId and da.amenagement.statusAmenagement = :statusAmenagement order by da.amenagement.administrationDate desc")
    List<Amenagement> findByDossierIdAndStatusAmenagement(Long dossierId, StatusAmenagement statusAmenagement);

    @Query("select distinct a from Amenagement a join DossierAmenagement da on da.amenagement = a " +
            "where " +
            "(:statusAmenagement is null or a.statusAmenagement = :statusAmenagement) " +
            "and (:codComposante is null or da.dossier.codComposante  = :codComposante) " +
            "and a.statusAmenagement != 'BROUILLON' and a.statusAmenagement != 'SUPPRIME'" +
            "and (:yearFilter is null or da.lastYear = :yearFilter)")
    Page<Amenagement> findByFullTextSearch(StatusAmenagement statusAmenagement, String codComposante, Integer yearFilter, Pageable pageable);


    @Query("select a from Amenagement a join DossierAmenagement da on da.amenagement = a join Individu i on da.dossier.individu = i " +
            "where (upper(i.firstName) like upper(concat('%', :fullTextSearch))) " +
            "or (upper(concat(i.name, ' ', i.firstName)) like upper(concat('%', :fullTextSearch, '%'))) " +
            "or (upper(concat(i.firstName, ' ', i.name)) like upper(concat('%', :fullTextSearch, '%'))) " +
            "or (upper(da.dossier.individu.numEtu) = upper(:fullTextSearch)) " +
            "or (upper(da.dossier.individu.codeIne) = upper(:fullTextSearch)) " +
            "and (da.lastYear < :yearFilter) " +
            "and da.dossier.individu.desinscrit != true " +
            "and a.statusAmenagement = 'VISE_ADMINISTRATION' " +
            "and a.typeAmenagement = 'CURSUS'")
    Page<Amenagement> findByIndividuNamePortable(@Param("fullTextSearch") String fullTextSearch,
                                                 @Param("yearFilter") Integer yearFilter,
                                                 Pageable pageable);


    @Query("select count(a) from Amenagement a join DossierAmenagement da on da.amenagement = a " +
            "where " +
            "a.statusAmenagement = 'VALIDE_MEDECIN' " +
            "and (:yearFilter is null or da.lastYear = :yearFilter)")
    Long countToValidate(Integer yearFilter);

    @Query("""
            select distinct a from Amenagement a
            join DossierAmenagement da on da.amenagement = a
            where (:codComposante is null or da.dossier.codComposante  = :codComposante)
            and a.statusAmenagement = 'VISE_ADMINISTRATION'
            and (da.lastYear < :yearFilter)
            and a not in (select da1.amenagement from DossierAmenagement da1 where da1.lastYear = :yearFilter)
            and a.typeAmenagement = 'CURSUS'
            and (da.dossier.individu.desinscrit is null or da.dossier.individu.desinscrit = false)
            """)
    Page<Amenagement> findByPortable(String codComposante, Integer yearFilter, Pageable pageable);

    @Query("""
            select count(distinct a) from Amenagement a
            left join DossierAmenagement da on da.amenagement = a
            where a.statusAmenagement = 'VISE_ADMINISTRATION'
            and (da.lastYear < :currentYear)
            and a not in (select da1.amenagement from DossierAmenagement da1 where da1.lastYear = :currentYear)
            and a.typeAmenagement = 'CURSUS'
            and (da.dossier.individu.desinscrit is null or da.dossier.individu.desinscrit = false)
            """)
    Long countToPorte(Integer currentYear);

    @Query("""
            select distinct a from Amenagement a
            join DossierAmenagement da on da.amenagement = a
            where
            ((upper(da.dossier.individu.firstName) like upper(concat('%', :fullTextSearch)))
            or (upper(concat(da.dossier.individu.name, ' ', da.dossier.individu.firstName)) like upper(concat('%', :fullTextSearch, '%')))
            or (upper(da.dossier.individu.numEtu) like upper(concat('%', :fullTextSearch, '%')))
            or (upper(concat(da.dossier.individu.firstName, ' ', da.dossier.individu.name)) like upper(concat('%', :fullTextSearch, '%'))))
            and (:codComposante is null or da.dossier.codComposante  = :codComposante)
            and a.statusAmenagement = 'VISE_ADMINISTRATION'
            and (da.lastYear < :yearFilter)
            and a.typeAmenagement = 'CURSUS'
            and (da.dossier.individu.desinscrit is null or da.dossier.individu.desinscrit = false)
            """)
    Page<Amenagement> findByFullTextSearchPortable(String fullTextSearch, String codComposante, Integer yearFilter, Pageable pageable);

    @Query(value = "select a from Amenagement a " +
            "join DossierAmenagement da on da.amenagement = a " +
            "join Individu i on da.dossier.individu = i " +
            "where " +
            "i = :individu " +
            "and a.statusAmenagement = 'VISE_ADMINISTRATION' " +
            "and (da.lastYear < :yearFilter)")
    List<Amenagement> findAmenagementPrec(Individu individu, Integer yearFilter);

    @Query(value = "select da.amenagement from DossierAmenagement da " +
            "where " +
            "(:statusAmenagement is null or da.amenagement.statusAmenagement = :statusAmenagement) " +
            "and (:codComposante is null or da.dossier.codComposante  = :codComposante) " +
            "order by da.amenagement.administrationDate desc")
    Page<Amenagement> findByFullTextSearchAdmin(StatusAmenagement statusAmenagement, String codComposante, Pageable pageable);


    @Query("""
            select a from Amenagement a join DossierAmenagement da on da.amenagement = a join Individu i on da.dossier.individu = i
            where (:statusAmenagement is null or a.statusAmenagement = :statusAmenagement)
            and da.dossier.codComposante in (:codComposantes)
            and (:campus is null or :campus member of da.dossier.campus)
            and (:viewedByUid is null or :viewedByUid member of a.viewByUid)
            and (:notViewedByUid is null or :notViewedByUid not member of a.viewByUid)
            and a.statusAmenagement = 'VISE_ADMINISTRATION'
            and (a.typeAmenagement = 'CURSUS' or a.typeAmenagement = 'DATE' and a.endDate >= current_date)
            and (:yearFilter is null or da.lastYear = :yearFilter)
            """)
    Page<Amenagement> findByFullTextSearchScol(StatusAmenagement statusAmenagement, List<String> codComposantes, String campus, String viewedByUid, String notViewedByUid, Integer yearFilter, Pageable pageable);

    @Query("""
            select a from Amenagement a join DossierAmenagement da on da.amenagement = a join Individu i on da.dossier.individu = i
            where ((upper(i.firstName) like upper(concat('%', :fullTextSearch)))
            or (upper(concat(i.name, ' ', i.firstName)) like upper(concat('%', :fullTextSearch, '%')))
            or (upper(i.numEtu) like upper(concat('%', :fullTextSearch, '%')))
            or (upper(concat(i.firstName, ' ', i.name)) like upper(concat('%', :fullTextSearch, '%'))))
            and (da.lastYear = :yearFilter)
            and (da.dossier.codComposante in (:codComposantes))
            and (:campus is null or :campus member of da.dossier.campus)
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

    @Query("""
            select a from Amenagement a
            where a.statusAmenagement = 'VISE_ADMINISTRATION'
            and (a.typeAmenagement = 'CURSUS' or a.endDate > CURRENT_TIMESTAMP)
            """)
    List<Amenagement> findDossierAmenagementToSync();
}
