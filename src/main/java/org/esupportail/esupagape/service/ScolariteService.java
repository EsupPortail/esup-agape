package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.enums.StatusAmenagement;
import org.esupportail.esupagape.repository.ScolariteRepository;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScolariteService {

    private static final Logger logger = LoggerFactory.getLogger(ScolariteService.class);

    private  ScolariteRepository scolariteRepository;

    private final UtilsService utilsService;

    public ScolariteService(ScolariteRepository scolariteRepository, UtilsService utilsService) {
        this.scolariteRepository = scolariteRepository;
        this.utilsService = utilsService;
    }

    public Page<Amenagement> getFullTextSearchScol(StatusAmenagement statusAmenagement, List<String> codComposantes, Integer yearFilter, Pageable pageable) {
        return scolariteRepository.findByFullTextSearchScol(statusAmenagement, codComposantes, yearFilter, pageable);
    }

    public Page<Amenagement> getByIndividuNameScol(String fullTextSearch, StatusAmenagement statusAmenagement, List<String> codComposantes, Pageable pageable) {
        return scolariteRepository.findByIndividuNameScol(fullTextSearch, statusAmenagement, utilsService.getCurrentYear(), codComposantes, pageable);
    }

}
