package org.esupportail.esupagape.service.dataSource;

import org.esupportail.esupagape.config.individuSource.IndividuSourceProperties;
import org.esupportail.esupagape.service.interfaces.importIndividu.IndividuSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Service
@EnableConfigurationProperties(IndividuSourceProperties.class)
public class IndividuDataSourceService {

    private static final Logger logger = LoggerFactory.getLogger(IndividuSourceService.class);

    @Resource
    private IndividuSourceProperties individuSourceProperties;

    public DataSource getDataSourceByName(String name) {
        logger.info("initialize db " + name + " with type " + individuSourceProperties.getDataSources().get(name).getType());
        return individuSourceProperties.getDataSources().get(name).initializeDataSourceBuilder().build();
    }

    public JdbcTemplate getJdbcTemplateByName(String name) {
        return new JdbcTemplate(getDataSourceByName(name));
    }

}
