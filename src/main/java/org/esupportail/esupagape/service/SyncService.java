package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.repository.DossierRepository;
import org.esupportail.esupagape.repository.IndividuRepository;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfos;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfosService;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuInfos;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuSourceService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class SyncService {

    private static final Logger logger = LoggerFactory.getLogger(SyncService.class);

    private final IndividuRepository individuRepository;

    private final DossierRepository dossierRepository;

    private final List<IndividuSourceService> individuSourceServices;

    private final List<DossierInfosService> dossierInfosServices;

    private final UtilsService utilsService;

    public SyncService(IndividuRepository individuRepository, DossierRepository dossierRepository, List<IndividuSourceService> individuSourceServices, List<DossierInfosService> dossierInfosServices, UtilsService utilsService) {
        this.individuRepository = individuRepository;
        this.dossierRepository = dossierRepository;
        this.individuSourceServices = individuSourceServices;
        Collections.reverse(dossierInfosServices);
        this.dossierInfosServices = dossierInfosServices;
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
        if (individuInfos.getDateOfBirth() != null) {
            individu.setDateOfBirth(individuInfos.getDateOfBirth());
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
        if (!StringUtils.hasText(individuInfos.getAffectation()) || Integer.parseInt(individuInfos.getYear()) < utilsService.getCurrentYear()) {
            individu.setDesinscrit(true);
        } else {
            individu.setDesinscrit(false);
        }
        try {
            Dossier dossier = dossierRepository.findByIndividuIdAndYear(id, utilsService.getCurrentYear()).orElse(null);
            if (dossier != null && individuInfos.getHandicap() != null) {
                if (dossier.getStatusDossier().equals(StatusDossier.IMPORTE)) {
                    dossier.getClassifications().add(individuInfos.getHandicap());
                }
            }
        } catch (AgapeJpaException e) {
            logger.debug(e.getMessage());
        }
    }

    @Transactional
    public void syncStatusDossierAmenagement(Long dossierId) {
        Dossier dossier = dossierRepository.findById(dossierId).orElseThrow();
        if(dossier.getDossierAmenagements() == null) return;
        if(dossier.getDossierAmenagements().stream().noneMatch(da -> da.getStatusDossierAmenagement().equals(StatusDossierAmenagement.EN_ATTENTE) && da.getAmenagement().getStatusAmenagement().equals(StatusAmenagement.SUPPRIME))
                && dossier.getDossierAmenagements().stream().noneMatch(da -> da.getStatusDossierAmenagement().equals(StatusDossierAmenagement.VALIDE))
                && dossier.getDossierAmenagements().stream().noneMatch(da -> da.getStatusDossierAmenagement().equals(StatusDossierAmenagement.PORTE))
        ) {
            if(dossier.getDossierAmenagements().stream().anyMatch(da -> da.getStatusDossierAmenagement().equals(StatusDossierAmenagement.EXPIRE))) {
                dossier.setStatusDossierAmenagement(StatusDossierAmenagement.EXPIRE);
            } else {
                dossier.setStatusDossierAmenagement(StatusDossierAmenagement.NON);
            }
        }
        if(dossier.getDossierAmenagements().stream().anyMatch(da -> da.getStatusDossierAmenagement().equals(StatusDossierAmenagement.PORTE))) {
            dossier.setStatusDossierAmenagement(StatusDossierAmenagement.PORTE);
        }
        if(dossier.getDossierAmenagements().stream().anyMatch(da -> da.getStatusDossierAmenagement().equals(StatusDossierAmenagement.VALIDE))) {
            dossier.setStatusDossierAmenagement(StatusDossierAmenagement.VALIDE);
        }
        if(dossier.getDossierAmenagements().stream().anyMatch(da -> da.getStatusDossierAmenagement().equals(StatusDossierAmenagement.EN_ATTENTE) && !da.getAmenagement().getStatusAmenagement().equals(StatusAmenagement.SUPPRIME))) {
            dossier.setStatusDossierAmenagement(StatusDossierAmenagement.EN_ATTENTE);
        }
        if(dossier.getStatusDossier().equals(StatusDossier.NON_RECONDUIT) || dossier.getStatusDossier().equals(StatusDossier.IMPORTE) || dossier.getStatusDossier().equals(StatusDossier.AJOUT_MANUEL)) {
            if(dossier.getStatusDossierAmenagement().equals(StatusDossierAmenagement.PORTE)) {
                dossier.setStatusDossier(StatusDossier.RECONDUIT);
            } else if(dossier.getStatusDossierAmenagement().equals(StatusDossierAmenagement.VALIDE)) {
                dossier.setStatusDossier(StatusDossier.AJOUT_MANUEL);
            }
        }
    }

    @Transactional
    public void syncDossier(Long id) {
        Dossier dossier = dossierRepository.findById(id).orElseThrow();
        if (dossier.getYear() < utilsService.getCurrentYear() && dossier.getIndividu().getDesinscrit() != null && dossier.getIndividu().getDesinscrit()) {
            return;
        }
        if (dossier.getIndividu().getDossiers().size() > 1) {
            dossier.setNewDossier(false);
        } else {
            dossier.setNewDossier(true);
        }
        if (dossier.getStatusDossier().equals(StatusDossier.ANONYMOUS)) return;
        if(StatusDossierAmenagement.PORTE.equals(dossier.getStatusDossierAmenagement()) && dossier.getClassifications().isEmpty()) {
            try {
                List<Classification> classifications = new ArrayList<>(dossier.getIndividu().getDossiers().stream().sorted(Comparator.comparingInt(Dossier::getYear).reversed()).filter(d -> !d.getClassifications().isEmpty()).findFirst().orElseThrow().getClassifications());
                dossier.getClassifications().addAll(classifications);
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
        }
        for (DossierInfosService dossierInfosService : dossierInfosServices) {
            DossierInfos dossierInfos = dossierInfosService.getDossierProperties(dossier.getIndividu(), dossier.getYear(), false, false, new DossierInfos());
            if (dossierInfos != null) {
                if (StringUtils.hasText(dossierInfos.getCodComposante())) {
                    dossier.setCodComposante(dossierInfos.getCodComposante());
                }
                if (StringUtils.hasText(dossierInfos.getCampus())) {
                    dossier.setCampus(dossierInfos.getCampus());
                }
                if (StringUtils.hasText(dossierInfos.getComposante())) {
                    dossier.setComposante(dossierInfos.getComposante().trim());
                }
                if (StringUtils.hasText(dossierInfos.getLibelleFormation())) {
                    dossier.setLibelleFormation(dossierInfos.getLibelleFormation());
                }
                if (StringUtils.hasText(dossierInfos.getLibelleFormationPrec())) {
                    dossier.setLibelleFormationPrec(dossierInfos.getLibelleFormationPrec());
                } else {
                    if(dossier.getLibelleFormationPrec() == null) {
                        dossier.setLibelleFormationPrec("");
                    }
                }
                if (StringUtils.hasText(dossierInfos.getFormAddress())) {
                    dossier.setFormAddress(dossierInfos.getFormAddress());
                }
                if (StringUtils.hasText(dossierInfos.getNiveauEtudes())) {
                    dossier.setNiveauEtudes(dossierInfos.getNiveauEtudes());
                }
                if (StringUtils.hasText(dossierInfos.getSecteurDisciplinaire())) {
                    dossier.setSecteurDisciplinaire(dossierInfos.getSecteurDisciplinaire());
                }
                if (StringUtils.hasText(dossierInfos.getResultatAnn())) {
                    dossier.setResultatTotal(dossierInfos.getResultatAnn());
                }
                dossier.setHasScholarship(dossierInfos.getHasScholarship());
                if(dossier.getClassifications().isEmpty()) {
                    List<Dossier> lastDossiers = dossierRepository.findAllByIndividuId(dossier.getIndividu().getId()).stream().sorted(Comparator.comparingInt(Dossier::getYear).reversed()).toList();
                    for(Dossier lastDossier : lastDossiers) {
                        if(!lastDossier.getClassifications().isEmpty()) {
                            dossier.getClassifications().addAll(lastDossier.getClassifications());
                            break;
                        }
                    }
                }
            }
        }
        syncStatusDossierAmenagement(dossier.getId());
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
