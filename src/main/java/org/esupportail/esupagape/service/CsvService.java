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
        csvFormat.setHeader("Id", "Année", "numéro étudiant", "Code INE", "Genre", "Nom", "Prénom", "Date de naissance", "Type de l'individu", "Statut du dossier", "Statut du Dossier Aménagement");
        CSVPrinter printer;
        try {
            printer = new CSVPrinter(writer, csvFormat.build());
            for (DossierCompletCSVDto dossierCompletCSVDto : dossierCompletCSVDtos) {
                printer.printRecord(dossierCompletCSVDto.getId(),
                        dossierCompletCSVDto.getYear(),
                        dossierCompletCSVDto.getNumEtu(),
                        dossierCompletCSVDto.getCodeIne(),
                        dossierCompletCSVDto.getGender(),
                        dossierCompletCSVDto.getName(),
                        dossierCompletCSVDto.getFirstName(),
                        dossierCompletCSVDto.getDateOfBirth(),
                        dossierCompletCSVDto.getType(),
                        dossierCompletCSVDto.getStatusDossier(),
                        dossierCompletCSVDto.getStatusDossierAmenagement());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
