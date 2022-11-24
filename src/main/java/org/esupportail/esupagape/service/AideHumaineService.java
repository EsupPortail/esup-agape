package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.AideHumaine;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.repository.AideHumaineRepository;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuInfos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AideHumaineService {

    private final AideHumaineRepository aideHumaineRepository;

    private final IndividuService individuService;

    public AideHumaineService(AideHumaineRepository aideHumaineRepository, IndividuService individuService) {
        this.aideHumaineRepository = aideHumaineRepository;
        this.individuService = individuService;
    }

    public AideHumaine create(AideHumaine aideHumaine) {
        recupAidantWithNumEtu(aideHumaine.getNumEtuAidant(), aideHumaine);
        return aideHumaineRepository.save(aideHumaine);
    }

    public Page<AideHumaine> findByDossier(Dossier dossier) {
        return aideHumaineRepository.findByDossierId(dossier.getId(), Pageable.unpaged());
    }

    public AideHumaine getById(Long aideHumaineId) {
        return aideHumaineRepository.findById(aideHumaineId).orElseThrow();
    }
    @Transactional
    public void delete(Long aideHumaineId) {
        aideHumaineRepository.deleteById(aideHumaineId);
    }

    @Transactional
    public void save(Long aideHumaineId, AideHumaine aideHumaine) {
        AideHumaine aideHumaineToUpdate = getById(aideHumaineId);
        aideHumaineToUpdate.setStatusAideHumaine(aideHumaine.getStatusAideHumaine());
        aideHumaineToUpdate.setFonctionAidants(aideHumaine.getFonctionAidants());
        if(!aideHumaine.getNumEtuAidant().equals(aideHumaineToUpdate.getNumEtuAidant())) {
            recupAidantWithNumEtu(aideHumaine.getNumEtuAidant(), aideHumaineToUpdate);
        }
    }

    private void recupAidantWithNumEtu(String numEtu, AideHumaine aideHumaineToUpdate) {
        IndividuInfos individuInfos = individuService.getIndividuInfosByNumEtu(numEtu);
        if(StringUtils.hasText(individuInfos.getName())) {
            aideHumaineToUpdate.setNameAidant(individuInfos.getName());
        }
        if(StringUtils.hasText(individuInfos.getFirstName())) {
            aideHumaineToUpdate.setFirstNameAidant(individuInfos.getFirstName());
        }
        if(StringUtils.hasText(individuInfos.getEmailEtu())) {
            aideHumaineToUpdate.setEmailAidant(individuInfos.getEmailEtu());
        }
        if(StringUtils.hasText(individuInfos.getEmailPerso())) {
            aideHumaineToUpdate.setEmailAidant(individuInfos.getEmailPerso());
        }
        if(individuInfos.getDateOfBirth() != null) {
            aideHumaineToUpdate.setDateOfBirthAidant(individuInfos.getDateOfBirth());
        }
        if(StringUtils.hasText(individuInfos.getFixPhone())) {
            aideHumaineToUpdate.setPhoneAidant(individuInfos.getFixPhone());
        }
        if(StringUtils.hasText(individuInfos.getContactPhone())) {
            aideHumaineToUpdate.setPhoneAidant(individuInfos.getContactPhone());
        }
        aideHumaineToUpdate.setNumEtuAidant(numEtu);
    }
}
