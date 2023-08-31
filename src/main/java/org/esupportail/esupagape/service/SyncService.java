package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.repository.IndividuRepository;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuInfos;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuSourceService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SyncService {

    private static final Logger logger = LoggerFactory.getLogger(SyncService.class);

    private final IndividuRepository individuRepository;

    private final DossierService dossierService;

    private final List<IndividuSourceService> individuSourceServices;

    private final UtilsService utilsService;

    public SyncService(IndividuRepository individuRepository, DossierService dossierService, List<IndividuSourceService> individuSourceServices, UtilsService utilsService) {
        this.individuRepository = individuRepository;
        this.dossierService = dossierService;
        this.individuSourceServices = individuSourceServices;
        this.utilsService = utilsService;
    }

    @Transactional
    public void syncIndividu(Long id) throws AgapeJpaException {
        Individu individu = individuRepository.findById(id).orElseThrow();
        IndividuInfos individuInfos = getIndividuInfosByNumEtu(individu.getNumEtu());
        if (StringUtils.hasText(individuInfos.getEppn())) {
            individu.setEppn(individuInfos.getEppn());
        }
        if (StringUtils.hasText(individuInfos.getName())) {
            individu.setName(individuInfos.getName());
        }
        if (StringUtils.hasText(individuInfos.getFirstName())) {
            individu.setFirstName(individuInfos.getFirstName());
        }
        if (StringUtils.hasText(individuInfos.getGenre())) {
            individu.setSex(individuInfos.getGenre());
        }
        if (StringUtils.hasText(individuInfos.getGenre())) {
            individu.setGender(Gender.valueOf(individuInfos.getGenre()));
        }
        if (StringUtils.hasText(individuInfos.getNationalite())) {
            individu.setNationalite(individuInfos.getNationalite());
        }
        if (StringUtils.hasText(individuInfos.getEmailEtu())) {
            individu.setEmailEtu(individuInfos.getEmailEtu());
        }
        if (StringUtils.hasText(individuInfos.getFixAddress())) {
            individu.setFixAddress(individuInfos.getFixAddress());
        }
        if (StringUtils.hasText(individuInfos.getFixCP())) {
            individu.setFixCP(individuInfos.getFixCP());
        }
        if (StringUtils.hasText(individuInfos.getFixCity())) {
            individu.setFixCity(individuInfos.getFixCity());
        }
        if (StringUtils.hasText(individuInfos.getFixCountry())) {
            individu.setFixCountry(individuInfos.getFixCountry());
        }
        if (StringUtils.hasText(individuInfos.getFixPhone())) {
            individu.setFixPhone(individuInfos.getFixPhone());
        }

        if (StringUtils.hasText(individuInfos.getContactPhone())) {
            individu.setContactPhone(individuInfos.getContactPhone());
        }
        if (StringUtils.hasText(individuInfos.getPhotoId())) {
            individu.setPhotoId(individuInfos.getPhotoId());
        }
        if((individu.getDesinscrit() == null || !individu.getDesinscrit()) && (!StringUtils.hasText(individuInfos.getAffectation()) || Integer.parseInt(individuInfos.getYear()) < utilsService.getCurrentYear())) {
            individu.setDesinscrit(true);
        } else {
            individu.setDesinscrit(false);
        }
        try {
            Dossier dossier = dossierService.getCurrent(id);
            if (individuInfos.getHandicap() != null) {
                if (dossier.getStatusDossier().equals(StatusDossier.IMPORTE) && individuInfos.getHandicap() != null) {
                    dossier.getClassifications().add(individuInfos.getHandicap());
                }
            }
        } catch (AgapeJpaException e) {
            logger.debug(e.getMessage());
        }
    }


    public IndividuInfos getIndividuInfosByNumEtu(String numEtu) {
        IndividuInfos individuInfos = new IndividuInfos();
        for (IndividuSourceService individuSourceService : individuSourceServices) {
            individuInfos = individuSourceService.getIndividuProperties(numEtu, individuInfos, utilsService.getCurrentYear());
            if ((!StringUtils.hasText(individuInfos.getAffectation()) || Integer.parseInt(individuInfos.getYear()) < utilsService.getCurrentYear())) {
                break;
            }
        }
        return individuInfos;
    }

}
