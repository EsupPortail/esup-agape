package org.esupportail.esupagape.dtos.charts;

import org.esupportail.esupagape.entity.enums.Classification;

public interface ClassificationChart {
    Classification getClassification();
    String getClassificationCount();
}