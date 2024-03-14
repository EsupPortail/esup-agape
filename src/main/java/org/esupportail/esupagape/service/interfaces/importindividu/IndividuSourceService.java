package org.esupportail.esupagape.service.interfaces.importindividu;

import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.Classification;
import org.esupportail.esupagape.exception.AgapeException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IndividuSourceService {

    IndividuInfos getIndividuProperties(String numEtu, IndividuInfos individuInfos, int annee);
    Individu getIndividuByNumEtu(String numEtu);
    Individu getIndividuByCodeIne(String codeIne);
    Individu getIndividuByProperties(String name, String firstName, LocalDate dateOfBirth);
    List<Individu> getAllIndividuNums() throws AgapeException, SQLException;
    Map<String, Classification> getClassificationMap();

}
