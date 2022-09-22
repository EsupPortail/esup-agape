package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.repository.IndividuRepository;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
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

    public Individu getIndividu(String numEtu) {
        return individuRepository.findByNumEtu(numEtu);
    }

    public Individu getIndividu(String name, String firstName, LocalDate dateOfBirth) {
        return individuRepository.findByNameIgnoreCaseAndFirstNameIgnoreCaseAndDateOfBirth(name, firstName, dateOfBirth);
    }

    public List<Individu> getAllIndividus(){
        return individuRepository.findAll();
    }

    @Transactional
    public void syncIndividu(Long id) {
        Individu individu = individuRepository.findById(id).orElseThrow();
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
        logger.info("Sync individus done");
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
        logger.info("Import individus done");
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

    public Page<Individu> getAllIndividus(Pageable pageable) {
        return individuRepository.findAll(pageable);
    }

    public Page<Individu> searchByName(String name, Pageable pageable) {
        return individuRepository.findAllByNameContainsIgnoreCase(name, pageable);
    }

    public Individu create(Individu individu) {
        Individu individuTestIsExist = null;
        if(!individu.getNumEtu().isEmpty()) {
             individuTestIsExist = getIndividu(individu.getNumEtu());
             if(individuTestIsExist == null) {
                 return createFromSources(individu.getNumEtu());
             }
        } else if(!individu.getName().isEmpty() && !individu.getFirstName().isEmpty() && individu.getDateOfBirth() != null) {
            individuTestIsExist = getIndividu(individu.getName(), individu.getFirstName(), individu.getDateOfBirth());
            if(individuTestIsExist == null) {
                return createFromSources(individu.getName(), individu.getFirstName(), individu.getDateOfBirth());
            }
        }
        if(individuTestIsExist != null) {
            return individuTestIsExist;
        } else if(!individu.getName().isEmpty() && !individu.getFirstName().isEmpty() && individu.getDateOfBirth() != null && !individu.getSex().isEmpty()) {
            save(individu);
        }
        return individu;
    }

    public Individu createFromSources(String numEtu) {
        Individu individuFromSources = null;
        for (IndividuSourceService individuSourceService : individuSourceServices) {
            individuFromSources = individuSourceService.getIndividuByNumEtu(numEtu);
            if(individuFromSources != null) {
                break;
            }
        }
        if (individuFromSources != null) {
            save(individuFromSources);
        }
        return individuFromSources;
    }

    public Individu createFromSources(String name, String firstName, LocalDate dateOfBirth) {
        Individu individuFromSources = null;
        for (IndividuSourceService individuSourceService : individuSourceServices) {
            individuFromSources = individuSourceService.getIndividuByProperties(name, firstName, dateOfBirth);
            if(individuFromSources != null) {
                break;
            }
        }
        if (individuFromSources != null) {
            save(individuFromSources);
        }
        return individuFromSources;
    }

    public Individu getById(Long id) {
        return individuRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void deleteIndividu(long id){
        this.individuRepository.deleteById(id);
    }
}
