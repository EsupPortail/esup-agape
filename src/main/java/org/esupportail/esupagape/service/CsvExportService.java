package org.esupportail.esupagape.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.esupportail.esupagape.dtos.DossierCompletCSVDto;
import org.esupportail.esupagape.exception.AgapeRuntimeException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

@Service
public class CsvExportService {

    private final static byte[] EXCEL_UTF8_HACK = new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF};

    public void writeExcelHackToCsv(HttpServletResponse response) {
        try {
            response.getOutputStream().write(EXCEL_UTF8_HACK);
        } catch (IOException e) {
            throw new AgapeRuntimeException("Enable to hack csv");
        }
    }

    public void writeDossierCompletToCsv(List<DossierCompletCSVDto> dossierCompletCSVDtos, Writer writer) {
        CSVFormat.Builder csvFormat = CSVFormat.Builder.create(CSVFormat.EXCEL);
        csvFormat.setDelimiter(";");
        csvFormat.setQuote('"');
        csvFormat.setQuoteMode(QuoteMode.ALL);
        csvFormat.setHeader("Id", "Année", "numéro étudiant", "Code INE", "Genre", "Nom", "Prénom", "Date de naissance", "email", "Adresse", "Code postal", "Ville", "Pays", "Type de l'individu", "Statut du dossier", "Statut du Dossier Aménagement", "Adresse de formation");
        try {
            CSVPrinter printer = new CSVPrinter(writer, csvFormat.build());
            for (DossierCompletCSVDto dossierCompletCSVDto : dossierCompletCSVDtos) {
                printer.printRecord(dossierCompletCSVDto.getId(),
                dossierCompletCSVDto.getYear(),
                dossierCompletCSVDto.getNumEtu(),
                dossierCompletCSVDto.getCodeIne(),
                dossierCompletCSVDto.getGender(),
                dossierCompletCSVDto.getName(),
                dossierCompletCSVDto.getFirstName(),
                dossierCompletCSVDto.getDateOfBirth(),
                dossierCompletCSVDto.getEmailEtu(),
                dossierCompletCSVDto.getFixAddress(),
                dossierCompletCSVDto.getFixCP(),
                dossierCompletCSVDto.getFixCity(),
                dossierCompletCSVDto.getFixCountry(),
                dossierCompletCSVDto.getType(),
                dossierCompletCSVDto.getStatusDossier(),
                dossierCompletCSVDto.getStatusDossierAmenagement(),
                dossierCompletCSVDto.getFormAddress());
            }
        } catch (IOException e) {
            throw new AgapeRuntimeException("Enable to write export csv");
        }
    }

    public void writeEmailsToCsv(List<DossierCompletCSVDto> dossierCompletCSVDtos, Writer writer) {
        CSVFormat.Builder csvFormat = CSVFormat.Builder.create(CSVFormat.EXCEL);
        csvFormat.setDelimiter(";");
        csvFormat.setQuote('"');
        csvFormat.setQuoteMode(QuoteMode.ALL);
        csvFormat.setHeader("Email universitaire", "Email personnel");
        CSVPrinter printer;
        try {
            printer = new CSVPrinter(writer, csvFormat.build());
            for (DossierCompletCSVDto dossierCompletCSVDto : dossierCompletCSVDtos) {
                printer.printRecord(dossierCompletCSVDto.getEmailEtu(),
                dossierCompletCSVDto.getEmailPerso());
            }
        } catch (IOException e) {
            throw new AgapeRuntimeException("Enable to write email csv");
        }
    }
}
