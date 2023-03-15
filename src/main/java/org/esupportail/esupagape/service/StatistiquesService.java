package org.esupportail.esupagape.service;

import org.esupportail.esupagape.repository.StatistiquesRepository;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.springframework.stereotype.Service;

@Service
public class StatistiquesService {
    private final StatistiquesRepository statistiquesRepository;
    private final DossierService dossierService;
    private final UtilsService utilsService;

    public StatistiquesService(StatistiquesRepository statistiquesRepository, DossierService dossierService, UtilsService utilsService) {
        this.statistiquesRepository = statistiquesRepository;
        this.dossierService = dossierService;
        this.utilsService = utilsService;
    }


}
