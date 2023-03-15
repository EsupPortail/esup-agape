package org.esupportail.esupagape.service.utils.chartjs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Dataset {

    String label;
    List<String> data;
    List<String> backgroundColor = List.of(
            "#FF5C4D",
            "#FF9933",
            "#FFD134",
            "#FFFF4F",
            "#FFFF67",
            "#E3FF3C",
            "#51FF62",
            "#5ADBFF",
            "#5A9FFF",
            "#5A9FFF",
            "#D759FF",
            "#FF5AC1",
            "#A880FF",
            "#F7F7F7",
            "#CCCCCC",
            "#B2B2B2",
            "#4D4D4D",
            "#A45077",
            "#FDCA59",
            "#E64D4D",
            "#985972");
    Integer hoverOffset;

    public Dataset(String label, List<String> data, List<String> backgroundColor, Integer hoverOffset) {
        this.label = label;
        this.data = data;
        if(backgroundColor != null) {
            this.backgroundColor = backgroundColor;
        }
        this.hoverOffset = hoverOffset;
    }
}
