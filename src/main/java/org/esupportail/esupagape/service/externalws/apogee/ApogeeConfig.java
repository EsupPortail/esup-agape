package org.esupportail.esupagape.service.externalws.apogee;

import gouv.education.apogee.commun.client.ws.AdministratifMetier.AdministratifMetierServiceInterface;
import gouv.education.apogee.commun.client.ws.EtudiantMetier.EtudiantMetierServiceInterface;
import gouv.education.apogee.commun.client.ws.PedagogiqueMetier.PedagogiqueMetierServiceInterface;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;

@Configuration
public class ApogeeConfig {

    @Bean
    public EtudiantMetierServiceInterface etudiantMetierServiceInterface() throws MalformedURLException, ServiceException {
        return ApogeeEtuFactory.createInstanceEtudiant("https://ws.univ-rouen.fr/apowsInscportal/services/EtudiantMetier");
    }

    @Bean
    public AdministratifMetierServiceInterface administratifMetierServiceInterface() throws MalformedURLException, ServiceException {
        return ApogeeAdministratifFactory.createInstanceAdministratif("https://ws.univ-rouen.fr/apowsInscportal/services/AdministratifMetier");
    }

    @Bean
    public PedagogiqueMetierServiceInterface pedagogiqueMetierServiceInterface() throws MalformedURLException, ServiceException {
        return ApogeePedagoFactory.createInstancePedago("https://ws.univ-rouen.fr/apowsInscportal/services/PedagogiqueMetier");
    }

}
