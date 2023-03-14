package org.esupportail.esupagape.service;

import org.esupportail.esupagape.dtos.DossierCompletCSVDto;
import org.esupportail.esupagape.dtos.EnqueteExportCsv;
import org.esupportail.esupagape.repository.ExportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExportService {

    private static final Logger logger = LoggerFactory.getLogger(EnqueteService.class);

    private final ExportRepository exportRepository;

    private final EnqueteService enqueteService;

    public ExportService(ExportRepository exportRepository,
                         EnqueteService enqueteService) {
        this.exportRepository = exportRepository;
        this.enqueteService = enqueteService;
    }

    @Transactional
    public List<DossierCompletCSVDto> getCsvDossier(Integer year) {
        List<DossierCompletCSVDto> dossierCompletCSVDtos = exportRepository.findByYearForCSV(year);
        return dossierCompletCSVDtos;
    }

    @Transactional
    public List<DossierCompletCSVDto> findEmailEtuByYearForCSV(Integer year) {
        List<DossierCompletCSVDto> dossierCompletCSVDtos = exportRepository.findEmailEtuByYearForCSV(year);
        return dossierCompletCSVDtos;
    }

    @Transactional
    public List<EnqueteExportCsv> findEnqueteByYearForCSV(Integer year) {
        List<EnqueteExportCsv> enqueteExportCsvs = exportRepository.findEnqueteByYearForCSV(year);


        return enqueteExportCsvs;

    }

}