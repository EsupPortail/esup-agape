package org.esupportail.esupagape.service.interfaces.importindividu;

import org.esupportail.esupagape.entity.Individu;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IndividuSourceService {

    Map<String, String> getSourcesPropertiesMapping();
    Map<String, Object> getIndividuProperties(String numEtu);
    Individu getIndividuByNumEtu(String numEtu);
    Individu getIndividuByProperties(String name, String firstName, LocalDate dateOfBirth, String sex);
    Map<String, String> getIndividuProperties(String name, String firstname, LocalDateTime dateOfBirth);
    void updateIndividu(Individu individu);
    void updateDossier(Individu individu, int year);
    List<Individu> getAllIndividuNums();

}
