package org.esupportail.esupagape.service.interfaces.importindividu;

import org.esupportail.esupagape.entity.Individu;

import java.time.LocalDate;
import java.util.List;

public interface IndividuSourceService {

    IndividuInfos getIndividuProperties(String numEtu, IndividuInfos individuInfos);
    Individu getIndividuByNumEtu(String numEtu);
    Individu getIndividuByProperties(String name, String firstName, LocalDate dateOfBirth);
    List<Individu> getAllIndividuNums();

}
