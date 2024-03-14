package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.EnqueteEnumFilFmtSco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EnqueteEnumFilFmtScoRepository extends JpaRepository <EnqueteEnumFilFmtSco, Long> {
    @Query("select distinct upper(codFil) from EnqueteEnumFilFmtSco where codSco is null ")
    List<String> findDistinctByCodScoIsNull();
    @Query("select distinct upper(codFmt) from EnqueteEnumFilFmtSco where upper(codFil) = :codFil")
    List<String> findDistinctByCodFil(String codFil);
    @Query("select distinct upper(codSco) from EnqueteEnumFilFmtSco where upper(codFmt) = :codFmt")
    List<String> findDistinctByCodFmt(String codFmt);
}
