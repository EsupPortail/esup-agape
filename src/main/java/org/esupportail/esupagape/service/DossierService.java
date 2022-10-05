package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.TypeIndividu;
import org.esupportail.esupagape.repository.DossierRepository;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DossierService {

    @Resource
    private UtilsService utilsService;

    @Resource
    private DossierRepository dossierRepository;

    public Dossier create(Individu individu) {
        Dossier dossier = new Dossier();
        dossier.setYear(utilsService.getCurrentYear());
        dossier.setIndividu(individu);
        dossier.setStatusDossier(StatusDossier.IMPORTE);
        if(!individu.getNumEtu().isEmpty()) {
            dossier.setType(TypeIndividu.ETUDIANT);
        }
//        dossier.setComposante("");
//        dossier.setFilliere("");
//        dossierRepository.save(dossier);
        return dossier;
    }

    public Page<Dossier> getAllByYear(int year, Pageable pageable) {
        return dossierRepository.findAllByYear(year, pageable);
    }

    public Page<Dossier> getAllCurrent(Pageable pageable) {
        return dossierRepository.findAllByYear(utilsService.getCurrentYear(), pageable);
    }

    public Dossier getId(Long id) {
        return dossierRepository.findById(id).orElseThrow();
    }

    public Dossier getByYear(Long individuId, int year) {
        return dossierRepository.findByIndividuIdAndYear(individuId, year).orElseThrow();
    }

    public Dossier getCurrent(Long individuId) {
        return dossierRepository.findByIndividuIdAndYear(individuId, utilsService.getCurrentYear()).orElseThrow();
    }

    public List<Dossier> getAllByIndividu(Long individuId) {
        return dossierRepository.findAllByIndividuId(individuId);
    }

    public void saveAll(List<Dossier> dossiers) {
        dossierRepository.saveAll(dossiers);
    }

    public Page<Dossier> getFullTextSearch(String fullTextSearch, TypeIndividu typeIndividu, StatusDossier statusDossier, Pageable pageable) {
        return dossierRepository.findByFullTextSearch(fullTextSearch, typeIndividu,statusDossier,pageable);
    }
}
