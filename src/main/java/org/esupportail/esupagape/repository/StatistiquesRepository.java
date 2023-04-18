package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.dtos.charts.ClassificationChart;
import org.esupportail.esupagape.dtos.charts.ComposanteChart;
import org.esupportail.esupagape.entity.Dossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatistiquesRepository extends JpaRepository<Dossier, Long> {

    @Query(value = """
            select dc.classification as classification, count(dc.classification) as classificationCount from dossier d
            join dossier_classification dc on d.id = dc.dossier_id
            where d.year = :year
            group by dc.classification order by classificationCount desc ;
            """, nativeQuery = true)
    List<ClassificationChart> countFindClassificationByYear(Integer year);

    @Query(value = """
            select d.composante as composante, count(d.composante) as composanteCount 
            from  Dossier d 
            where d.year = :year
            group by d.composante order by composanteCount desc 
            """, nativeQuery = true)
    List<ComposanteChart> countFindComposanteByYear(Integer year);

    @Query(value = """
            select count(distinct i.id) 
            from Individu i
            inner join Dossier d on d.individu_id = i.id
            where d.year = :year
            """, nativeQuery = true)
    Long countFindIndividuByYear(@Param("year") Integer year);


    @Query("""
            SELECT DISTINCT year FROM Dossier """)
    List<Integer> findDistinctYears();
}
