package org.esupportail.esupagape.service.externalws.apogee;

import gouv.education.apogee.commun.client.ws.PedagogiqueMetier.PedagogiqueMetierServiceInterface;
import gouv.education.apogee.commun.client.ws.PedagogiqueMetier.PedagogiqueMetierServiceInterfaceService;

import java.net.MalformedURLException;
import java.net.URL;

public class ApogeePedagoFactory {
    
    public static PedagogiqueMetierServiceInterface createInstancePedago(String urlString) throws MalformedURLException {
        PedagogiqueMetierServiceInterface pedagogiqueMetierServiceInterface = new PedagogiqueMetierServiceInterfaceService(new URL(urlString)).getPedagogiqueMetier();
        return pedagogiqueMetierServiceInterface;
    }

}
