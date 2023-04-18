package org.esupportail.esupagape.service.scheduler;

import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.IndividuService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

@EnableScheduling
@Service
public class SchedulerService {

    private final IndividuService individuService;

    private final DossierService dossierService;

    public SchedulerService(IndividuService individuService, DossierService dossierService) {
        this.individuService = individuService;
        this.dossierService = dossierService;
    }

    //    @Scheduled(initialDelay = 1, fixedRate = 86400000)
    public void importIndividus() {
        individuService.importIndividus();
        individuService.syncAllIndividus();
        dossierService.syncAllDossiers();
    }

}
