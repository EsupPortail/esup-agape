package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.dtos.DossierCompletCSVDto;
import org.esupportail.esupagape.entity.Dossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExportRepository extends JpaRepository <Dossier, Long> {
    @Query("""
            select distinct d.id as id, 
            d.year as year,
            i.numEtu as numEtu, 
            i.codeIne as codeIne,
            i.gender as gender,
            i.name as name, 
            i.firstName as firstName, 
            i.dateOfBirth as dateOfBirth,
            i.emailEtu as emailEtu,
            i.fixAddress as fixAddress,
            i.fixCP as fixCP,
            i.fixCity as fixCity,
            i.fixCountry as fixCountry,
            d.type as type, 
            d.statusDossier as statusDossier,
            d.statusDossierAmenagement as statusDossierAmenagement,
            d.formAddress as formAddress
            from Dossier d join Individu i on d.individu.id = i.id 
            where (:year is null or d.year = :year) 
            order by year desc, name
            """)
    List<DossierCompletCSVDto> findByYearForCSV(Integer year);

    @Query("""
            select i.emailEtu as emailEtu,
            i.emailPerso as emailPerso
            from Dossier d join Individu i on d.individu.id = i.id
            where (:year is null or d.year = :year)
            order by i.name
            """)
    List<DossierCompletCSVDto> findEmailEtuByYearForCSV(Integer year);
}
