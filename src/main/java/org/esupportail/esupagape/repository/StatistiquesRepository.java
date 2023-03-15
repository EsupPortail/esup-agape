package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.dtos.charts.ClassificationChart;
import org.esupportail.esupagape.entity.Dossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StatistiquesRepository extends JpaRepository <Dossier, Long> {

    @Query(value = """
            select dc.classification as classification, count(dc.classification) as classificationCount from dossier d
            join dossier_classification dc on d.id = dc.dossier_id
            where d.year = '2022'
            group by dc.classification order by classificationCount desc ;
            """, nativeQuery = true)
    List<ClassificationChart> countFindClassificationByYear(Integer year);

}
