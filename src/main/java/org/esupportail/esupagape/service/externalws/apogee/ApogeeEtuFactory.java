package org.esupportail.esupagape.service.externalws.apogee;


import gouv.education.apogee.commun.client.ws.EtudiantMetier.EtudiantMetierServiceInterface;
import gouv.education.apogee.commun.client.ws.EtudiantMetier.EtudiantMetierServiceInterfaceService;
import org.esupportail.esupagape.exception.AgapeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;


public class ApogeeEtuFactory {

    private static final Logger logger = LoggerFactory.getLogger(ApogeeEtuFactory.class);

    String urlWsdl;

    public ApogeeEtuFactory(String urlWsdl) {
        this.urlWsdl = urlWsdl;
    }

    public EtudiantMetierServiceInterface getInstanceEtudiant() throws AgapeException {
        try {
            return new EtudiantMetierServiceInterfaceService(new URL(urlWsdl)).getEtudiantMetier();
        } catch (Exception e) {
            logger.warn("Error on get etu ws instance : " + e.getMessage());
            throw new AgapeException("Error on get etu ws instance");
        }
    }

}
