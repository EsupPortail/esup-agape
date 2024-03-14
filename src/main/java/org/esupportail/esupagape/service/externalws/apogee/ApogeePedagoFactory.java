package org.esupportail.esupagape.service.externalws.apogee;

import gouv.education.apogee.commun.client.ws.PedagogiqueMetier.PedagogiqueMetierServiceInterface;
import gouv.education.apogee.commun.client.ws.PedagogiqueMetier.PedagogiqueMetierServiceInterfaceService;
import org.esupportail.esupagape.exception.AgapeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class ApogeePedagoFactory {

    private static final Logger logger = LoggerFactory.getLogger(ApogeePedagoFactory.class);

    String urlWsdl;

    public ApogeePedagoFactory(String urlWsdl) {
        this.urlWsdl = urlWsdl;
    }

    public PedagogiqueMetierServiceInterface getInstancePedago() throws AgapeException {
        try {
            return new PedagogiqueMetierServiceInterfaceService(new URL(urlWsdl)).getPedagogiqueMetier();
        } catch (Exception e) {
            throw new AgapeException("Error on get pedago ws instance " + e.getMessage());
        }
    }

}
