package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.repository.IndividuRepository;
import org.esupportail.esupagape.service.interfaces.importIndividu.IndividuSourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class IndividuService {

    private final List<IndividuSourceService> individuSourceServices;

    @Resource
    private IndividuRepository individuRepository;

    public IndividuService(List<IndividuSourceService> individuSourceServices) {
        this.individuSourceServices = individuSourceServices;
    }

    @Transactional
    public void importIndividus() {
        for (IndividuSourceService individuSourceService : individuSourceServices) {
            List<Individu> individus = individuSourceService.getAllIndividuNums();
            for (Individu individu : individus) {
                Individu individu1 = individuRepository.findByNumEtu(individu.getNumEtu());
                if(individu1 == null) {
                    individuRepository.save(individu);
                }
            }
        }
    }

}
