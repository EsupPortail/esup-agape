package org.esupportail.esupagape.service.scheduler;

import org.esupportail.esupagape.service.IndividuService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@EnableScheduling
@Service
public class SchedulerService {

    @Resource
    private IndividuService individuService;

//    @Scheduled(initialDelay = 1, fixedRate = 86400000)
    public void importIndividus() {
        individuService.importIndividus();
        individuService.syncAllIndividus();
    }

}
