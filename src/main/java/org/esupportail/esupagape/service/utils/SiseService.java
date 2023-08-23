package org.esupportail.esupagape.service.utils;

import com.opencsv.bean.CsvToBeanBuilder;
import org.esupportail.esupagape.dtos.csvs.SiseDiplomeCsvDto;
import org.esupportail.esupagape.dtos.csvs.SiseSecteurDisciplinaireCsvDto;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SiseService {

    private static final Logger logger = LoggerFactory.getLogger(SiseService.class);

    Map<String, String> siseDiplomeCache = new HashMap<>();

    Map<String, String> siseSecteurDisciplinaireCache = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            File fileDiplome = new File("N_DIPLOME_SISE.csv");
            if(fileDiplome.length() == 0) {
                refreshSiseCsv("N_DIPLOME_SISE");
            }
            refreshDiplomeCache();
            File fileSecteurDisciplinaire = new File("N_SECTEUR_DISCIPLINAIRE_SISE.csv");
            if(fileSecteurDisciplinaire.length() == 0) {
                refreshSiseCsv("N_SECTEUR_DISCIPLINAIRE_SISE");
            }
            refreshSeteurDisciplinaireCache();
        } catch (FileNotFoundException e) {
            logger.error("error on init SISE files");
        }
    }

    public void refreshAll() throws FileNotFoundException {
        refreshSiseCsv("N_DIPLOME_SISE");
        refreshDiplomeCache();
        refreshSiseCsv("N_SECTEUR_DISCIPLINAIRE_SISE");
        refreshSeteurDisciplinaireCache();

    }

    private void refreshDiplomeCache() throws FileNotFoundException {
        File fileDiplome = new File("N_DIPLOME_SISE.csv");
        List<SiseDiplomeCsvDto> siseDiplomeCsvDtos = new CsvToBeanBuilder(new FileReader(fileDiplome)).withType(SiseDiplomeCsvDto.class).withSeparator(';').build().parse();
        for(SiseDiplomeCsvDto siseDiplomeCsvDto : siseDiplomeCsvDtos) {
            siseDiplomeCache.put(siseDiplomeCsvDto.diplomeSise, siseDiplomeCsvDto.libelle);
        }
    }

    private void refreshSeteurDisciplinaireCache() throws FileNotFoundException {
        File fileSecteurDisciplinaire = new File("N_SECTEUR_DISCIPLINAIRE_SISE.csv");
        List<SiseSecteurDisciplinaireCsvDto> siseSecteurDisciplinaireCsvDtos = new CsvToBeanBuilder(new FileReader(fileSecteurDisciplinaire)).withType(SiseSecteurDisciplinaireCsvDto.class).withSeparator(';').build().parse();
        for(SiseSecteurDisciplinaireCsvDto siseSecteurDisciplinaireCsvDto : siseSecteurDisciplinaireCsvDtos) {
            siseSecteurDisciplinaireCache.put(siseSecteurDisciplinaireCsvDto.secteurDisciplinaireSise, siseSecteurDisciplinaireCsvDto.libelle);
        }
    }

    private File refreshSiseCsv(String type) {
        RestTemplate restTemplate = new RestTemplate();
        File backup = new File(type + "-backup.csv");
        File file = new File(type + ".csv");
        if(file.length() > 0) {
            try {
                new FileOutputStream(backup).write(new FileInputStream(file).readAllBytes());
            } catch (IOException e) {
                throw new AgapeIOException("unable to backup");
            }
        }
        logger.info("get sise csv from : " + "https://infocentre.pleiade.education.fr/bcn/index.php/export/CSV?n=" + type + "&separator=;");
        restTemplate.execute("https://infocentre.pleiade.education.fr/bcn/index.php/export/CSV?n=" + type + "&separator=;", HttpMethod.GET, null, clientHttpResponse -> {
            StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(file));
            return file;
        });
        if(file.length() == 0 && backup.length() > 0) {
            try {
                new FileOutputStream(file).write(new FileInputStream(backup).readAllBytes());
            } catch (IOException e) {
                throw new AgapeIOException("unable to restore");
            }
        }
        return file;
    }

    public String getLibelleDiplome(String code) {
        return siseDiplomeCache.get(code);
    }

    public String getLibelleSecteurDisciplinaire(String code) {
        return siseSecteurDisciplinaireCache.get(code);
    }

}
