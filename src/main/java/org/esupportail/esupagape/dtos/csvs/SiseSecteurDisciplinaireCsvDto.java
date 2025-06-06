package org.esupportail.esupagape.dtos.csvs;

import com.opencsv.bean.CsvBindByName;

import java.util.HashMap;
import java.util.Map;

public class SiseSecteurDisciplinaireCsvDto {

    @CsvBindByName(column = "SECTEUR_DISCIPLINAIRE_SISE")
    public String secteurDisciplinaireSise = "";

    @CsvBindByName(column = "LIBELLE_SECTEUR_DISCIPLINAIRE")
    public String libelle = "";

    public static String getCodFil(String secteurDisciplinaireSise) {
        Map<String, String> siseSecteurDisciplinaireToCodFilMap = new HashMap<>();
        siseSecteurDisciplinaireToCodFilMap.put("71", "medi");
        siseSecteurDisciplinaireToCodFilMap.put("80", "sci");
        siseSecteurDisciplinaireToCodFilMap.put("01", "sci");
        siseSecteurDisciplinaireToCodFilMap.put("02", "sci");
        siseSecteurDisciplinaireToCodFilMap.put("03", "sci");
        siseSecteurDisciplinaireToCodFilMap.put("04", "sci");
        siseSecteurDisciplinaireToCodFilMap.put("07", "medi");
        siseSecteurDisciplinaireToCodFilMap.put("08", "medi");
        siseSecteurDisciplinaireToCodFilMap.put("09", "medi");
        siseSecteurDisciplinaireToCodFilMap.put("10", "sta");
        siseSecteurDisciplinaireToCodFilMap.put("12", "sci");
        siseSecteurDisciplinaireToCodFilMap.put("14", "sci");
        siseSecteurDisciplinaireToCodFilMap.put("16", "sci");
        siseSecteurDisciplinaireToCodFilMap.put("18", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("19", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("20", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("21", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("22", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("23", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("24", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("25", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("26", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("27", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("28", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("29", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("31", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("33", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("34", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("35", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("36", "dsg");
        siseSecteurDisciplinaireToCodFilMap.put("37", "dsg");
        siseSecteurDisciplinaireToCodFilMap.put("38", "dsg");
        siseSecteurDisciplinaireToCodFilMap.put("39", "dsg");
        siseSecteurDisciplinaireToCodFilMap.put("40", "dsg");
        siseSecteurDisciplinaireToCodFilMap.put("41", "sci");
        siseSecteurDisciplinaireToCodFilMap.put("50", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("62", "dsg");
        siseSecteurDisciplinaireToCodFilMap.put("63", "dsg");
        siseSecteurDisciplinaireToCodFilMap.put("64", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("65", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("66", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("67", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("68", "sci");
        siseSecteurDisciplinaireToCodFilMap.put("69", "sci");
        siseSecteurDisciplinaireToCodFilMap.put("61", "dsg");
        siseSecteurDisciplinaireToCodFilMap.put("70", "sci");
        siseSecteurDisciplinaireToCodFilMap.put("42", "sci");
        siseSecteurDisciplinaireToCodFilMap.put("43", "sci");
        siseSecteurDisciplinaireToCodFilMap.put("32", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("44", "dsg");
        siseSecteurDisciplinaireToCodFilMap.put("05", "sci");
        siseSecteurDisciplinaireToCodFilMap.put("06", "sci");
        siseSecteurDisciplinaireToCodFilMap.put("15", "sci");
        siseSecteurDisciplinaireToCodFilMap.put("13", "sci");
        siseSecteurDisciplinaireToCodFilMap.put("11", "sci");
        siseSecteurDisciplinaireToCodFilMap.put("45", "dsg");
        siseSecteurDisciplinaireToCodFilMap.put("46", "dsg");
        siseSecteurDisciplinaireToCodFilMap.put("47", "dsg");
        siseSecteurDisciplinaireToCodFilMap.put("48", "dsg");
        siseSecteurDisciplinaireToCodFilMap.put("30", "lsh");
        siseSecteurDisciplinaireToCodFilMap.put("17", "lsh");
        return siseSecteurDisciplinaireToCodFilMap.get(secteurDisciplinaireSise);
    }

}
