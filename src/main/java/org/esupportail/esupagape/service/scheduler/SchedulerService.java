package org.esupportail.esupagape.service.scheduler;

import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.service.AmenagementService;
import org.esupportail.esupagape.service.IndividuService;
import org.esupportail.esupagape.service.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@EnableScheduling
@Service
public class SchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    private final IndividuService individuService;

    private final SyncService syncService;

    private final AmenagementService amenagementService;

    private final ApplicationProperties applicationProperties;

    public SchedulerService(IndividuService individuService, SyncService syncService, AmenagementService amenagementService, ApplicationProperties applicationProperties) {
        this.individuService = individuService;
        this.syncService = syncService;
        this.amenagementService = amenagementService;
        this.applicationProperties = applicationProperties;
    }

    @Scheduled(cron="00 02 02 * * *")
    public void importIndividus() throws AgapeException, SQLException {
        if(applicationProperties.getEnableSchedulerIndividu()) {
            logger.info("Synchro individus");
            individuService.importIndividus();
            individuService.syncAllIndividus();
            syncService.syncAllDossiers();
            logger.info("Synchro individus terminée");
        }
    }

    @Scheduled(initialDelay = 1, fixedRate = 600000)
    public void syncEsupSignature() throws AgapeException {
        if(applicationProperties.getEnableSchedulerEsupSignature()) {
            logger.debug("Synchro Esup Signature");
            amenagementService.syncEsupSignatureAmenagements();
            logger.debug("Synchro Esup Signature terminée");
        }
    }

    @Scheduled(initialDelay = 1, fixedRate = 600000)
    public void syncAmenagements() {
        if(applicationProperties.getEnableSchedulerAmenagement()) {
            logger.info("Synchro Aménagements");
            amenagementService.syncAllAmenagments();
            logger.info("Synchro Aménagements terminée");
        }
    }

    @Scheduled(cron="00 02 02 * * *")
    public void anonymiseOldDossiers() {
        if(applicationProperties.getEnableSchedulerAnonymise()) {
            logger.info("Anonymisation des anciens dossiers ");
            individuService.anonymiseOldDossiers("scheduler");
            logger.info("Anonymisation des anciens dossiers terminée");
        }
    }

}
