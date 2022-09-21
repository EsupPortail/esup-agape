package org.esupportail.esupagape.service.interfaces.importindividu.impl;

import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.service.datasource.IndividuDataSourceService;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(value = "individu-source.data-sources.PEGASE.name")
public class PegaseIndividuSourceService implements IndividuSourceService {

    private static final Logger logger = LoggerFactory.getLogger(PegaseIndividuSourceService.class);

    DataSource dataSource;

    public PegaseIndividuSourceService(IndividuDataSourceService individuDataSourceService) {
        this.dataSource = individuDataSourceService.getDataSourceByName("PEGASE");
    }

    @Override
    public Map<String, String> getSourcesPropertiesMapping() {
        return null;
    }

    @Override
    public Map<String, Object> getIndividuProperties(String numEtu) {
        return new HashMap<>();
    }

    @Override
    public Individu getIndividuByNumEtu(String numEtu) {
        return null;
    }

    @Override
    public Individu getIndividuByProperties(String name, String firstName, LocalDate dateOfBirth, String sex) {
        return null;
    }

    @Override
    public Map<String, String> getIndividuProperties(String name, String firstname, LocalDateTime dateOfBirth) {
        return null;
    }

    @Override
    public void updateIndividu(Individu individu) {

    }

    @Override
    public void updateDossier(Individu individu, int year) {

    }

    @Override
    public List<Individu> getAllIndividuNums() {
        return null;
    }
}
