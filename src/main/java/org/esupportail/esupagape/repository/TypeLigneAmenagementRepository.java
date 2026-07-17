package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.TypeLigneAmenagement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TypeLigneAmenagementRepository extends JpaRepository<TypeLigneAmenagement, Long> {

    List<TypeLigneAmenagement> findByYearOrderByOrdreAsc(Integer year);

    List<TypeLigneAmenagement> findByYearAndActifTrueOrderByOrdreAsc(Integer year);
}