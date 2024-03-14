package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.EnqueteEnumFilFmtScoLibelle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EnqueteEnumFilFmtScoLibelleRepository extends JpaRepository <EnqueteEnumFilFmtScoLibelle, Long> {
    @Query("select libelle from EnqueteEnumFilFmtScoLibelle  where upper(cod) = :cod")
    String findByCod(String cod);
}
