package org.esupportail.esupagape.service.interfaces.importindividu.impl;

import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.service.datasource.IndividuDataSourceService;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(value = "individu-source.data-sources.APO.name")
public class ApoIndividuSourceService implements IndividuSourceService {

    private static final Logger logger = LoggerFactory.getLogger(ApoIndividuSourceService.class);

    DataSource dataSource;

    public ApoIndividuSourceService(IndividuDataSourceService individuDataSourceService) {
        this.dataSource = individuDataSourceService.getDataSourceByName("APO");
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
    public Individu getIndividuByProperties(String name, String firstName, LocalDate dateOfBirth) {
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
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Individu> numEtus = new ArrayList<>();
        String sqlRequest = 
                "SELECT individu.cod_etu, individu.lib_nom_pat_ind, individu.lib_pr1_ind, individu.cod_sex_etu, individu.date_nai_ind " +
                "FROM individu " +
                "    INNER JOIN ins_adm_etp ON individu.cod_ind = ins_adm_etp.cod_ind " +
                "    INNER JOIN annee_uni ON ins_adm_etp.cod_anu = annee_uni.cod_anu " +
                "WHERE annee_uni.eta_anu_iae = 'O' " +
                "    AND individu.cod_thp IS NOT NULL " +
                "    AND ins_adm_etp.eta_iae = 'E' " +
                "    AND ins_adm_etp.eta_pmt_iae = 'P' " +
                "    AND ins_adm_etp.tem_iae_prm = 'O'" +
                "    AND ins_adm_etp.cod_cge not in ('NM1')";
        new JdbcTemplate(dataSource).query(sqlRequest, (ResultSet rs) -> {
            numEtus.add(new Individu(rs.getString("cod_etu"), rs.getString("lib_nom_pat_ind"), rs.getString("lib_pr1_ind"), rs.getString("cod_sex_etu"), LocalDate.parse(rs.getString("date_nai_ind"), dateTimeFormatter)));
            while (rs.next()) {
                numEtus.add(new Individu(rs.getString("cod_etu"), rs.getString("lib_nom_pat_ind"), rs.getString("lib_pr1_ind"), rs.getString("cod_sex_etu"), LocalDate.parse(rs.getString("date_nai_ind"), dateTimeFormatter)));
            }
        });
        try {
            Connection connection = dataSource.getConnection();
            connection.close();
        } catch (SQLException e) {
            logger.warn("unable to close APO connection");
        }
        return numEtus;
    }
}
