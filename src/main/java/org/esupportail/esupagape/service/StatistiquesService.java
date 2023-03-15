package org.esupportail.esupagape.service;

import org.esupportail.esupagape.dtos.ClassificationChart;
import org.esupportail.esupagape.dtos.charjs.*;
import org.esupportail.esupagape.repository.StatistiquesRepository;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class StatistiquesService {

    private final StatistiquesRepository statistiquesRepository;
    private final MessageSource messageSource;
    private final DossierService dossierService;
    private final UtilsService utilsService;

    public StatistiquesService(StatistiquesRepository statistiquesRepository, MessageSource messageSource, DossierService dossierService, UtilsService utilsService) {
        this.statistiquesRepository = statistiquesRepository;
        this.messageSource = messageSource;
        this.dossierService = dossierService;
        this.utilsService = utilsService;
    }

    public Chart getClassificationChart(Integer year) {
        List<ClassificationChart> classificationCharts =  statistiquesRepository.countFindClassificationByYear(year);
        Dataset dataset = new Dataset(
                "Nombre d'individus",
                classificationCharts.stream().map(ClassificationChart::getClassificationCount).collect(Collectors.toList()),
                null,
                4
                );
        List<String> labels = new ArrayList<>();
        for(String classification : classificationCharts.stream().map(ClassificationChart::getClassification).toList()) {
            labels.add(messageSource.getMessage("dossier.classification." + classification, null, Locale.getDefault()));
        }
        return new Doughnut(new Data(labels, Collections.singletonList(dataset)));
    }

}

