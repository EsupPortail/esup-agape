package org.esupportail.esupagape.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.esupportail.esupagape.dtos.DossierCompletCSVDto;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CsvService {

    public void writeDossierCompletToCsv(List<DossierCompletCSVDto> dossierCompletCSVDtos, Writer writer) {
        CSVFormat.Builder csvFormat = CSVFormat.Builder.create();
        csvFormat.setDelimiter(";");
        csvFormat.setHeader("id", "year", "numEtu", "nom", "prénom", "type", "statutDossier", "statutDossierAmenagement");
        CSVPrinter printer;
        try {
            printer = new CSVPrinter(writer, csvFormat.build());
            for (DossierCompletCSVDto dossierCompletCSVDto : dossierCompletCSVDtos) {
                printer.printRecord(dossierCompletCSVDto.getId(),
                        dossierCompletCSVDto.getYear(),
                        dossierCompletCSVDto.getNumEtu(),
                        dossierCompletCSVDto.getName(),
                        dossierCompletCSVDto.getFirstName(),
                        dossierCompletCSVDto.getType(),
                        dossierCompletCSVDto.getStatusDossier(),
                        dossierCompletCSVDto.getStatusDossierAmenagement());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
