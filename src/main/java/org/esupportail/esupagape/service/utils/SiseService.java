package org.esupportail.esupagape.service.utils;

import com.opencsv.bean.CsvToBeanBuilder;
import org.esupportail.esupagape.dtos.csvs.SiseDiplomeCsvDto;
import org.esupportail.esupagape.dtos.csvs.SiseSecteurDisciplinaireCsvDto;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;

@Service
public class SiseService {

    List<SiseDiplomeCsvDto> siseDiplomeCsvDtos;

    List<SiseSecteurDisciplinaireCsvDto> siseSecteurDisciplinaireCsvDtos;

    @PostConstruct
    public void init() {
        try {
            File fileDiplome = new File("N_DIPLOME_SISE.csv");
            if(fileDiplome.length() == 0) {
                fileDiplome = refreshSiseCsv("N_DIPLOME_SISE");
            }
            siseDiplomeCsvDtos = new CsvToBeanBuilder(new FileReader(fileDiplome)).withType(SiseDiplomeCsvDto.class).withSeparator(';').build().parse();
            File fileSecteurDisciplinaire = new File("N_SECTEUR_DISCIPLINAIRE_SISE.csv");
            if(fileSecteurDisciplinaire.length() == 0) {
                fileSecteurDisciplinaire = refreshSiseCsv("N_SECTEUR_DISCIPLINAIRE_SISE");
            }
            siseSecteurDisciplinaireCsvDtos = new CsvToBeanBuilder(new FileReader(fileSecteurDisciplinaire)).withType(SiseSecteurDisciplinaireCsvDto.class).withSeparator(';').build().parse();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshAll() {
        refreshSiseCsv("N_DIPLOME_SISE");
        refreshSiseCsv("N_SECTEUR_DISCIPLINAIRE_SISE");
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
        restTemplate.execute("https://infocentre.pleiade.education.fr/bcn/index.php/export/CSV?n=" + type + "&separator=;", HttpMethod.GET, null, clientHttpResponse -> {
            StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(file));
            return file;
        });
        if(file.length() == 0 && backup.length() > 0) {
            try {
                new FileOutputStream(file).write(new FileInputStream(backup).readAllBytes());
            } catch (IOException e) {
                throw new AgapeIOException("unable to backup");
            }
        }
        return file;
    }

    public String getLibelleDiplome(String code) {
        return siseDiplomeCsvDtos.stream().filter(s -> s.diplomeSise.equals(code)).findFirst().orElse(new SiseDiplomeCsvDto()).libelle;
    }

    public String getLibelleSecteurDisciplinaire(String code) {
        return siseSecteurDisciplinaireCsvDtos.stream().filter(s -> s.secteurDisciplinaireSise.equals(code)).findFirst().orElse(new SiseSecteurDisciplinaireCsvDto()).libelle;
    }

}
