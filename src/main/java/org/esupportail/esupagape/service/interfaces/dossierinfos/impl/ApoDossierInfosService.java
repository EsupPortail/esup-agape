package org.esupportail.esupagape.service.interfaces.dossierinfos.impl;

import gouv.education.apogee.commun.client.ws.AdministratifMetier.InsAdmEtpDTO3;
import gouv.education.apogee.commun.client.ws.PedagogiqueMetier.ContratPedagogiqueResultatElpEprDTO5;
import gouv.education.apogee.commun.client.ws.PedagogiqueMetier.ResultatElpDTO3;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.service.externalws.apogee.WsApogeeServiceAdministratif;
import org.esupportail.esupagape.service.externalws.apogee.WsApogeeServicePedago;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Order(1)
public class ApoDossierInfosService implements DossierInfosService {

    private static final Logger logger = LoggerFactory.getLogger(ApoDossierInfosService.class);

    @Resource
    WsApogeeServicePedago wsApogeeServicePedago;

    @Resource
    WsApogeeServiceAdministratif wsApogeeServiceAdministratif;

    public List<Map<String, Object>> getDossierProperties(Individu individu, Integer annee, boolean getAllSteps) {

        List<Map<String, Object>> dossierProperties = new ArrayList<>();

        try {
            List<InsAdmEtpDTO3> ieEtapes = wsApogeeServiceAdministratif.recupererIAEtapes(individu.getNumEtu(), annee.toString());
            for (InsAdmEtpDTO3 insAdmEtpDTO : ieEtapes) {
                if (!insAdmEtpDTO.getEtapePremiere().equals("oui") && !getAllSteps) {
                    continue;
                }
                Map<String, Object> dossierDatas = new HashMap<>();
                dossierDatas.put("ufr", insAdmEtpDTO.getComposante().getLibComposante());
                dossierDatas.put("filiere", insAdmEtpDTO.getEtape().getLibWebVet());
                dossierDatas.put("etablissement", insAdmEtpDTO.getComposante().getLibComposante());
                ContratPedagogiqueResultatElpEprDTO5[] resultatElpEprDTOs = wsApogeeServicePedago
                        .recupererResultatsElpEprDTO(individu.getNumEtu(), annee.toString(), insAdmEtpDTO.getEtape().getCodeEtp(),
                                insAdmEtpDTO.getEtape().getVersionEtp());
                try {
                    for (ContratPedagogiqueResultatElpEprDTO5 resultatElpEprDTO : resultatElpEprDTOs) {

                        if (resultatElpEprDTO.getElp().getNatureElp().getCodNel().equals("SEM")
                                && resultatElpEprDTO.getElp().getNatureElp().getTemFictif().equals("N")
                                && resultatElpEprDTO.getElp().getNatureElp().getTemSemestre().equals("O")
                                && (resultatElpEprDTO.getElp().getNumPreElp()==1
                                        || resultatElpEprDTO.getElp().getNumPreElp()==3
                                        || resultatElpEprDTO.getElp().getNumPreElp()==5)) {
                            if(resultatElpEprDTO.getResultatsElp() != null) {
                                ResultatElpDTO3[] resultat = resultatElpEprDTO.getResultatsElp().getItem().toArray(new ResultatElpDTO3[0]);
                                dossierDatas.put("noteS1", resultat[0].getNotElp());
                                dossierDatas.put("resultatS1", resultat[0].getTypResultat().getLibTre());
                            }

                        }

                        if (resultatElpEprDTO.getElp().getNatureElp().getCodNel().equals("SEM")
                                && resultatElpEprDTO.getElp().getNatureElp().getTemFictif().equals("N")
                                && resultatElpEprDTO.getElp().getNatureElp().getTemSemestre().equals("O")
                                && (resultatElpEprDTO.getElp().getNumPreElp()==2
                                    || resultatElpEprDTO.getElp().getNumPreElp()==4
                                    || resultatElpEprDTO.getElp().getNumPreElp()==6)) {
                            if(resultatElpEprDTO.getResultatsElp() != null) {
                                ResultatElpDTO3[] resultat = resultatElpEprDTO.getResultatsElp().getItem().toArray(new ResultatElpDTO3[0]);
                                dossierDatas.put("noteS2", resultat[0].getNotElp());
                                dossierDatas.put("resultatS2", resultat[0].getTypResultat().getLibTre());
                            }
                        }

                        if (resultatElpEprDTO.getElp().getNatureElp().getCodNel().equals("MIR")
                            && 	resultatElpEprDTO.getElp().getNatureElp().getTemFictif().equals("N")) {
                            if(resultatElpEprDTO.getResultatsElp() != null) {
                                ResultatElpDTO3[] resultat = resultatElpEprDTO.getResultatsElp().getItem().toArray(new ResultatElpDTO3[0]);
                                dossierDatas.put("noteAnn", resultat[0].getNotElp());
                                if (resultat[0].getTypResultat() != null) {
                                    dossierDatas.put("resultatAnn", resultat[0].getTypResultat().getLibTre());
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    logger.warn("Erreur lors de la recup des resultats Vdi", e);
                }
            dossierProperties.add(dossierDatas);
            }
        } catch (Exception e) {
            logger.warn("Erreur lors de la recup des resultat VdiVet", e);
        }

        return dossierProperties;
    }
}
