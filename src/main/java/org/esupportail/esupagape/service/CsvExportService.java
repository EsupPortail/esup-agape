package org.esupportail.esupagape.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.esupportail.esupagape.dtos.DossierCompletCSVDto;
import org.esupportail.esupagape.exception.AgapeRuntimeException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

@Service
public class CsvExportService {

    private final MessageSource messageSource;

    private final static byte[] EXCEL_UTF8_HACK = new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF};

    public CsvExportService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

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
        csvFormat.setHeader("Id", "Année", "Numéro étudiant", "Code INE", "Genre", "Nom", "Prénom",
                "Date de naissance", "Email étudiant", "Email perso", "Adresse", "Code postal", "Ville", "Pays",
                "Type de l'individu", "Statut du dossier", "Statut du Dossier Aménagement", "Classification du handicap",
                 "Suivi MDPH", "Taux", "Suivi Handisup", "Commentaire", "Type de formation",
                "Modalités de formation", "Etablissement", "Formation", "Formation précédente",
                "Code composante", "Composante","Adresse de formation", "Resultat 1er semestre",
                "Note 1er semestre", "Resultat 2e semestre", "Note 2e semestre",
                "Resultat total", "Note total", "suivi Handisup", "N° étudiant de l'aidant",
                "Nom de l'aidant", "Prénom de l'aidant", "Date de naissance de l'aidant", "Email de l'aidant",
                "tél de l'aidant", "Date de début du contrat", "Fonction de l'aidant","Type d'aide matérielle", "Date de fin de l'aide matérielle",
                "Coût", "Commentaire");
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
                dossierCompletCSVDto.getEmailPerso(),
                dossierCompletCSVDto.getFixAddress(),
                dossierCompletCSVDto.getFixCP(),
                dossierCompletCSVDto.getFixCity(),
                dossierCompletCSVDto.getFixCountry(),
                dossierCompletCSVDto.getType(),
                dossierCompletCSVDto.getStatusDossier(),
                dossierCompletCSVDto.getStatusDossierAmenagement(),
                dossierCompletCSVDto.getClassification(),
                dossierCompletCSVDto.getMdph(),
                dossierCompletCSVDto.getTaux(),
                dossierCompletCSVDto.getTypeSuiviHandisup(),
                dossierCompletCSVDto.getCommentaire(),
                dossierCompletCSVDto.getTypeFormation(),
                dossierCompletCSVDto.getModeFormation(),
                dossierCompletCSVDto.getSite(),
                dossierCompletCSVDto.getLibelleFormation(),
                dossierCompletCSVDto.getLibelleFormationPrec(),
                dossierCompletCSVDto.getCodComposante(),
                dossierCompletCSVDto.getComposante(),
                dossierCompletCSVDto.getFormAddress(),
                dossierCompletCSVDto.getResultatS1(),
                dossierCompletCSVDto.getNoteS1(),
                dossierCompletCSVDto.getResultatS2(),
                dossierCompletCSVDto.getNoteS2(),
                dossierCompletCSVDto.getResultatTotal(),
                dossierCompletCSVDto.getNoteTotal(),
                dossierCompletCSVDto.getSuiviHandisup(),
                dossierCompletCSVDto.getNumEtuAidant(),
                dossierCompletCSVDto.getNameAidant(),
                dossierCompletCSVDto.getFirstNameAidant(),
                dossierCompletCSVDto.getDateOfBirthAidant(),
                dossierCompletCSVDto.getEmailAidant(),
                dossierCompletCSVDto.getPhoneAidant(),
                dossierCompletCSVDto.getStartDate(),
                dossierCompletCSVDto.getFonctionAidants(),
                dossierCompletCSVDto.getTypeAideMaterielle(),
                dossierCompletCSVDto.getEndDate(),
                dossierCompletCSVDto.getCost(),
                dossierCompletCSVDto.getComment());
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
