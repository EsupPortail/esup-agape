package org.esupportail.esupagape.service;

import org.esupportail.esupagape.dtos.DossierCompletCSVDto;
import org.esupportail.esupagape.dtos.EnqueteForm;
import org.esupportail.esupagape.entity.Enquete;
import org.esupportail.esupagape.entity.enums.enquete.CodAmL;
import org.esupportail.esupagape.entity.enums.enquete.CodMeaa;
import org.esupportail.esupagape.entity.enums.enquete.CodMeae;
import org.esupportail.esupagape.repository.ExportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
    public List<EnqueteForm> findEnqueteByYearForCSV(Integer year) {
       // List<EnqueteForm> enqueteForms = exportRepository.findByEnqueteByYearForCSV(year);
        List<EnqueteForm> enqueteForms = new ArrayList<>();
        List<Enquete> enquetes = enqueteService.findAllByDossierYear(year);
        for (Enquete enquete : enquetes) {
            Set<CodMeae> codMeaes = enquete.getCodMeae();
            Set<CodMeaa> codMeaas = enquete.getCodMeaa();
            Set<CodAmL> codAmLs = enquete.getCodAmL();
            List<CodAmL> codAmLOrdered = new ArrayList<>(codAmLs).stream().sorted().toList();
            List<String> stringsCodAmL = codAmLOrdered.stream().map(codAmL -> codAmL.name().toLowerCase()).toList();
            //codAmLs = new LinkedHashSet<>(codAmLOrdered);
            Set<String> stringsOrdered = new LinkedHashSet<>(stringsCodAmL);

            logger.info(stringsOrdered.toString());


            EnqueteForm enqueteForm = new EnqueteForm();
            enqueteForm.setId(enquete.getId());
            enqueteForm.setNfic(enquete.getNfic());
            enqueteForm.setNumetu(enquete.getNumetu());
            enqueteForm.setAn(enquete.getAn());
            enqueteForm.setSexe(enquete.getSexe());
            enqueteForm.setTypFrmn(enquete.getTypFrmn());
            enqueteForm.setModFrmn(enquete.getModFrmn());
            enqueteForm.setCodSco(enquete.getCodSco());
            enqueteForm.setCodFmt(enquete.getCodFmt());
            enqueteForm.setCodFil(enquete.getCodFil());
            enqueteForm.setCodHd(enquete.getCodHd());
            enqueteForm.setHdTmp(enquete.getHdTmp());
            enqueteForm.setCom(enquete.getCom());
            enqueteForm.setCodPfpp(enquete.getCodPfpp());
            enqueteForm.setCodPfas(enquete.getCodPfas());
            enqueteForm.setAutAE(enquete.getAutAE());
            enqueteForm.setAutAA(enquete.getAutAA());
            enqueteForm.setCodMeae(codMeaes);
            enqueteForm.setCodMeaa(codMeaas);
            enqueteForm.setCodAmLs(stringsOrdered);
            enqueteForm.setDjaCop(enquete.getDjaCop());

            enqueteForms.add(enqueteForm);
        }
        return enqueteForms;
    }
}

