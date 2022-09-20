package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.repository.IndividuRepository;
import org.esupportail.esupagape.service.interfaces.importIndividu.IndividuSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class IndividuService {

    private static final Logger logger = LoggerFactory.getLogger(IndividuService.class);

    private final List<IndividuSourceService> individuSourceServices;

    @Resource
    private IndividuRepository individuRepository;

    public IndividuService(List<IndividuSourceService> individuSourceServices) {
        this.individuSourceServices = individuSourceServices;
    }

    public List<Individu> getAllIndividus(){
        List<Individu> individus = new ArrayList<>();
        individuRepository.findAll().forEach(individus::add);
        return individus;
    }
    @Transactional
    public void syncIndividu(Long id) {
        Individu individu = individuRepository.findById(id).get();
        for (IndividuSourceService individuSourceService : individuSourceServices) {
            individuSourceService.updateIndividu(individu);
        }
    }

    @Transactional
    public void syncAllIndividus() {
        List<Individu> individus = individuRepository.findAll();
        for (Individu individu : individus) {
            syncIndividu(individu.getId());
        }
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

    public Individu findById(Long id) throws AgapeException {
        Optional<Individu> optionalIndividu = individuRepository.findById(id);
        if(optionalIndividu.isPresent()) {
            return optionalIndividu.get();
        } else {
            throw new AgapeException("Je n'ai pas trouvé cet individu");
        }
    }

    public void save(Individu individu) {
        individuRepository.save(individu);
    }
}
