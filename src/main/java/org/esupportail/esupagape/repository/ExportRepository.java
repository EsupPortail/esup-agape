package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.dtos.csvs.DossierCompletCsvDto;
import org.esupportail.esupagape.entity.Dossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExportRepository extends JpaRepository <Dossier, Long> {
    @Query(value = """
            select to_char(i.date_of_birth, 'YYYY') as yearOfBirth,
                   cast(i.gender AS VARCHAR) as gender,
                   cast(i.fixcp AS VARCHAR) as fixCP,
                   cast(i.fix_city AS VARCHAR) as fixCity,
                   cast(i.fix_country AS VARCHAR) as fixCountry,
                   cast(d.type AS VARCHAR) as type,
                   cast(d.status_dossier AS VARCHAR) as statusDossier,
                   cast(d.status_dossier_amenagement AS VARCHAR) as statusDossierAmenagement,
                   cast((select string_agg(distinct c.classifications, ',') from dossier_classifications as c where c.dossier_id = d.id) AS VARCHAR) as classifications,
                   cast((select string_agg(distinct m.mdphs, ',') from dossier_mdphs as m where m.dossier_id = d.id) AS VARCHAR) as mdph,
                   cast(d.taux AS VARCHAR) as taux,
                   cast((select string_agg(distinct t.type_suivi_handisup, ',') from dossier_type_suivi_handisup as t where t.dossier_id = d.id) AS VARCHAR) as typeSuiviHandisup,
                   cast(d.type_formation AS VARCHAR) as typeFormation,
                   cast(d.mode_formation AS VARCHAR) as modeFormation,
                   cast(d.libelle_formation AS VARCHAR) as libelleFormation,
                   cast(d.libelle_formation_prec AS VARCHAR) as libelleFormationPrec,
                   cast(d.cod_composante AS VARCHAR) as codComposante,
                   cast(d.composante AS VARCHAR) as composante,
                   cast(d.form_address AS VARCHAR) as formAddress,
                   cast(d.resultat_total AS VARCHAR) as resultatTotal,
                   cast(d.suivi_handisup AS VARCHAR) as suiviHandisup
            from dossier as d
                     join individu as i on d.individu_id = i.id
            where (:year is null or d.year = :year)
            order by i.date_of_birth desc
            """, nativeQuery = true)
    List<DossierCompletCsvDto> findByYearForCSV(Integer year);
}
