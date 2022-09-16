package org.esupportail.esupagape.service.interfaces.importIndividu.impl;

import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.service.interfaces.importIndividu.ImportIndividuService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PegaseImportIndividuService implements ImportIndividuService {

    @Override
    public Individu getIndividu(String numEtu) {
        return null;
    }

    @Override
    public Individu getIndividu(String name, String firstname, LocalDateTime dateOfBirth) {
        return null;
    }

    @Override
    public List<Individu> getAllIndividus() {
        return null;
    }
}
