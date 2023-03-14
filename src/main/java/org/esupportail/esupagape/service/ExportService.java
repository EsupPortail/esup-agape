package org.esupportail.esupagape.service;

import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.dtos.DossierCompletCSVDto;
import org.esupportail.esupagape.dtos.EnqueteExportCsv;
import org.esupportail.esupagape.entity.Enquete;
import org.esupportail.esupagape.repository.ExportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExportService {

    private static final Logger logger = LoggerFactory.getLogger(EnqueteService.class);

    private final ApplicationProperties applicationProperties;

    private final ExportRepository exportRepository;

    private final EnqueteService enqueteService;

    public ExportService(ApplicationProperties applicationProperties, ExportRepository exportRepository,
                         EnqueteService enqueteService) {
        this.applicationProperties = applicationProperties;
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
        List<EnqueteExportCsv> enqueteExportCsvs = new ArrayList<>();
        List<Enquete> enquetes = enqueteService.findAllByDossierYear(year);
        int id = 1;
        for(Enquete enquete : enquetes) {
            EnqueteExportCsv enqueteExportCsv = new EnqueteExportCsv(
                    "1",
                    applicationProperties.getCodeEtab(),
                    String.valueOf(id + Integer.parseInt(year + "0000")),
                    enquete.getAn(),
                    enquete.getSexe(),
                    enquete.getTypFrmn() != null ? enquete.getTypFrmn().name().toLowerCase() : "",
                    enquete.getModFrmn() != null ? enquete.getModFrmn().name().toLowerCase() : "",
                    enquete.getCodSco(),
                    enquete.getCodFmt(),
                    enquete.getCodFil(),
                    enquete.getCodHd() != null ? enquete.getCodHd().name().toLowerCase() : "",
                    (enquete.getHdTmp()) ? "1" : "0",
                    enquete.getCom(),
                    enquete.getCodPfpp() != null ? enquete.getCodPfpp().name().toLowerCase() : "",
                    enquete.getCodPfas() != null ? enquete.getCodPfas().name().toLowerCase() : "",
                    String.join("" ,enquete.getCodMeahF().stream().map(codMeahF -> codMeahF.name().toLowerCase()).sorted(String::compareTo).toList()),
                    enquete.getInterpH().toString(),
                    enquete.getCodeurH().toString(),
                    enquete.getAidHNat(),
                    String.join("" ,enquete.getCodMeae().stream().map(codMeahF -> codMeahF.name().toLowerCase()).toList()),
                    enquete.getAutAE(),
                    String.join("" ,enquete.getCodMeaa().stream().map(codMeahF -> codMeahF.name().toLowerCase()).toList()),
                    enquete.getAutAA(),
                    String.join("" ,enquete.getCodAmL().stream().map(codMeahF -> codMeahF.name().toLowerCase()).toList())
            );
            enqueteExportCsvs.add(enqueteExportCsv);
        }
        return enqueteExportCsvs;

    }

}