package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.AideHumaine;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.repository.AideHumaineRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AideHumaineService {

    private final AideHumaineRepository aideHumaineRepository;

    public AideHumaineService(AideHumaineRepository aideHumaineRepository) {
        this.aideHumaineRepository = aideHumaineRepository;
    }

    public void create(AideHumaine aideHumaine) {
        aideHumaineRepository.save(aideHumaine);
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
        aideHumaineToUpdate.setFonctionAidant(aideHumaine.getFonctionAidant());
        if(!aideHumaine.getNumEtuAidant().equals(aideHumaineToUpdate.getNumEtuAidant())) {
            //TODO recup des infos dans ldap
            aideHumaineToUpdate.setNumEtuAidant(aideHumaine.getNumEtuAidant());
        }
    }
}
