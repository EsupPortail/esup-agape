package org.esupportail.esupagape.service.scheduler;

import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.repository.DossierRepository;
import org.esupportail.esupagape.service.AmenagementService;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.IndividuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@EnableScheduling
@Service
public class SchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    private final IndividuService individuService;

    private final DossierService dossierService;

    private final AmenagementService amenagementService;

    private final DossierRepository dossierRepository;

    public SchedulerService(IndividuService individuService, DossierService dossierService, AmenagementService amenagementService, DossierRepository dossierRepository) {
        this.individuService = individuService;
        this.dossierService = dossierService;
        this.amenagementService = amenagementService;
        this.dossierRepository = dossierRepository;
    }

    //    @Scheduled(initialDelay = 1, fixedRate = 86400000)
    public void importIndividus() {
        individuService.importIndividus();
        individuService.syncAllIndividus();
        dossierService.syncAllDossiers();
    }

    @Scheduled(initialDelay = 1, fixedRate = 30000)
    public void syncEsupSignature() throws AgapeException {
        logger.debug("Synchro Esup Signature");
        amenagementService.syncAllAmenagements();
        amenagementService.sendAllCertificats();
    }

}
