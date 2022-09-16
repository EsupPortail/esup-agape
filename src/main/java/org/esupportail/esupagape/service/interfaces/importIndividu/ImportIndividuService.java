package org.esupportail.esupagape.service.interfaces.importIndividu;

import org.esupportail.esupagape.entity.Individu;

import java.time.LocalDateTime;
import java.util.List;

public interface ImportIndividuService {

    public Individu getIndividu(String numEtu);
    public Individu getIndividu(String name, String firstname, LocalDateTime dateOfBirth);
    public List<Individu> getAllIndividus();

}
