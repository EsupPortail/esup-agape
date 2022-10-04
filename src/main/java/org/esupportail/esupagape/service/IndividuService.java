package org.esupportail.esupagape.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.ExcludeIndividu;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.repository.ExcludeIndividuRepository;
import org.esupportail.esupagape.repository.IndividuRepository;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuSourceService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class IndividuService {

    private static final Logger logger = LoggerFactory.getLogger(IndividuService.class);

    private final List<IndividuSourceService> individuSourceServices;

    @Resource
    private IndividuRepository individuRepository;

    @Resource
    private UtilsService utilsService;

    @Resource
    private ExcludeIndividuRepository excludeIndividuRepository;

    @Resource
    private DossierService dossierService;

    public IndividuService(List<IndividuSourceService> individuSourceServices) {
        this.individuSourceServices = individuSourceServices;
    }

    public Individu getIndividu(String numEtu) {
        return individuRepository.findByNumEtu(numEtu);
    }

    public Individu getIndividu(String name, String firstName, LocalDate dateOfBirth) {
        return individuRepository.findByNameIgnoreCaseAndFirstNameIgnoreCaseAndDateOfBirth(name, firstName, dateOfBirth);
    }

    public List<Individu> getAllIndividus() {
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
        List<Individu> individus = individuRepository.findAll();
        List<ExcludeIndividu> excludeIndividus = excludeIndividuRepository.findAll();
        for (IndividuSourceService individuSourceService : individuSourceServices) {
            List<Individu> individusFromSource = individuSourceService.getAllIndividuNums();
            List<Individu> individusToCreate = individusFromSource.stream().filter(
                    individuToCreate -> individus.stream().noneMatch(individuInDataBase -> individuInDataBase.getNumEtu().equals(individuToCreate.getNumEtu()))
                    &&
                    excludeIndividus.stream().noneMatch(excludeIndividu -> excludeIndividu.getNumEtuHash().equals(new DigestUtils("SHA3-256").digestAsHex(individuToCreate.getNumEtu())))
            ).toList();
            individuRepository.saveAll(individusToCreate);
            List<Individu> individusWithoutDossier = new ArrayList<>();
            individusWithoutDossier.addAll(individusToCreate);
            individusWithoutDossier.addAll(individus.stream().filter(individu -> individu.getDossiers().stream().noneMatch(dossier -> dossier.getYear() == utilsService.getCurrentYear())).toList());
            List<Dossier> dossiers = new ArrayList<>();
            for(Individu individu : individusWithoutDossier) {
                Dossier dossier = dossierService.create(individu);
                dossiers.add(dossier);
                individu.getDossiers().add(dossier);
            }
            dossierService.saveAll(dossiers);
        }
        logger.info("Import individus done");
    }

    public void save(Individu individuToAdd, String force) throws AgapeJpaException {
        ExcludeIndividu excludeIndividu = excludeIndividuRepository.findByNumEtuHash(new DigestUtils("SHA3-256").digestAsHex(individuToAdd.getNumEtu()));
        if (force == null && excludeIndividu != null) {
            throw new AgapeJpaException("L'étudiant est dans la liste d'exclusion");
        }
        Individu foundIndividu = null;
        if (!individuToAdd.getNumEtu().isEmpty()) {
            foundIndividu = individuRepository.findByNumEtu(individuToAdd.getNumEtu());
        } else {
            //numEtu à null pour éviter la contrainte d'unicité
            individuToAdd.setNumEtu(null);
        }
        if (foundIndividu != null) {
            dossierService.create(foundIndividu);
        } else {
            if (excludeIndividu != null) {
                // suppression de l'exclusion si l'insertion est forcée
                excludeIndividuRepository.delete(excludeIndividu);
            }
            individuRepository.save(individuToAdd);
            dossierService.create(individuToAdd);
        }
    }

    public Individu findById(Long id) throws AgapeException {
        Optional<Individu> optionalIndividu = individuRepository.findById(id);
        if (optionalIndividu.isPresent()) {
            return optionalIndividu.get();
        } else {
            throw new AgapeException("Je n'ai pas trouvé cet individu");
        }
    }

    public Page<Individu> getAllIndividus(Pageable pageable) {
        return individuRepository.findAll(pageable);
    }

    public Page<Individu> searchByName(String name, Pageable pageable) {
        return individuRepository.findAllByNameContainsIgnoreCase(name, pageable);
    }

    public Individu create(Individu individu, String force) throws AgapeJpaException {
        Individu individuTestIsExist = null;
        if (!individu.getNumEtu().isEmpty()) {
            individuTestIsExist = getIndividu(individu.getNumEtu());
            if (individuTestIsExist == null) {
                return createFromSources(individu.getNumEtu(), force);
            }
        } else if (!individu.getName().isEmpty() && !individu.getFirstName().isEmpty() && individu.getDateOfBirth() != null) {
            individuTestIsExist = getIndividu(individu.getName(), individu.getFirstName(), individu.getDateOfBirth());
            if (individuTestIsExist == null) {
                Individu newIndividu = createFromSources(individu.getName(), individu.getFirstName(), individu.getDateOfBirth(), force);
                if (newIndividu != null) {
                    return newIndividu;
                } else {
                    save(individu, force);
                    return individu;
                }
            }
        }
        if (individuTestIsExist != null) {
            return individuTestIsExist;
        } else if (!individu.getName().isEmpty() && !individu.getFirstName().isEmpty() && individu.getDateOfBirth() != null && !individu.getSex().isEmpty()) {
            save(individu, force);
        }
        return individu;
    }

    public Individu createFromSources(String numEtu, String force) throws AgapeJpaException {
        Individu individuFromSources = null;
        for (IndividuSourceService individuSourceService : individuSourceServices) {
            individuFromSources = individuSourceService.getIndividuByNumEtu(numEtu);
            if (individuFromSources != null) {
                break;
            }
        }
        if (individuFromSources != null) {
            save(individuFromSources, force);
        }
        return individuFromSources;
    }

    public Individu createFromSources(String name, String firstName, LocalDate dateOfBirth, String force) throws AgapeJpaException {
        Individu individuFromSources = null;
        for (IndividuSourceService individuSourceService : individuSourceServices) {
            individuFromSources = individuSourceService.getIndividuByProperties(name, firstName, dateOfBirth);
            if (individuFromSources != null) {
                break;
            }
        }
        if (individuFromSources != null) {
            save(individuFromSources, force);
        }
        return individuFromSources;
    }

    public Individu getById(Long id) {
        return individuRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void deleteIndividu(long id) {
        Individu individu = getById(id);
        if (individu.getNumEtu() != null && !individu.getNumEtu().isEmpty()) {
            ExcludeIndividu excludeIndividu = new ExcludeIndividu();
            excludeIndividu.setNumEtuHash(new DigestUtils("SHA3-256").digestAsHex(individu.getNumEtu()));
            excludeIndividuRepository.save(excludeIndividu);
        }
        this.individuRepository.delete(individu);
    }
}
