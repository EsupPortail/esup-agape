package org.esupportail.esupagape.service;

import org.esupportail.esupagape.dtos.charts.ClassificationChart;
import org.esupportail.esupagape.dtos.charts.ComposanteChart;
import org.esupportail.esupagape.repository.StatistiquesRepository;
import org.esupportail.esupagape.service.utils.chartjs.Chart;
import org.esupportail.esupagape.service.utils.chartjs.Data;
import org.esupportail.esupagape.service.utils.chartjs.Dataset;
import org.esupportail.esupagape.service.utils.chartjs.Doughnut;
import org.esupportail.esupagape.service.utils.chartjs.Line;
import org.esupportail.esupagape.service.utils.chartjs.Options;
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

    public StatistiquesService(StatistiquesRepository statistiquesRepository, MessageSource messageSource) {
        this.statistiquesRepository = statistiquesRepository;
        this.messageSource = messageSource;
    }

    public Chart getClassificationChart(Integer year) {
        List<ClassificationChart> classificationCharts = statistiquesRepository.countFindClassificationByYear(year);
        Dataset dataset = new Dataset("Nombre d'individus", classificationCharts.stream().map(ClassificationChart::getClassificationCount).collect(Collectors.toList()), null, 4, null, null);
        List<String> labels = new ArrayList<>();
        for (String classification : classificationCharts.stream().map(ClassificationChart::getClassification).toList()) {
            labels.add(messageSource.getMessage("dossier.classification." + classification, null, Locale.getDefault()));
        }

        return new Doughnut(new Data(labels, Collections.singletonList(dataset)));
    }

    public Chart getComposanteChart(Integer year) {
        List<ComposanteChart> composanteCharts = statistiquesRepository.countFindComposanteByYear(year);
        Dataset dataset = new Dataset("Nombre d'individus", composanteCharts.stream().map(ComposanteChart::getComposanteCount).collect(Collectors.toList()), null, 4, null, null);
        List<String> labels = new ArrayList<>();
        labels.addAll(composanteCharts.stream().map(ComposanteChart::getComposante).toList());
        return new Doughnut(new Data(labels, Collections.singletonList(dataset)));
    }

    /*public Chart getIndividuChart() {

        List<Integer> years = statistiquesRepository.findDistinctYears();
        List<String> counts = new ArrayList<>();
        List<Integer> yAxesTicks = null;

        for (Integer year : years) {
            counts.add(String.valueOf(statistiquesRepository.countFindIndividuByYear(year)));
        }

        Dataset dataset = new Dataset("Nombre d'individus", counts, null, 4, 1, null);
        List<String> labels = years.stream().map(String::valueOf).collect(Collectors.toList());

        Options options = new Options();
        options.beginAtZero = true;
        options.yAxesTicks = yAxesTicks;

        Data data = new Data(labels, Collections.singletonList(dataset));
        return new Bar(data, options);


        // return new Bar(new Data(labels, Collections.singletonList(dataset)), options);

    }*/

        public Chart getIndividuLineChart() {
        List<Integer> years = statistiquesRepository.findDistinctYears().stream()
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());

        List<String> counts = new ArrayList<>();
        List<Integer> yAxesTicks = null;

        for (Integer year : years) {
            counts.add(String.valueOf(statistiquesRepository.countFindIndividuByYear(year)));
        }

        Dataset dataset = new Dataset("Nombre d'individus par année", counts, null, null, 1, 0.1);
        List<String> labels = years.stream().map(String::valueOf).collect(Collectors.toList());

        Options options = new Options();
        options.beginAtZero = true;
        options.yAxesTicks = yAxesTicks;

        Data data = new Data(labels, Collections.singletonList(dataset));
        return new Line(data, options);

    }

}
