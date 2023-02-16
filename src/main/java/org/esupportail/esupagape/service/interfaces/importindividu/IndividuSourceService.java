package org.esupportail.esupagape.service.interfaces.importindividu;

import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.Classification;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IndividuSourceService {

    IndividuInfos getIndividuProperties(String numEtu, IndividuInfos individuInfos, int annee);
    Individu getIndividuByNumEtu(String numEtu);
    Individu getIndividuByProperties(String codeIne, String name, String firstName, LocalDate dateOfBirth);


    List<Individu> getAllIndividuNums();
    Map<String, Classification> getClassificationMap();

}
