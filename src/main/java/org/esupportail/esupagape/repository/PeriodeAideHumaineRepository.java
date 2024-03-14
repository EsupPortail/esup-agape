package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.PeriodeAideHumaine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeriodeAideHumaineRepository extends JpaRepository <PeriodeAideHumaine, Long> {

    List<PeriodeAideHumaine> findByAideHumaineId(Long aideHumaineId);

}
