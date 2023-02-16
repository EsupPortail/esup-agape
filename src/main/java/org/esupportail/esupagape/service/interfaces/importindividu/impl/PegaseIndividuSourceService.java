package org.esupportail.esupagape.service.interfaces.importindividu.impl;

import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.Classification;
import org.esupportail.esupagape.service.datasource.IndividuDataSourceService;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuInfos;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Order(4)
@ConditionalOnProperty(value = "individu-source.data-sources.PEGASE.name")
public class PegaseIndividuSourceService implements IndividuSourceService {

    private static final Logger logger = LoggerFactory.getLogger(PegaseIndividuSourceService.class);

    DataSource dataSource;

    public PegaseIndividuSourceService(IndividuDataSourceService individuDataSourceService) {
        this.dataSource = individuDataSourceService.getDataSourceByName("PEGASE");
    }

    @Override
    public IndividuInfos getIndividuProperties(String numEtu, IndividuInfos individuInfos, int annee) {
        return individuInfos;
    }

    @Override
    public Individu getIndividuByNumEtu(String numEtu) {
        return null;
    }

    @Override
    public Individu getIndividuByProperties(String codeIne, String name, String firstName, LocalDate dateOfBirth) {
        return null;
    }

    @Override
    public List<Individu> getAllIndividuNums() {
        return null;
    }

    @Override
    public Map<String, Classification> getClassificationMap() {
        return null;
    }
}
