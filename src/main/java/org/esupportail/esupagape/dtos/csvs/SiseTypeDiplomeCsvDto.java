package org.esupportail.esupagape.dtos.csvs;

import com.opencsv.bean.CsvBindByName;

import java.util.HashMap;
import java.util.Map;

public class SiseTypeDiplomeCsvDto {

    @CsvBindByName(column = "TYPE_DIPLOME_SISE")
    public String typeDiplomeSise = "";

    @CsvBindByName(column = "LIBELLE_COURT")
    public String libelle = "";

    public static String getCodFmt(String typeDiplomeSise) {
        Map<String, String> siseTypeDiplomeToCodFmtMap = new HashMap<>();
        siseTypeDiplomeToCodFmtMap.put("FI", "ing");    // FORM ING -> Formation d’ingénieur
        siseTypeDiplomeToCodFmtMap.put("XA", "L");      // LIC LMD -> Licence
        siseTypeDiplomeToCodFmtMap.put("JD", "medi");   // DES MED -> Formation de santé
        siseTypeDiplomeToCodFmtMap.put("XB", "M");      // MAST LMD -> Master
        siseTypeDiplomeToCodFmtMap.put("IB", "medi");   // DE MEDECIN -> Formation de santé
        siseTypeDiplomeToCodFmtMap.put("FH", "medi");   // DE PHARM -> Formation de santé
        siseTypeDiplomeToCodFmtMap.put("PF", "para");   // DE MASSEUR -> Formation paramédicale
        siseTypeDiplomeToCodFmtMap.put("XF", "Mf");     // MAST ENS -> Master MEEF
        siseTypeDiplomeToCodFmtMap.put("XD", "Mf");     // MAST ENS -> Master MEEF
        siseTypeDiplomeToCodFmtMap.put("YB", "D");      // DOCT.UNI.G -> Doctorat
        siseTypeDiplomeToCodFmtMap.put("DP", "Lp");     // LIC PRO -> Licence professionnelle
        siseTypeDiplomeToCodFmtMap.put("UF", "X");      // D.UNIV. B5 -> Autres formations
        siseTypeDiplomeToCodFmtMap.put("VF", "X");      // P CONC ENS -> Autres formations
        siseTypeDiplomeToCodFmtMap.put("CD", "X");      // DEUST -> Autres formations
        siseTypeDiplomeToCodFmtMap.put("CZ", "medi");   // DFG SANTE -> Formation de santé
        siseTypeDiplomeToCodFmtMap.put("PC", "para");   // DE AUDIOP -> Formation paramédicale
        siseTypeDiplomeToCodFmtMap.put("DR", "but");    // BUT -> BUT
        siseTypeDiplomeToCodFmtMap.put("ZH", "X");      // CERT COMPE -> Autres formations
        siseTypeDiplomeToCodFmtMap.put("AC", "CA");     // CAPA DROIT -> Capacité en droit
        siseTypeDiplomeToCodFmtMap.put("ID", "medi");   // DFA SANTE -> Formation de santé
        siseTypeDiplomeToCodFmtMap.put("PE", "para");   // DE SAGE-FE -> Formation paramédicale
        siseTypeDiplomeToCodFmtMap.put("PG", "para");   // DE ERGOTH -> Formation paramédicale
        siseTypeDiplomeToCodFmtMap.put("CV", "Las");    // PASS -> Licence accès santé
        siseTypeDiplomeToCodFmtMap.put("PB", "para");   // C.ORTHOPT -> Formation paramédicale
        siseTypeDiplomeToCodFmtMap.put("PJ", "para");   // INFIR GR.L -> Formation paramédicale
        siseTypeDiplomeToCodFmtMap.put("01", "DU");      // DU GENERI -> DU
        return siseTypeDiplomeToCodFmtMap.get(typeDiplomeSise);
    }
}
