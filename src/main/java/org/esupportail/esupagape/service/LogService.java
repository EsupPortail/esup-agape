package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.Log;
import org.esupportail.esupagape.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LogService {

    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Transactional
    public void create(String eppn, Long dossierId, String initalStatutDossier, String finalStatutDossier) {
        Log log = new Log();
        log.setDate(LocalDateTime.now());
        log.setEppn(eppn);
        log.setInitialStatusDossier(initalStatutDossier);
        log.setFinalStatusDossier(finalStatutDossier);
        log.setDossierId(dossierId);
        logRepository.save(log);
    }

}
