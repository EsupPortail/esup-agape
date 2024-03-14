package org.esupportail.esupagape.service;

import org.esupportail.esupagape.dtos.charts.ClassificationChart;
import org.esupportail.esupagape.dtos.charts.ComposanteChart;
import org.esupportail.esupagape.entity.enums.Classification;
import org.esupportail.esupagape.repository.StatistiquesRepository;
import org.esupportail.esupagape.service.utils.chartjs.*;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatistiquesService {

    private final StatistiquesRepository statistiquesRepository;
    private final MessageSource messageSource;

    public StatistiquesService(StatistiquesRepository statistiquesRepository, MessageSource messageSource) {
        this.statistiquesRepository = statistiquesRepository;
        this.messageSource = messageSource;
    }

    /*public Chart getClassificationChart(Integer year) {
        List<ClassificationChart> classificationCharts = statistiquesRepository.countFindClassificationByYear(year);
        Dataset dataset = new Dataset("Nombre d'individus", classificationCharts.stream().map(ClassificationChart::getClassificationCount).collect(Collectors.toList()), null, 4, null, null);
        List<String> labels = new ArrayList<>();
        for (String classification : classificationCharts.stream().map(ClassificationChart::getClassification).toList()) {
            labels.add(messageSource.getMessage("dossier.classification." + classification, null, Locale.getDefault()));
        }

        return new Doughnut(new Data(labels, Collections.singletonList(dataset)));
    }*/
    /*public Chart getClassificationChart(Integer year) {
        List<ClassificationChart> classificationCharts = statistiquesRepository.countFindClassificationByYear(year);
        List<String> classificationCounts = classificationCharts.stream().map(ClassificationChart::getClassificationCount).collect(Collectors.toList());
        List<String> percentages = new ArrayList<>();
        double sum = classificationCounts.stream().mapToDouble(Double::parseDouble).sum();
        for (String count : classificationCounts) {
            double percentage = Double.parseDouble(count) * 100 / sum;
            percentages.add(String.format("%.2f%%", percentage));
        }
        Dataset dataset = new Dataset("Nombre d'individus par classification", classificationCharts.stream().map(ClassificationChart::getClassificationCount).collect(Collectors.toList()), null, 4, null, null, percentages);
        dataset.addDataLabels(percentages);
        List<String> labels = new ArrayList<>();

        labels.addAll(classificationCharts.stream().map(c -> c.getClassification().name()).toList());

        return new Doughnut(new Data(labels, Collections.singletonList(dataset)));
    }*/

    public Chart getClassificationChart(Integer year) {
        List<ClassificationChart> classificationCharts = statistiquesRepository.countFindClassificationByYear(year);
        List<String> classificationCounts = classificationCharts.stream().map(ClassificationChart::getClassificationCount).collect(Collectors.toList());
        List<String> percentages = new ArrayList<>();
        double sum = classificationCounts.stream().mapToDouble(Double::parseDouble).sum();
        for (String count : classificationCounts) {
            double percentage = Double.parseDouble(count) * 100 / sum;
            percentages.add(String.format("%.2f%%", percentage));
        }

        Dataset dataset = new Dataset("Nombre d'individus par classification",
                classificationCharts.stream().map(ClassificationChart::getClassificationCount).collect(Collectors.toList()),
                null, 4, null, null, percentages);
        dataset.addDataLabels(percentages);

        List<String> labels = new ArrayList<>();
        for (ClassificationChart chart : classificationCharts) {
            Classification classification = chart.getClassification();
            if (classification != null) {
                labels.add(classification.name());
            } else {
                labels.add("N/A");
            }
        }

        return new Doughnut(new Data(labels, Collections.singletonList(dataset)));
    }



    public Chart getComposanteChart(Integer year) {
        List<ComposanteChart> composanteCharts = statistiquesRepository.countFindComposanteByYear(year);
        List<String> composanteCounts = composanteCharts.stream().map(ComposanteChart::getComposanteCount).collect(Collectors.toList());
        List<String> percentages = new ArrayList<>();
        double sum = composanteCounts.stream().mapToDouble(Double::parseDouble).sum();
        for (String count : composanteCounts) {
            double percentage = Double.parseDouble(count) * 100 / sum;
            percentages.add(String.format("%.2f%%", percentage));
        }

        Dataset dataset = new Dataset("Nombre d'individus par composante", composanteCharts.stream().map(ComposanteChart::getComposanteCount).collect(Collectors.toList()), null, 4, null, null,percentages);
        dataset.getDataLabels(percentages);
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
                    .sorted()
                    .collect(Collectors.toList());

            List<String> counts = new ArrayList<>();
            List<Integer> yAxesTicks = null;

            for (Integer year : years) {
                counts.add(String.valueOf(statistiquesRepository.countFindIndividuByYear(year)));
            }

            Dataset dataset = new Dataset("Nombre d'individus par année", counts, null, null, 1, 0.1,null);
            List<String> labels = years.stream().map(String::valueOf).collect(Collectors.toList());

            Options options = new Options();
            options.beginAtZero = true;
            options.yAxesTicks = yAxesTicks;

            Data data = new Data(labels, Collections.singletonList(dataset));
            return new Line(data, options);

        }

}
