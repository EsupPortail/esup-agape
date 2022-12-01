package org.esupportail.esupagape.service.externalws.apogee;

import gouv.education.apogee.commun.client.ws.AdministratifMetier.AdministratifMetierServiceInterface;
import gouv.education.apogee.commun.client.ws.AdministratifMetier.AdministratifMetierServiceInterfaceService;
import org.esupportail.esupagape.exception.AgapeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class ApogeeAdministratifFactory {

    private static final Logger logger = LoggerFactory.getLogger(ApogeeAdministratifFactory.class);

    String urlWsdl;

    public ApogeeAdministratifFactory(String urlWsdl) {
        this.urlWsdl = urlWsdl;
    }

    public AdministratifMetierServiceInterface getInstanceAdministratif() throws AgapeException {
        try {
            return new AdministratifMetierServiceInterfaceService(new URL(urlWsdl)).getAdministratifMetier();
        } catch (Exception e) {
            throw new AgapeException("Error on get administratif ws instance " + e.getMessage());
        }
    }

}
