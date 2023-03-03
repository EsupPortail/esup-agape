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

    private final static byte[] EXCEL_UTF8_HACK = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    public void writeExcelHackToCsv(HttpServletResponse response) {
        try {
            response.getOutputStream().write(EXCEL_UTF8_HACK);
        } catch (IOException e) {
            throw new AgapeRuntimeException("Enable to hack csv");
        }
    }

    Map<String, String> dossierCompletCsv = new LinkedHashMap<>() {{
        put("id",                       "Id");
        put("year",                     "Année");
        put("numEtu",                   "Numéro étudiant");
        put("codeIne",                  "Code INE");
        put("gender",                   "Genre");
        put("name",                     "Nom");
        put("firstName",                "Prénom");
        put("emailEtu",                 "Email étudiant");
        put("emailPerso",               "Email perso");
        put("dateOfBirth",              "Date de naissance");
        put("fixAddress",               "Adresse");
        put("fixCP",                    "Code postal");
        put("fixCity",                  "Ville");
        put("fixCountry",               "Pays");
        put("type",                     "Type de l'individu");
        put("statusDossier",            "Statut du dossier");
        put("statusDossierAmenagement", "Statut du Dossier Aménagement");
        put("classification",           "Classification du handicap");
        put("mdph",                     "Suivi MDPH");
        put("taux",                     "Taux");
        put("typeSuiviHandisup",        "Suivi Handisup");
        put("commentaire",              "Commentaire");
        put("typeFormation",            "Type de formation");
        put("modeFormation",            "Modalités de formation");
        put("site",                     "Etablissement");
        put("libelleFormation",         "Formation");
        put("libelleFormationPrec",     "Formation précédente");
        put("codComposante",            "Code composante");
        put("composante",               "Composante");
        put("formAddress",              "Adresse de formation");
        put("resultatS1",               "Resultat 1er semestre");
        put("noteS1",                   "Note 1er semestre");
        put("resultatS2",               "Resultat 2e semestre");
        put("noteS2",                   "Note 2e semestre");
        put("resultatTotal",            "Resultat total");
        put("noteTotal",                "Note total");
        put("suiviHandisup",            "suivi Handisup");
        put("numEtuAidant",             "N° étudiant de l'aidant");
        put("nameAidant",               "Nom de l'aidant");
        put("firstNameAidant",          "Prénom de l'aidant");
        put("dateOfBirthAidant",        "Date de naissance de l'aidant");
        put("emailAidant",              "Email de l'aidant");
        put("phoneAidant",              "Tél de l'aidant");
        put("startDate",                "Date de début du contrat");
        put("fonctionAidants",          "Fonction de l'aidant");
        put("typeAideMaterielle",       "Type d'aide matérielle");
        put("endDate",                  "Date de fin de l'aide matérielle");
        put("cost",                     "Coût");
        put("comment",                  "Commentaire");
        put("autorisation",             "Autorisation");
        put("statusAmenagement",        "Statut de l'amenagement");
        put("statusAideHumaine",        "Statut de l'aide humaine");
        put("mailIndividu",             "Mail de l'individu");
        put("typeAmenagement",          "Type d'amenagement");
        put("typeEpreuves",             "Type d'epreuve");
        put("autresTypeEpreuve",        "Autre type d'epreuve");
        put("tempMajore",               "Temps Majores");
        put("autresTempsMajores",       "Autre temps majore");
        put("amenagementText",          "Texte");
        put("mailMedecin",              "Mail du médecin");
        put("nomMedecin",               "Nom du médecin");
        put("mailValideur",             "Mail du valideur");
        put("nomValideur",              "Nom du valideur");
        put("motifRefus",               "Motif du refus");
        put("createDate",               "Date de création");
        put("valideMedecinDate",        "Date de validation du medecin");
        put("administrationDate",       "Date de validation de l'administration");
        put("deleteDate",               "Date de suppression");
    }};

    public void writeDossierCompletToCsv(List<DossierCompletCSVDto> dossierCompletCSVDtos, Writer writer) {
        CSVFormat.Builder csvFormat = CSVFormat.Builder.create(CSVFormat.EXCEL);
        csvFormat.setDelimiter(";");
        csvFormat.setQuote('"');
        csvFormat.setQuoteMode(QuoteMode.ALL);
        csvFormat.setHeader(dossierCompletCsv.values().toArray(String[]::new));
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
