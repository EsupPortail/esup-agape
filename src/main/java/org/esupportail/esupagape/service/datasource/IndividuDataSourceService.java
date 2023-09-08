package org.esupportail.esupagape.service.datasource;

import oracle.jdbc.datasource.impl.OracleDataSource;
import org.esupportail.esupagape.config.individusource.IndividuSourceProperties;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@EnableConfigurationProperties(IndividuSourceProperties.class)
public class IndividuDataSourceService {

    private static final Logger logger = LoggerFactory.getLogger(IndividuSourceService.class);

    private final IndividuSourceProperties individuSourceProperties;

    public IndividuDataSourceService(IndividuSourceProperties individuSourceProperties) {
        this.individuSourceProperties = individuSourceProperties;
    }

    public DataSource getDataSourceByName(String name) {
        logger.info("initialize db " + name + " with driver " + individuSourceProperties.getDataSources().get(name).getDriverClassName());
        DataSource dataSource = individuSourceProperties.getDataSources().get(name).initializeDataSourceBuilder().type(OracleDataSource.class).build();
        return dataSource;
    }

    public JdbcTemplate getJdbcTemplateByName(String name) {
        return new JdbcTemplate(getDataSourceByName(name));
    }

}
