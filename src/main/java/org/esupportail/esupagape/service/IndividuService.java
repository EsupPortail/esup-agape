package org.esupportail.esupagape.service;

import org.esupportail.esupagape.service.interfaces.importIndividu.IndividuSourceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndividuService {

    private final List<IndividuSourceService> importIndividuServices;


    public IndividuService(List<IndividuSourceService> importIndividuServices) {
        this.importIndividuServices = importIndividuServices;
    }
}
