package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Individu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface IndividuRepository extends JpaRepository<Individu, Long> {

    Individu findByNumEtu(String numEtu);

    Individu findByNameIgnoreCaseAndFirstNameIgnoreCaseAndDateOfBirth(String name, String firstName, LocalDate dateOfBirth);

    Page<Individu> findAllByNameContainsIgnoreCase(String name, Pageable pageable);

    @Query("select distinct i.fixCP as fixCP from Individu i group by i.fixCP order by fixCP asc")
    List<String> findAllFixCP();

    @Query(value = "select date_part('Year', date_of_birth) from individu group by date_part('Year', date_of_birth) order by date_part('Year', date_of_birth)", nativeQuery = true)
    List<Integer> findAllDateOfBirthDistinct();

    @Query("select i.id from Individu i")
    List<Long> findIdsAll();

}
