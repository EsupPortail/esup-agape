package org.esupportail.esupagape.service.interfaces.dossierinfos.impl;

import gouv.education.apogee.commun.client.ws.AdministratifMetier.InsAdmEtpDTO3;
import gouv.education.apogee.commun.client.ws.PedagogiqueMetier.ContratPedagogiqueResultatElpEprDTO5;
import gouv.education.apogee.commun.client.ws.PedagogiqueMetier.ResultatElpDTO3;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.exception.AgapeApogeeException;
import org.esupportail.esupagape.service.externalws.apogee.WsApogeeServiceAdministratif;
import org.esupportail.esupagape.service.externalws.apogee.WsApogeeServicePedago;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfos;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Order(1)
@ConditionalOnBean(value = {WsApogeeServicePedago.class, WsApogeeServiceAdministratif.class})
public class ApoDossierInfosService implements DossierInfosService {

    private static final Logger logger = LoggerFactory.getLogger(ApoDossierInfosService.class);

    private final WsApogeeServicePedago wsApogeeServicePedago;

    private final WsApogeeServiceAdministratif wsApogeeServiceAdministratif;

    public ApoDossierInfosService(WsApogeeServicePedago wsApogeeServicePedago, WsApogeeServiceAdministratif wsApogeeServiceAdministratif) {
        this.wsApogeeServicePedago = wsApogeeServicePedago;
        this.wsApogeeServiceAdministratif = wsApogeeServiceAdministratif;
    }

    public DossierInfos getDossierProperties(Individu individu, Integer annee, boolean getAllSteps, DossierInfos dossierInfos) {
        try {
            List<InsAdmEtpDTO3> ieEtapes = wsApogeeServiceAdministratif.recupererIAEtapes(individu.getNumEtu(), annee.toString());
            if(ieEtapes != null) {
                for (InsAdmEtpDTO3 insAdmEtpDTO : ieEtapes) {
                    if (!insAdmEtpDTO.getEtapePremiere().equals("oui") && !getAllSteps) {
                        continue;
                    }
                    dossierInfos.setCodComposante(insAdmEtpDTO.getComposante().getCodComposante());
                    dossierInfos.setComposante(insAdmEtpDTO.getComposante().getLibComposante());
                    dossierInfos.setLibelleFormation(insAdmEtpDTO.getEtape().getLibWebVet());
                    ContratPedagogiqueResultatElpEprDTO5[] resultatElpEprDTOs = wsApogeeServicePedago
                            .recupererResultatsElpEprDTO(individu.getNumEtu(), annee.toString(), insAdmEtpDTO.getEtape().getCodeEtp(),
                                    insAdmEtpDTO.getEtape().getVersionEtp());
                    for (ContratPedagogiqueResultatElpEprDTO5 resultatElpEprDTO : resultatElpEprDTOs) {

                        if (resultatElpEprDTO.getElp().getNatureElp().getCodNel().equals("SEM")
                                && resultatElpEprDTO.getElp().getNatureElp().getTemFictif().equals("N")
                                && resultatElpEprDTO.getElp().getNatureElp().getTemSemestre().equals("O")
                                && (resultatElpEprDTO.getElp().getNumPreElp() == 1
                                || resultatElpEprDTO.getElp().getNumPreElp() == 3
                                || resultatElpEprDTO.getElp().getNumPreElp() == 5)) {
                            if (resultatElpEprDTO.getResultatsElp() != null) {
                                ResultatElpDTO3[] resultat = resultatElpEprDTO.getResultatsElp().getItem().toArray(new ResultatElpDTO3[0]);
                                dossierInfos.setNoteS1(resultat[0].getNotElp());
                                dossierInfos.setResultatS1(resultat[0].getTypResultat().getLibTre());
                            }

                        }

                        if (resultatElpEprDTO.getElp().getNatureElp().getCodNel().equals("SEM")
                                && resultatElpEprDTO.getElp().getNatureElp().getTemFictif().equals("N")
                                && resultatElpEprDTO.getElp().getNatureElp().getTemSemestre().equals("O")
                                && (resultatElpEprDTO.getElp().getNumPreElp() == 2
                                || resultatElpEprDTO.getElp().getNumPreElp() == 4
                                || resultatElpEprDTO.getElp().getNumPreElp() == 6)) {
                            if (resultatElpEprDTO.getResultatsElp() != null) {
                                ResultatElpDTO3[] resultat = resultatElpEprDTO.getResultatsElp().getItem().toArray(new ResultatElpDTO3[0]);
                                dossierInfos.setNoteS2(resultat[0].getNotElp());
                                dossierInfos.setResultatS2(resultat[0].getTypResultat().getLibTre());
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
        } catch (AgapeApogeeException e) {
            logger.warn(e.getMessage());
        }

        return dossierInfos;
    }
}
