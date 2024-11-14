package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogRepository extends JpaRepository <Log, Long> {
    List<Log> findByDossierId(Long dossierId);
}
