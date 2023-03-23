package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.dtos.csvs.DossierCompletCSVDto;
import org.esupportail.esupagape.entity.Dossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExportRepository extends JpaRepository <Dossier, Long> {
    @Query("""
            select YEAR(i.dateOfBirth) as yearOfBirth,
            i.gender as gender,
            i.fixCP as fixCP,
            i.fixCity as fixCity,
            i.fixCountry as fixCountry,
            d.type as type, 
            d.statusDossier as statusDossier,
            d.statusDossierAmenagement as statusDossierAmenagement,
            c as classification,
            d.mdph as mdph,
            d.taux as taux,
            t as typeSuiviHandisup,
            d.typeFormation as typeFormation,
            d.modeFormation as modeFormation,
            d.site as site,
            d.libelleFormation as libelleFormation,
            d.libelleFormationPrec as libelleFormationPrec,
            d.codComposante as codComposante,
            d.composante as composante,
            d.formAddress as formAddress,
            d.resultatTotal as resultatTotal,
            d.suiviHandisup as suiviHandisup
            from Dossier d join Individu i on d.individu.id = i.id
            left join d.classification c
            left join d.typeSuiviHandisup t
            where (:year is null or d.year = :year)
             order by i.dateOfBirth desc """)
    List<DossierCompletCSVDto> findByYearForCSV(Integer year);
}
