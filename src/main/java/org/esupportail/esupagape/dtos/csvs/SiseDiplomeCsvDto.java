package org.esupportail.esupagape.dtos.csvs;

import com.opencsv.bean.CsvBindByName;

import java.util.HashMap;
import java.util.Map;

public class SiseDiplomeCsvDto {

    @CsvBindByName(column = "DIPLOME_SISE")
    public String diplomeSise = "";

    @CsvBindByName(column = "LIBELLE_INTITULE_1")
    public String libelle = "";

    public static String getCodSco(String niveau) {
        Map<String, String> niveauMap = new HashMap<>();
        niveauMap.put("L1", "1");
        niveauMap.put("L2", "2");
        niveauMap.put("L3", "3");
        niveauMap.put("M1", "4");
        niveauMap.put("M2", "5");
        niveauMap.put("D1", "6+");
        niveauMap.put("D2", "6+");
        niveauMap.put("D3", "6+");
        niveauMap.put("X1", "1");
        niveauMap.put("X2", "2");
        niveauMap.put("X3", "3");
        niveauMap.put("X4", "4");
        niveauMap.put("X5", "5");
        niveauMap.put("X6", "6+");
        return niveauMap.get(niveau);
    }
}