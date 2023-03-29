package org.esupportail.esupagape.dtos.csvs;

import com.opencsv.bean.CsvBindByName;

public class SiseDiplomeCsvDto {

    @CsvBindByName(column = "DIPLOME_SISE")
    public String diplomeSise = "";

    @CsvBindByName(column = "LIBELLE_INTITULE_1")
    public String libelle = "";

}
