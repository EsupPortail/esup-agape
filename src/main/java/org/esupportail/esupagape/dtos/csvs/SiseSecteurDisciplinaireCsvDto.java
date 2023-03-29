package org.esupportail.esupagape.dtos.csvs;

import com.opencsv.bean.CsvBindByName;

public class SiseSecteurDisciplinaireCsvDto {

    @CsvBindByName(column = "SECTEUR_DISCIPLINAIRE_SISE")
    public String secteurDisciplinaireSise = "";

    @CsvBindByName(column = "LIBELLE_SECTEUR_DISCIPLINAIRE")
    public String libelle = "";

}
