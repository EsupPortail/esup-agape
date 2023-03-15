package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.dtos.EntretienAttachementDto;
import org.esupportail.esupagape.entity.Entretien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EntretienRepository extends JpaRepository <Entretien, Long> {

    Page<Entretien> findEntretiensByDossierId(Long dossierId, Pageable pageable);

    @Query("select e.id as id, " +
            "e.date as date, " +
            "e.typeContact as typeContact, " +
            "e.interlocuteur as interlocuteur, " +
            "e.compteRendu as compteRendu, " +
            "coalesce(count(d.id), 0) as numberOfAttachements " +
            "from Entretien e " +
            "left join Document d on e.id = d.parentId " +
            "where e.dossier.id = :dossierId " +
            "group by e.id")
    Page<EntretienAttachementDto> findEntretiensWithAttachementsByDossierId(Long dossierId, Pageable pageable);

}
