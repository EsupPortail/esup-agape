package org.esupportail.esupagape.service.interfaces.dossierinfos.impl;

import gouv.education.apogee.commun.client.ws.AdministratifMetier.InsAdmEtpDTO3;
import gouv.education.apogee.commun.client.ws.PedagogiqueMetier.ContratPedagogiqueResultatElpEprDTO5;
import gouv.education.apogee.commun.client.ws.PedagogiqueMetier.ResultatElpDTO3;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.exception.AgapeApogeeException;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.service.datasource.IndividuDataSourceService;
import org.esupportail.esupagape.service.externalws.apogee.WsApogeeServiceAdministratif;
import org.esupportail.esupagape.service.externalws.apogee.WsApogeeServicePedago;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfos;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Order(1)
@ConditionalOnProperty(value = {"individu-source.data-sources.APOGEE.name", "apogee.etu-url"})
public class ApoDossierInfosService implements DossierInfosService {

    private static final Logger logger = LoggerFactory.getLogger(ApoDossierInfosService.class);

    DataSource dataSource;

    private final WsApogeeServicePedago wsApogeeServicePedago;

    private final WsApogeeServiceAdministratif wsApogeeServiceAdministratif;

    public ApoDossierInfosService(IndividuDataSourceService individuDataSourceService, WsApogeeServicePedago wsApogeeServicePedago, WsApogeeServiceAdministratif wsApogeeServiceAdministratif) {
        this.dataSource = individuDataSourceService.getDataSourceByName("APOGEE");
        this.wsApogeeServicePedago = wsApogeeServicePedago;
        this.wsApogeeServiceAdministratif = wsApogeeServiceAdministratif;
    }

    public DossierInfos getDossierProperties(Individu individu, Integer annee, boolean getAllSteps, boolean getNotes, DossierInfos dossierInfos) {
        try {
            List<InsAdmEtpDTO3> ieEtapes = wsApogeeServiceAdministratif.recupererIAEtapes(individu.getNumEtu(), annee.toString());
            if(ieEtapes != null) {
                for (InsAdmEtpDTO3 insAdmEtpDTO : ieEtapes) {
                    if (!insAdmEtpDTO.getEtapePremiere().equals("oui") && !getAllSteps) {
                        continue;
                    }
                    dossierInfos.setCodComposante(insAdmEtpDTO.getComposante().getCodComposante());
//                    dossierInfos.setComposante(insAdmEtpDTO.getComposante().getLibComposante());
                    dossierInfos.setLibelleFormation(insAdmEtpDTO.getEtape().getLibWebVet());
                    if (insAdmEtpDTO.getBourse() != null) {
                        dossierInfos.setHasScholarship("02".equals(insAdmEtpDTO.getBourse().getCodeBourse()));
                    } else {
                        dossierInfos.setHasScholarship(false);
                    }

                    if(getNotes) {
                        ContratPedagogiqueResultatElpEprDTO5[] resultatElpEprDTOs = wsApogeeServicePedago
                                .recupererResultatsElpEprDTO(individu.getNumEtu(), annee.toString(), insAdmEtpDTO.getEtape().getCodeEtp(),
                                        insAdmEtpDTO.getEtape().getVersionEtp());
                        for (ContratPedagogiqueResultatElpEprDTO5 resultatElpEprDTO : resultatElpEprDTOs) {
                            if (resultatElpEprDTO.getElp().getNatureElp().getCodNel().equals("SEM")
                                    && resultatElpEprDTO.getElp().getNatureElp().getTemFictif().equals("N")
                                    && resultatElpEprDTO.getElp().getNatureElp().getTemSemestre().equals("O")
                                    && resultatElpEprDTO.getElp().getNumPreElp() != null
                                    && (resultatElpEprDTO.getElp().getNumPreElp() == 1
                                    || resultatElpEprDTO.getElp().getNumPreElp() == 3
                                    || resultatElpEprDTO.getElp().getNumPreElp() == 5)) {
                                if (resultatElpEprDTO.getResultatsElp() != null) {
                                    ResultatElpDTO3[] resultat = resultatElpEprDTO.getResultatsElp().getItem().toArray(new ResultatElpDTO3[0]);
                                    if (resultat[0] != null) {
                                        dossierInfos.setNoteS1(resultat[0].getNotElp());
                                        if (resultat[0].getTypResultat() != null) {
                                            dossierInfos.setResultatS1(resultat[0].getTypResultat().getLibTre());
                                        }
                                    }
                                }
                            }

                            if (resultatElpEprDTO.getElp().getNatureElp().getCodNel().equals("SEM")
                                    && resultatElpEprDTO.getElp().getNatureElp().getTemFictif().equals("N")
                                    && resultatElpEprDTO.getElp().getNatureElp().getTemSemestre().equals("O")
                                    && resultatElpEprDTO.getElp().getNumPreElp() != null
                                    && (resultatElpEprDTO.getElp().getNumPreElp() == 2
                                    || resultatElpEprDTO.getElp().getNumPreElp() == 4
                                    || resultatElpEprDTO.getElp().getNumPreElp() == 6)) {
                                if (resultatElpEprDTO.getResultatsElp() != null) {
                                    ResultatElpDTO3[] resultat = resultatElpEprDTO.getResultatsElp().getItem().toArray(new ResultatElpDTO3[0]);
                                    if (resultat[0] != null) {
                                        dossierInfos.setNoteS2(resultat[0].getNotElp());
                                        if (resultat[0].getTypResultat() != null) {
                                            dossierInfos.setResultatS2(resultat[0].getTypResultat().getLibTre());
                                        }
                                    }
                                }
                            }
                            if (resultatElpEprDTO.getElp().getNatureElp().getCodNel().equals("MIR")
                                    && resultatElpEprDTO.getElp().getNatureElp().getTemFictif().equals("N")) {
                                if (resultatElpEprDTO.getResultatsElp() != null) {
                                    ResultatElpDTO3[] resultat = resultatElpEprDTO.getResultatsElp().getItem().toArray(new ResultatElpDTO3[0]);
                                    dossierInfos.setNoteAnn(resultat[0].getNotElp());
                                    if (resultat[0].getTypResultat() != null) {
                                        dossierInfos.setResultatAnn(resultat[0].getTypResultat().getLibTre());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (AgapeException e) {
            if(e instanceof AgapeApogeeException) {
                logger.debug(e.getMessage());
            } else {
                logger.warn(e.getMessage());
            }
        }
        try {
            List<InsAdmEtpDTO3> ieEtapes = wsApogeeServiceAdministratif.recupererIAEtapes(individu.getNumEtu(), String.valueOf(annee - 1));
            if(ieEtapes != null) {
                for (InsAdmEtpDTO3 insAdmEtpDTO : ieEtapes) {
                    if (!insAdmEtpDTO.getEtapePremiere().equals("oui") && !getAllSteps) {
                        continue;
                    }
                    dossierInfos.setLibelleFormationPrec(insAdmEtpDTO.getEtape().getLibWebVet());
                }
            }
        } catch (AgapeException e) {
            if(e instanceof AgapeApogeeException) {
                logger.debug(e.getMessage());
            } else {
                logger.warn(e.getMessage());
            }
        }
        return dossierInfos;
    }

    @Override
    public Map<String, String> getCodComposanteLabels() throws AgapeException, SQLException {
        Map<String, String> codComposanteLabelsMap = new LinkedHashMap<>();
        codComposanteLabelsMap.put("ALL_ACCESS", "Toutes les composantes");
//        List<Individu> numEtus = new ArrayList<>();
        String sqlRequest =
                "SELECT composante.cod_cmp, composante.lib_cmp " +
                        "FROM composante";
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            new JdbcTemplate(dataSource).query(sqlRequest, (ResultSet rs) -> {
                codComposanteLabelsMap.put(rs.getString("cod_cmp"), rs.getString("lib_cmp"));
                while (rs.next()) {
                    codComposanteLabelsMap.put(rs.getString("cod_cmp"), rs.getString("lib_cmp"));
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
        return codComposanteLabelsMap;
    }
}
