package org.esupportail.esupagape.service;

import org.esupportail.esupagape.dtos.DossierCompletCSVDto;
import org.esupportail.esupagape.repository.ExportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExportService {

    private final ExportRepository exportRepository;

    public ExportService(ExportRepository exportRepository) {
        this.exportRepository = exportRepository;
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
}

