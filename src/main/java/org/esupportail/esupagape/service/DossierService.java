package org.esupportail.esupagape.service;

import org.esupportail.esupagape.dtos.DossierIndividuDto;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.TypeIndividu;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.repository.DossierRepository;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfosService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class DossierService {

    private static final Logger logger = LoggerFactory.getLogger(DossierService.class);

    private final UtilsService utilsService;

    private final List<DossierInfosService> dossierInfosServices;

    private final DossierRepository dossierRepository;

    public DossierService(UtilsService utilsService, List<DossierInfosService> dossierInfosServices, DossierRepository dossierRepository) {
        this.utilsService = utilsService;
        this.dossierInfosServices = dossierInfosServices;
        this.dossierRepository = dossierRepository;
    }

    public Dossier create(Individu individu, StatusDossier statusDossier) {
        Dossier dossier = new Dossier();
        dossier.setYear(utilsService.getCurrentYear());
        dossier.setIndividu(individu);
        dossier.setStatusDossier(statusDossier);
        if(individu.getNumEtu() != null && !individu.getNumEtu().isEmpty()) {
            dossier.setType(TypeIndividu.ETUDIANT);
        }
        dossierRepository.save(dossier);
        return dossier;
    }

    public void deleteDossier(Long id) {
        Optional<Dossier> dossier = dossierRepository.findById(id);
        dossier.ifPresent(d -> dossierRepository.delete(d));
    }

    public Page<Dossier> getAllByYear(int year, Pageable pageable) {
        return dossierRepository.findAllByYear(year, pageable);
    }

    public Page<Dossier> getAllCurrent(Pageable pageable) {
        return dossierRepository.findAllByYear(utilsService.getCurrentYear(), pageable);
    }

    public Dossier getById(Long id) {
        return dossierRepository.findById(id).orElseThrow();
    }

    public Dossier getByYear(Long individuId, int year) throws AgapeJpaException {
        try {
            return dossierRepository.findByIndividuIdAndYear(individuId, year).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new AgapeJpaException(e.getMessage());
        }
    }

    public Dossier getCurrent(Long individuId) throws AgapeJpaException {
        return getByYear(individuId, utilsService.getCurrentYear());
    }

    public List<Dossier> getAllByIndividu(Long individuId) {
        return dossierRepository.findAllByIndividuId(individuId);
    }

    public void saveAll(List<Dossier> dossiers) {
        dossierRepository.saveAll(dossiers);
    }

    public Page<DossierIndividuDto> getFullTextSearch(String fullTextSearch, TypeIndividu typeIndividu, StatusDossier statusDossier, Integer year,Pageable pageable) {
        return dossierRepository.findByFullTextSearch(fullTextSearch, typeIndividu,statusDossier,year, pageable);
    }

    @Transactional
    public void update(Long id, Dossier dossier) {
        Dossier dossierToUpdate = getById(id);
        dossierToUpdate.setClassification(dossier.getClassification());
        dossierToUpdate.setEtat(dossier.getEtat());
        dossierToUpdate.setMdph(dossier.getMdph());
        dossierToUpdate.setTaux(dossier.getTaux());
        dossierToUpdate.setTypeSuiviHandisup(dossier.getTypeSuiviHandisup());
        dossierToUpdate.setSuiviHandisup(dossier.getSuiviHandisup());
        dossierToUpdate.setEtat(dossier.getEtat());
        dossierToUpdate.setRentreeProchaine(dossier.getRentreeProchaine());
    }

    public Map<String, Object> getInfos(Dossier dossier) {
        Map<String, Object> infos = new HashMap<>();
        for(DossierInfosService dossierInfosService : dossierInfosServices) {
            List<Map<String, Object>> info = dossierInfosService.getDossierProperties(dossier.getIndividu(), utilsService.getCurrentYear(), false);
            if(info.size() > 0) {
                infos.putAll(info.get(0));
            }
        }
        return infos;
    }

    @Transactional
    public void syncDossier(Long id) {
        Dossier dossier = getById(id);
        for (DossierInfosService dossierInfosService : dossierInfosServices) {
            List<Map<String, Object>> dossierPropertiesList = dossierInfosService.getDossierProperties(dossier.getIndividu(), utilsService.getCurrentYear(), false);
            for(Map<String, Object> dossierProperties : dossierPropertiesList) {
                if (dossierProperties != null && dossierProperties.size() > 0) {
                    for (String key : dossierProperties.keySet()) {
                        if (dossierProperties.get(key) != null) {
                            switch (key) {
                                case "ufr" -> dossier.setComposante(dossierProperties.get(key).toString());
                                case "filiere" -> dossier.setFilliere(dossierProperties.get(key).toString());
                                case "etablissement" -> dossier.setSite(dossierProperties.get(key).toString());
                            }
                        }
                    }
                }
            }
        }
    }
}
