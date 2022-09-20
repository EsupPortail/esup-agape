package org.esupportail.esupagape.service.interfaces.importIndividu.impl;

import org.esupportail.esupagape.service.dataSource.IndividuDataSourceService;
import org.esupportail.esupagape.service.interfaces.importIndividu.IndividuSourceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(value = "individu-source.data-sources.PEGASE.name")
public class PegaseIndividuSourceService implements IndividuSourceService {

    DataSource dataSource;

    public PegaseIndividuSourceService(IndividuDataSourceService individuDataSourceService) {
        this.dataSource = individuDataSourceService.getDataSourceByName("PEGASE");
    }

    @Override
    public Map<String, String> getSourcesPropertiesMapping() {
        return null;
    }

    @Override
    public Map<String, String> getIndividuProperties(String numEtu) {
        return null;
    }

    @Override
    public Map<String, String> getIndividuProperties(String name, String firstname, LocalDateTime dateOfBirth) {
        return null;
    }

    @Override
    public List<String> getAllIndividuNums() {
        return null;
    }
}
