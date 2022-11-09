package org.esupportail.esupagape.service.externalws.apogee;


import gouv.education.apogee.commun.client.ws.EtudiantMetier.EtudiantMetierServiceInterface;
import gouv.education.apogee.commun.client.ws.EtudiantMetier.EtudiantMetierServiceInterfaceService;

import java.net.MalformedURLException;
import java.net.URL;


public class ApogeeEtuFactory {

    public static EtudiantMetierServiceInterface createInstanceEtudiant(String urlString) throws MalformedURLException {
        EtudiantMetierServiceInterface etudiantMetierServiceInterface = new EtudiantMetierServiceInterfaceService(new URL(urlString)).getEtudiantMetier();
        return etudiantMetierServiceInterface;
   }

}
