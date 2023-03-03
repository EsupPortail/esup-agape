package org.esupportail.esupagape.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.esupportail.esupagape.dtos.DossierCompletCSVDto;
import org.esupportail.esupagape.exception.AgapeRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

@Service
public class CsvExportService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private final static byte[] EXCEL_UTF8_HACK = new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF};

    public void writeExcelHackToCsv(HttpServletResponse response) {
        try {
            response.getOutputStream().write(EXCEL_UTF8_HACK);
        } catch (IOException e) {
            throw new AgapeRuntimeException("Enable to hack csv");
        }
    }

    Map<String, String> dossierCompletCsv = new LinkedHashMap<>() {{
        put("id",                           "Id");
        put("year",                         "Année");
        put("numEtu",                       "Numéro étudiant");
        put("codeIne",                      "Code INE");
        put("gender",                       "Genre");
        put("name",                         "Nom");
        put("firstName",                    "Prénom");
        put("dateOfBirth",                  "Date de naissance");
        put("emailEtu",                     "Email étudiant");
        put("emailPerso",                   "Email perso");
        put("fixAddress",                   "Adresse");
        put("fixCp",                        "Code postal");
        put("fixCity",                      "Ville");
        put("fixCountry",                   "Pays");
        put("type",                         "Type d’individu");
        put("statusDossier",                "Statut du dossier");
        put("statusDossierAmenagement",     "Statut de l’aménagement");
        put("classification",               "Classification du handicap");
        put("mdph",                         "Suivi MDPH");
        put("taux",                         "Taux");
        put("TypeSuiviHandisup",            "Suivi Handisup");
        put("commentaire",                  "Commentaire");
        put("typeFormation",                "Type de formation");
        put("modeFormation",                "Modalités de formation");
        put("site",                         "Établissement");
    }};

    public void writeDossierCompletToCsv(List<DossierCompletCSVDto> dossierCompletCSVDtos, Writer writer) {
        CSVFormat.Builder csvFormat = CSVFormat.Builder.create(CSVFormat.EXCEL);
        csvFormat.setDelimiter(";");
        csvFormat.setQuote('"');
        csvFormat.setQuoteMode(QuoteMode.ALL);
        csvFormat.setHeader(dossierCompletCsv.values().toArray(String[]::new));
//        csvFormat.setHeader("Id", "Année", "Numéro étudiant", "Code INE", "Genre", "Nom", "Prénom",
//                "Date de naissance", "Email étudiant", "Email perso", "Adresse", "Code postal", "Ville", "Pays",
//                "Type de l’individu", "Statut du dossier", "Statut du Dossier Aménagement", "Classification du handicap",
//                 "Suivi MDPH", "Taux", "Suivi Handisup", "Commentaire", "Type de formation",
//                "Modalités de formation", "Etablissement", "Formation", "Formation précédente",
//                "Code composante", "Composante","Adresse de formation", "Resultat 1er semestre",
//                "Note 1er semestre", "Resultat 2e semestre", "Note 2e semestre",
//                "Resultat total", "Note total", "suivi Handisup", "N° étudiant de l'aidant",
//                "Nom de l'aidant", "Prénom de l'aidant", "Date de naissance de l'aidant", "Email de l'aidant",
//                "tél de l'aidant", "Date de début du contrat", "Fonction de l'aidant","Type d'aide matérielle", "Date de fin de l'aide matérielle",
//                "Coût", "Commentaire");
        try {
            CSVPrinter printer = new CSVPrinter(writer, csvFormat.build());
            for (DossierCompletCSVDto dossierCompletCSVDto : dossierCompletCSVDtos) {
                List<String> record = new ArrayList<>();
                for(String methodName : dossierCompletCsv.keySet()) {
                    try {
                        record.add(DossierCompletCSVDto.class.getDeclaredMethod("get" + StringUtils.capitalize(methodName)).invoke(dossierCompletCSVDto).toString());
                    } catch (Exception e) {
                        record.add("");
                        logger.debug(methodName + " doesn't exist");
                    }
                }
                printer.printRecord(record);
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
