package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.EnqueteEnumFilFmtSco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EnqueteEnumFilFmtScoRepository extends JpaRepository <EnqueteEnumFilFmtSco, Long> {
    @Query("select distinct upper(codFil) from EnqueteEnumFilFmtSco where codFil is not null")
    List<String> findCodFils();
    @Query("select distinct upper(codFmt) from EnqueteEnumFilFmtSco where codFmt is not null")
    List<String> findCodFmts();
    @Query("select distinct upper(codSco) from EnqueteEnumFilFmtSco where codSco is not null")
    List<String> findCodScos();
}
