package org.esupportail.esupagape.service.interfaces.importindividu.impl;

import gouv.education.apogee.commun.client.ws.EtudiantMetier.CoordonneesDTO2;
import gouv.education.apogee.commun.client.ws.EtudiantMetier.InfoAdmEtuDTO4;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.Classification;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.service.datasource.IndividuDataSourceService;
import org.esupportail.esupagape.service.externalws.apogee.WsApogeeServiceEtudiant;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuInfos;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuSourceService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Order(3)
@ConditionalOnProperty(value = {"individu-source.data-sources.APOGEE.name", "apogee.etu-url"})
public class ApoIndividuSourceService implements IndividuSourceService {

    private static final Logger logger = LoggerFactory.getLogger(ApoIndividuSourceService.class);

    DataSource dataSource;

    private final WsApogeeServiceEtudiant wsApogeeServiceEtudiant;

    private final UtilsService utilsService;

    private final Map<String, Classification> classificationMap = new HashMap<>();

    public ApoIndividuSourceService(IndividuDataSourceService individuDataSourceService, WsApogeeServiceEtudiant wsApogeeServiceEtudiant, UtilsService utilsService) {
        this.dataSource = individuDataSourceService.getDataSourceByName("APOGEE");
        this.wsApogeeServiceEtudiant = wsApogeeServiceEtudiant;
        this.utilsService = utilsService;

        this.classificationMap.put("A", Classification.TROUBLES_DES_FONCTIONS_AUDITIVES);
        this.classificationMap.put("B", Classification.TROUBLES_DES_FONCTIONS_AUDITIVES);
        this.classificationMap.put("M", Classification.MOTEUR);
        this.classificationMap.put("V", Classification.TROUBLES_DES_FONCTIONS_VISUELLES);
        this.classificationMap.put("W", Classification.TROUBLES_DES_FONCTIONS_VISUELLES);
        this.classificationMap.put("XX", Classification.TROUBLES_VISCERAUX);
        this.classificationMap.put("G", Classification.TROUBLE_DU_LANGAGE_OU_DE_LA_PAROLE);
        this.classificationMap.put("H", Classification.AUTISME);
        this.classificationMap.put("I", Classification.TROUBLES_PSYCHIQUES);
    }

    @Override
    public IndividuInfos getIndividuProperties(String numEtu, IndividuInfos individuInfos, int annee) {
        CoordonneesDTO2 coordonneesDTO2 = wsApogeeServiceEtudiant.recupererAdressesEtudiant(numEtu, String.valueOf(annee));
        if(coordonneesDTO2 != null) {
            if (StringUtils.hasText(coordonneesDTO2.getNumTelPortable())) {
                individuInfos.setContactPhone(coordonneesDTO2.getNumTelPortable());
            }
            if (coordonneesDTO2.getAdresseFixe() != null) {
                if(coordonneesDTO2.getAdresseFixe().getCommune() != null) {
                    individuInfos.setFixCity(coordonneesDTO2.getAdresseFixe().getCommune().getNomCommune());
                    individuInfos.setFixCP(coordonneesDTO2.getAdresseFixe().getCommune().getCodePostal());
                    if(StringUtils.hasText(coordonneesDTO2.getAdresseFixe().getLibAd1())) {
                        individuInfos.setFixAddress(coordonneesDTO2.getAdresseFixe().getLibAd1());
                    }
                    individuInfos.setFixCountry(coordonneesDTO2.getAdresseFixe().getPays().getLibPay());
                }
            }
        }
        InfoAdmEtuDTO4  infoAdmEtuDTO4 = wsApogeeServiceEtudiant.recupererInfosAdmEtu(numEtu);
        if(infoAdmEtuDTO4 != null) {
            if(infoAdmEtuDTO4.getHandicap() != null) {
                if(infoAdmEtuDTO4.getHandicap().getCodeHandicap() != null && getClassificationMap().containsKey(infoAdmEtuDTO4.getHandicap().getCodeHandicap())) {
                    individuInfos.setHandicap(getClassificationMap().get(infoAdmEtuDTO4.getHandicap().getCodeHandicap()));
                } else if(infoAdmEtuDTO4.getHandicap().getCodeHandicap() == null) {
                    individuInfos.setHandicap(Classification.NON_COMMUNIQUE);
                } else {
                    individuInfos.setHandicap(Classification.AUTRES_TROUBLES);
                }
            }
            if(infoAdmEtuDTO4.getNationaliteDTO() != null) {
                individuInfos.setNationalite(infoAdmEtuDTO4.getNationaliteDTO().getLibNationalite());
            }

        }
        return individuInfos;
    }

    @Override
    public Individu getIndividuByNumEtu(String numEtu) {
        return null;
    }

    @Override
    public Individu getIndividuByCodeIne(String codeIne) {
        return null;
    }

    @Override
    public Individu getIndividuByProperties(String name, String firstName, LocalDate dateOfBirth) {
        return null;
    }

    @Override
    public List<Individu> getAllIndividuNums() throws AgapeException, SQLException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Individu> numEtus = new ArrayList<>();
        String sqlRequest = 
                "SELECT individu.cod_etu, individu.lib_nom_pat_ind, individu.lib_pr1_ind, individu.cod_sex_etu, individu.date_nai_ind " +
                    "FROM individu " +
                    "INNER JOIN ins_adm_etp ON individu.cod_ind = ins_adm_etp.cod_ind " +
                "WHERE individu.cod_etu IS NOT NULL " +
                    "AND individu.cod_thp IS NOT NULL " +
                    "AND ins_adm_etp.cod_anu = '" + utilsService.getCurrentYear() + "' " +
                    "AND ins_adm_etp.eta_iae = 'E' " +
                    "AND ins_adm_etp.eta_pmt_iae = 'P' " +
                    "AND ins_adm_etp.tem_iae_prm = 'O' " +
                    "AND ins_adm_etp.cod_cge NOT IN ('NM1')";
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            new JdbcTemplate(dataSource).query(sqlRequest, (ResultSet rs) -> {
                numEtus.add(new Individu(rs.getString("cod_etu"), rs.getString("lib_nom_pat_ind"), rs.getString("lib_pr1_ind"), rs.getString("cod_sex_etu"), LocalDate.parse(rs.getString("date_nai_ind"), dateTimeFormatter)));
                while (rs.next()) {
                    numEtus.add(new Individu(rs.getString("cod_etu"), rs.getString("lib_nom_pat_ind"), rs.getString("lib_pr1_ind"), rs.getString("cod_sex_etu"), LocalDate.parse(rs.getString("date_nai_ind"), dateTimeFormatter)));
                }
            });
            connection.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AgapeException(e.getMessage(), e);
        } finally {
            if(connection != null) {
                connection.close();
            }
        }
        return numEtus;
    }

    @Override
    public Map<String, Classification> getClassificationMap() {
        return this.classificationMap;
    }

}
