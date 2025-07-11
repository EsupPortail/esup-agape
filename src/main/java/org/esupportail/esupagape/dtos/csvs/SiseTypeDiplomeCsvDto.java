package org.esupportail.esupagape.dtos.csvs;

import com.opencsv.bean.CsvBindByName;

public class SiseTypeDiplomeCsvDto {

    @CsvBindByName(column = "TYPE_DIPLOME_SISE")
    public String typeDiplomeSise = "";

    @CsvBindByName(column = "LIBELLE_COURT")
    public String libelle = "";
}
