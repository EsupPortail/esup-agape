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
            i.emailPerso as emailPerso,
            i.fixAddress as fixAddress,
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
            ah as fonctionAidant,
            am as typeEpreuves,
            d.commentaire as commentaire,
            d.typeFormation as typeFormation,
            d.modeFormation as modeFormation,
            d.site as site,
            d.libelleFormation as libelleFormation,
            d.libelleFormationPrec as libelleFormationPrec,
            d.codComposante as codComposante,
            d.composante as composante,
            d.formAddress as formAddress,
            d.resultatS1 as resultatS1,
            d.noteS1 as noteS1,
            d.resultatS2 as resultatS2,
            d.noteS2 as noteS2, 
            d.resultatTotal as resultatTotal,
            d.noteTotal as noteTotal,
            d.suiviHandisup as suiviHandisup,
            ah.numEtuAidant as numEtuAidant,
            ah.statusAideHumaine as statusAideHumaine,
            ah.nameAidant as nameAidant,
            ah.firstNameAidant as firstNameAidant,
            ah.dateOfBirthAidant as dateOfBirthAidant,
            ah.emailAidant as emailAidant,
            ah.phoneAidant as phoneAidant,
            ah.startDate as startDate,
            amat.typeAideMaterielle as typeAideMaterielle,
            amat.endDate as endDate,
            amat.cost as cost,
            amat.comment as comment,
            am.autorisation as autorisation,
            am.statusAmenagement as statusAmenagement,
            am.typeAmenagement as typeAmenagement,
            am.createDate as createDate,
            am.autresTypeEpreuve as autresTypeEpreuve,
            am.tempsMajore as tempsMajore,
            am.autresTempsMajores as autresTempsMajores,
            am.amenagementText as amenagementText,
            am.nomMedecin as nomMedecin,
            am.mailMedecin as mailMedecin,
            am.nomValideur as nomValideur,
            am.mailValideur as mailValideur,
            am.mailIndividu as mailIndividu,
            am.motifRefus as motifRefus,
            am.valideMedecinDate as valideMedecinDate,
            am.administrationDate as administrationDate,
            am.deleteDate as deleteDate
            from Dossier d join Individu i on d.individu.id = i.id
            left join d.classification c
            left join d.typeSuiviHandisup t
            left join d.aidesHumaines ah
            left join d.aidesMaterielles amat
            left join d.amenagements am
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
