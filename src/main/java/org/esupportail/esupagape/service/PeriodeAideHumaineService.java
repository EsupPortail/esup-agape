package org.esupportail.esupagape.service;

import org.esupportail.esupagape.dtos.AideHumainePeriodeSums;
import org.esupportail.esupagape.entity.AideHumaine;
import org.esupportail.esupagape.entity.Document;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.PeriodeAideHumaine;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.esupportail.esupagape.repository.PeriodeAideHumaineRepository;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Month;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class PeriodeAideHumaineService {

    private static final Logger logger = LoggerFactory.getLogger(PeriodeAideHumaineService.class);

    private final PeriodeAideHumaineRepository periodeAideHumaineRepository;

    private final AideHumaineService aideHumaineService;

    private final DocumentService documentService;

    private final UtilsService utilsService;

    public PeriodeAideHumaineService(PeriodeAideHumaineRepository periodeAideHumaineRepository, AideHumaineService aideHumaineService, DocumentService documentService, UtilsService utilsService) {
        this.periodeAideHumaineRepository = periodeAideHumaineRepository;
        this.aideHumaineService = aideHumaineService;
        this.documentService = documentService;
        this.utilsService = utilsService;
    }

    public Map<Integer, PeriodeAideHumaine> getPeriodeAideHumaineMapByAideHumaine(Long aideHumaineId) {
        LinkedHashMap<Integer, PeriodeAideHumaine> periodeAideHumaineMap = new LinkedHashMap<>();
        List<PeriodeAideHumaine> periodeAideHumaines = periodeAideHumaineRepository.findByAideHumaineId(aideHumaineId);
        periodeAideHumaineMap.put(9, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 9).findFirst().orElse(new PeriodeAideHumaine()));
        periodeAideHumaineMap.put(10, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 10).findFirst().orElse(new PeriodeAideHumaine()));
        periodeAideHumaineMap.put(11, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 11).findFirst().orElse(new PeriodeAideHumaine()));
        periodeAideHumaineMap.put(12, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 12).findFirst().orElse(new PeriodeAideHumaine()));
        periodeAideHumaineMap.put(1, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 1).findFirst().orElse(new PeriodeAideHumaine()));
        periodeAideHumaineMap.put(2, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 2).findFirst().orElse(new PeriodeAideHumaine()));
        periodeAideHumaineMap.put(3, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 3).findFirst().orElse(new PeriodeAideHumaine()));
        periodeAideHumaineMap.put(4, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 4).findFirst().orElse(new PeriodeAideHumaine()));
        periodeAideHumaineMap.put(5, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 5).findFirst().orElse(new PeriodeAideHumaine()));
        periodeAideHumaineMap.put(6, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 6).findFirst().orElse(new PeriodeAideHumaine()));
        return periodeAideHumaineMap;
    }

    @Transactional
    public void save(Long aideHumaineId, Integer month, PeriodeAideHumaine periodeAideHumaine) {
        AideHumaine aideHumaine = aideHumaineService.getById(aideHumaineId);
        periodeAideHumaine.setAideHumaine(aideHumaine);
        periodeAideHumaine.setMois(Month.of(month));
        PeriodeAideHumaine periodeAideHumaineToUpdate;
        try {
            periodeAideHumaineToUpdate = getPeriodeAideHumaineByMonth(aideHumaineId, month);
            periodeAideHumaineToUpdate.setRegistrationDate(periodeAideHumaine.getRegistrationDate());
            periodeAideHumaineToUpdate.setCost(periodeAideHumaine.getCost());
            periodeAideHumaineToUpdate.setMoisPaye(periodeAideHumaine.getMoisPaye());
            periodeAideHumaineToUpdate.setNbHeures(periodeAideHumaine.getNbHeures());
        } catch (NoSuchElementException e) {
            periodeAideHumaineRepository.save(periodeAideHumaine);
        }
    }

    @Transactional
    public AideHumainePeriodeSums getAideHumainePeriodeSums(Long aideHumaineId) {
        AideHumaine aideHumaine = aideHumaineService.getById(aideHumaineId);
        AideHumainePeriodeSums aideHumainePeriodeSums = new AideHumainePeriodeSums();
        Integer coutSemestre1 = aideHumaine.getPeriodeAideHumaines().stream().filter(p -> p.getSemestre().equals(1)).map(PeriodeAideHumaine::getCost).reduce(0, Integer::sum);
        Integer coutSemestre2 = aideHumaine.getPeriodeAideHumaines().stream().filter(p -> p.getSemestre().equals(2)).map(PeriodeAideHumaine::getCost).reduce(0, Integer::sum);
        Integer coutTotal = coutSemestre1 + coutSemestre2;
        Double nbHeureSemestre1 = aideHumaine.getPeriodeAideHumaines().stream().filter(p -> p.getSemestre().equals(1)).map(PeriodeAideHumaine::getNbHeures).reduce(.0, Double::sum);
        Double nbHeureSemestre2 = aideHumaine.getPeriodeAideHumaines().stream().filter(p -> p.getSemestre().equals(2)).map(PeriodeAideHumaine::getNbHeures).reduce(.0, Double::sum);
        Double nbHeureTotales = nbHeureSemestre1 + nbHeureSemestre2;
        aideHumainePeriodeSums.setCoutSemestre1((double) coutSemestre1 / 100 + "");
        aideHumainePeriodeSums.setCoutSemestre2((double) coutSemestre2 / 100 + "");
        aideHumainePeriodeSums.setCoutTotal((double) coutTotal / 100 + "");
        aideHumainePeriodeSums.setNbHeuresSemestre1(nbHeureSemestre1.toString());
        aideHumainePeriodeSums.setNbHeuresSemestre2(nbHeureSemestre2.toString());
        aideHumainePeriodeSums.setNbHeuresTotales(nbHeureTotales.toString());
        return aideHumainePeriodeSums;
    }

    @Transactional
    public void addFeuilleHeures(Long aideHumaineId, Integer month, MultipartFile[] multipartFiles, Dossier dossier) throws AgapeIOException {
        try {
            for(MultipartFile multipartFile : multipartFiles) {
                PeriodeAideHumaine periodeAideHumaineToUpdate = getPeriodeAideHumaineByMonth(aideHumaineId, month);
                Document feuille = documentService.createDocument(multipartFile.getInputStream(), multipartFile.getOriginalFilename(), multipartFile.getContentType(), periodeAideHumaineToUpdate.getId(), PeriodeAideHumaine.class.getTypeName(), dossier);
                periodeAideHumaineToUpdate.setFeuilleHeures(feuille);
            }
        } catch (IOException e) {
            throw new AgapeIOException(e.getMessage());
        }
    }

    @Transactional
    public void getFeuilleHeuresHttpResponse(Long aideHumaineId, Integer month, HttpServletResponse httpServletResponse) throws AgapeIOException {
        try {
            Document document = getPeriodeAideHumaineByMonth(aideHumaineId, month).getFeuilleHeures();
            utilsService.copyFileStreamToHttpResponse(document.getFileName(), document.getContentType(), document.getInputStream(), httpServletResponse);
        } catch (IOException e) {
            throw new AgapeIOException(e.getMessage());
        }
    }

    @Transactional
    public void deleteFeuilleHeures(Long aideHumaineId, Integer month) {
        PeriodeAideHumaine periodeAideHumaine = getPeriodeAideHumaineByMonth(aideHumaineId, month);
        Document document = periodeAideHumaine.getFeuilleHeures();
        periodeAideHumaine.setFeuilleHeures(null);
        documentService.delete(document);
    }

    @Transactional
    public void addPlanning(Long aideHumaineId, Integer month, MultipartFile[] multipartFiles, Dossier dossier) throws AgapeIOException {
        AideHumaine aideHumaine = aideHumaineService.getById(aideHumaineId);
        try {
            for(MultipartFile multipartFile : multipartFiles) {
                PeriodeAideHumaine periodeAideHumaineToUpdate = aideHumaine.getPeriodeAideHumaines().stream().filter(p -> p.getMois().equals(Month.of(month))).findFirst().orElseThrow();
                Document planning = documentService.createDocument(multipartFile.getInputStream(), multipartFile.getOriginalFilename(), multipartFile.getContentType(), periodeAideHumaineToUpdate.getId(), PeriodeAideHumaine.class.getTypeName(), dossier);
                periodeAideHumaineToUpdate.setPlanning(planning);
            }
        } catch (IOException e) {
            throw new AgapeIOException(e.getMessage());
        }
    }

    @Transactional
    public void getPlanningHttpResponse(Long aideHumaineId, Integer month, HttpServletResponse httpServletResponse) throws AgapeIOException {
        try {
            Document document = getPeriodeAideHumaineByMonth(aideHumaineId, month).getPlanning();
            utilsService.copyFileStreamToHttpResponse(document.getFileName(), document.getContentType(), document.getInputStream(), httpServletResponse);
        } catch (IOException e) {
            throw new AgapeIOException(e.getMessage());
        }
    }

    @Transactional
    public void deletePlanning(Long aideHumaineId, Integer month) {
        PeriodeAideHumaine periodeAideHumaine = getPeriodeAideHumaineByMonth(aideHumaineId, month);
        Document document = periodeAideHumaine.getPlanning();
        periodeAideHumaine.setPlanning(null);
        documentService.delete(document);
    }

    @Transactional
    public void delete(Long aideHumaineId, Integer month) {
        AideHumaine aideHumaine = aideHumaineService.getById(aideHumaineId);
        PeriodeAideHumaine periodeAideHumaine = getPeriodeAideHumaineByMonth(aideHumaineId, month);
        aideHumaine.getPeriodeAideHumaines().remove(periodeAideHumaine);
        periodeAideHumaineRepository.delete(periodeAideHumaine);
    }

    private PeriodeAideHumaine getPeriodeAideHumaineByMonth(Long aideHumaineId, Integer month) {
        AideHumaine aideHumaine = aideHumaineService.getById(aideHumaineId);
        return aideHumaine.getPeriodeAideHumaines().stream().filter(p -> p.getMois().equals(Month.of(month))).findFirst().orElseThrow();
    }
}
