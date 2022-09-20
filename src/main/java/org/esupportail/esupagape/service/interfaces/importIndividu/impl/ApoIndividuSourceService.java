package org.esupportail.esupagape.service.interfaces.importIndividu.impl;

import org.esupportail.esupagape.service.dataSource.IndividuDataSourceService;
import org.esupportail.esupagape.service.interfaces.importIndividu.IndividuSourceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(value = "individu-source.data-sources.APO.name")
public class ApoIndividuSourceService implements IndividuSourceService {

    DataSource dataSource;

    public ApoIndividuSourceService(IndividuDataSourceService individuDataSourceService) {
        this.dataSource = individuDataSourceService.getDataSourceByName("APO");
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
        List<String> numEtus = new ArrayList<>();
        String sqlRequest = "\n" +
                "SELECT\n" +
                "    individu.cod_etu\n" +
                "FROM\n" +
                "         individu\n" +
                "    INNER JOIN ins_adm_etp ON individu.cod_ind = ins_adm_etp.cod_ind\n" +
                "    INNER JOIN annee_uni ON ins_adm_etp.cod_anu = annee_uni.cod_anu\n" +
                "WHERE\n" +
                "        annee_uni.eta_anu_iae = 'O'\n" +
                "    AND individu.cod_thp IS NOT NULL\n" +
                "    AND ins_adm_etp.eta_iae = 'E'\n" +
                "    AND ins_adm_etp.eta_pmt_iae = 'P'\n" +
                "    AND ins_adm_etp.tem_iae_prm = 'O'" +
                "    AND ins_adm_etp.cod_cge not in ('NM1')";
        new JdbcTemplate(dataSource).query(sqlRequest, (ResultSet rs) -> {
            numEtus.add(rs.getString("cod_etu"));
            while (rs.next()) {
                numEtus.add(rs.getString("cod_etu"));
            }
        });
        return numEtus;
    }
}
