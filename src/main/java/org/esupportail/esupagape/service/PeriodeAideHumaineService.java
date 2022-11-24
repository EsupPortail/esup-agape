package org.esupportail.esupagape.service;

import org.esupportail.esupagape.dtos.AideHumainePeriodeSums;
import org.esupportail.esupagape.entity.AideHumaine;
import org.esupportail.esupagape.entity.PeriodeAideHumaine;
import org.esupportail.esupagape.repository.PeriodeAideHumaineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Month;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class PeriodeAideHumaineService {

    @PersistenceContext
    private EntityManager entityManager;

    private final PeriodeAideHumaineRepository periodeAideHumaineRepository;

    private final AideHumaineService aideHumaineService;

    public PeriodeAideHumaineService(PeriodeAideHumaineRepository periodeAideHumaineRepository, AideHumaineService aideHumaineService) {
        this.periodeAideHumaineRepository = periodeAideHumaineRepository;
        this.aideHumaineService = aideHumaineService;
    }

    public Map<Integer, PeriodeAideHumaine> getPeriodeAideHumaineServiceMapByAideHumaine(Long aideHumaineId) {
        LinkedHashMap<Integer, PeriodeAideHumaine> periodeAideHumaineServiceMap = new LinkedHashMap<>();
        List<PeriodeAideHumaine> periodeAideHumaines = periodeAideHumaineRepository.findByAideHumaineId(aideHumaineId);
        periodeAideHumaineServiceMap.put(9, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 9).findFirst().orElse(new PeriodeAideHumaine(Month.SEPTEMBER)));
        periodeAideHumaineServiceMap.put(10, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 10).findFirst().orElse(new PeriodeAideHumaine(Month.OCTOBER)));
        periodeAideHumaineServiceMap.put(11, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 11).findFirst().orElse(new PeriodeAideHumaine(Month.NOVEMBER)));
        periodeAideHumaineServiceMap.put(12, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 12).findFirst().orElse(new PeriodeAideHumaine(Month.DECEMBER)));
        periodeAideHumaineServiceMap.put(1, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 1).findFirst().orElse(new PeriodeAideHumaine(Month.JANUARY)));
        periodeAideHumaineServiceMap.put(2, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 2).findFirst().orElse(new PeriodeAideHumaine(Month.FEBRUARY)));
        periodeAideHumaineServiceMap.put(3, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 3).findFirst().orElse(new PeriodeAideHumaine(Month.MARCH)));
        periodeAideHumaineServiceMap.put(4, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 4).findFirst().orElse(new PeriodeAideHumaine(Month.APRIL)));
        periodeAideHumaineServiceMap.put(5, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 5).findFirst().orElse(new PeriodeAideHumaine(Month.MAY)));
        periodeAideHumaineServiceMap.put(6, periodeAideHumaines.stream().filter(p -> p.getMois().getValue() == 6).findFirst().orElse(new PeriodeAideHumaine(Month.JUNE)));
        return periodeAideHumaineServiceMap;
    }

    @Transactional
    public void save(Long aideHumaineId, Integer month, PeriodeAideHumaine periodeAideHumaine) {
        AideHumaine aideHumaine = aideHumaineService.getById(aideHumaineId);
        periodeAideHumaine.setAideHumaine(aideHumaine);
        periodeAideHumaine.setMois(Month.of(month));
        PeriodeAideHumaine periodeAideHumaineToUpdate;
        try {
            periodeAideHumaineToUpdate = aideHumaine.getPeriodeAideHumaines().stream().filter(p -> p.getMois().equals(Month.of(month))).findFirst().orElseThrow();
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

}
