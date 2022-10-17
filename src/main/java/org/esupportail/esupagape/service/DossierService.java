package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.TypeIndividu;
import org.esupportail.esupagape.dtos.DossierIndividuDto;
import org.esupportail.esupagape.repository.DossierRepository;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class DossierService {

    @Resource
    private UtilsService utilsService;

    @Resource
    private DossierRepository dossierRepository;

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

    public Dossier getId(Long id) {
        return dossierRepository.findById(id).orElseThrow();
    }

    public Dossier getByYear(Long individuId, int year) {
        return dossierRepository.findByIndividuIdAndYear(individuId, year).orElseThrow();
    }

    public Dossier getCurrent(Long individuId) {
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

    public List<Integer> getYearDistinct(){
        return dossierRepository.findYearDistinct();
    }

}
