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
            "#88338C",
            "#FFFF4F",
            "#284EE8",
            "#E3FF3C",
            "#51FF62",
            "#5ADBFF",
            "#208A8C",
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
    Integer borderWith;



    public Dataset(String label, List<String> data, List<String> backgroundColor, Integer hoverOffset, Integer borderWith) {
        this.label = label;
        this.data = data;
        if(backgroundColor != null) {
            this.backgroundColor = backgroundColor;
        }
        this.hoverOffset = hoverOffset;
        this.borderWith = borderWith;
    }
}
