package org.esupportail.esupagape.service.externalws.apogee;

import gouv.education.apogee.commun.client.ws.AdministratifMetier.AdministratifMetierServiceInterface;
import gouv.education.apogee.commun.client.ws.AdministratifMetier.AdministratifMetierServiceInterfaceService;

import java.net.MalformedURLException;
import java.net.URL;

public class ApogeeAdministratifFactory {
    
    public static AdministratifMetierServiceInterface createInstanceAdministratif(String urlString) throws MalformedURLException {
        AdministratifMetierServiceInterface administratifMetierServiceInterface = new AdministratifMetierServiceInterfaceService(new URL(urlString)).getAdministratifMetier();
        return administratifMetierServiceInterface;
    }

}
