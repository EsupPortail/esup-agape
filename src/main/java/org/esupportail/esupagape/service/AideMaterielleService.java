package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.AideMaterielle;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.exception.AgapeYearException;
import org.esupportail.esupagape.repository.AideMaterielleRepository;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AideMaterielleService {


    private final AideMaterielleRepository aideMaterielleRepository;

    private final DossierService dossierService;

    private final UtilsService utilsService;

    public AideMaterielleService(AideMaterielleRepository aideMaterielleRepository, DossierService dossierService, UtilsService utilsService) {
        this.aideMaterielleRepository = aideMaterielleRepository;
        this.dossierService = dossierService;
        this.utilsService = utilsService;
    }

    public AideMaterielle getById(Long id) throws AgapeJpaException {
        Optional<AideMaterielle> optionalAideMaterielle = aideMaterielleRepository.findById(id);
        if (optionalAideMaterielle.isPresent()) {
            return optionalAideMaterielle.get();
        } else {
            throw new AgapeJpaException("Je n'ai pas trouvé cette aide");
        }
    }

    @Transactional
    public void create(AideMaterielle aideMaterielle, Long dossierId, String eppn) {
        Dossier dossier = dossierService.getById(dossierId);
        if(dossier.getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        aideMaterielle.setDossier(dossier);
        if (dossier.getStatusDossier().equals(StatusDossier.IMPORTE)
            || dossier.getStatusDossier().equals(StatusDossier.AJOUT_MANUEL)
            || dossier.getStatusDossier().equals(StatusDossier.ACCUEILLI)
            || dossier.getStatusDossier().equals(StatusDossier.RECONDUIT)) {
            dossierService.changeStatutDossier(dossierId, StatusDossier.SUIVI, eppn);
        }
        aideMaterielleRepository.save(aideMaterielle);
    }

    public Page<AideMaterielle> findByDossier(Long dossierId) {
        return aideMaterielleRepository.findByDossierId(dossierId, Pageable.unpaged());
    }

    @Transactional
    public void delete(Long aideMaterielleId) {
        AideMaterielle aideMaterielle = getById(aideMaterielleId);
        if(aideMaterielle.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        aideMaterielleRepository.deleteById(aideMaterielleId);
    }

    @Transactional
    public void save(Long id, AideMaterielle aideMaterielle) throws AgapeJpaException {
        AideMaterielle aideMaterielleToUpdate = getById(id);
        if(aideMaterielleToUpdate.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        aideMaterielleToUpdate.setTypeAideMaterielle(aideMaterielle.getTypeAideMaterielle());
        aideMaterielleToUpdate.setStartDate(aideMaterielle.getStartDate());
        aideMaterielleToUpdate.setEndDate(aideMaterielle.getEndDate());
        aideMaterielleToUpdate.setCost(aideMaterielle.getCost());
        aideMaterielleToUpdate.setComment(aideMaterielle.getComment());
        aideMaterielleRepository.save(aideMaterielleToUpdate);
    }

    public double additionCostAideMaterielle(Page<AideMaterielle> aideMaterielles) {
        double sum = 0;
        for (AideMaterielle aideMaterielle : aideMaterielles) {
            sum = sum + aideMaterielle.getCost();
        }
        return sum;
    }
}
