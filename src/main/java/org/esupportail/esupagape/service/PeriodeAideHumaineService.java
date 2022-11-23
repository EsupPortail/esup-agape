package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.PeriodeAideHumaine;
import org.esupportail.esupagape.repository.PeriodeAideHumaineRepository;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PeriodeAideHumaineService {

    private final PeriodeAideHumaineRepository periodeAideHumaineRepository;

    public PeriodeAideHumaineService(PeriodeAideHumaineRepository periodeAideHumaineRepository) {
        this.periodeAideHumaineRepository = periodeAideHumaineRepository;
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
}
