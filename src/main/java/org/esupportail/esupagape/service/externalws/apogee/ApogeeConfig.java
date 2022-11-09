package org.esupportail.esupagape.service.externalws.apogee;

import gouv.education.apogee.commun.client.ws.AdministratifMetier.AdministratifMetierServiceInterface;
import gouv.education.apogee.commun.client.ws.EtudiantMetier.EtudiantMetierServiceInterface;
import gouv.education.apogee.commun.client.ws.PedagogiqueMetier.PedagogiqueMetierServiceInterface;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;

@Configuration
@EnableConfigurationProperties({ApogeeProperties.class})
@ConditionalOnProperty(value = {"apogee.etu-url", "apogee.administratif-url", "apogee.pedago-url"})
public class ApogeeConfig {

    private final ApogeeProperties apogeeProperties;

    public ApogeeConfig(ApogeeProperties apogeeProperties) {
        this.apogeeProperties = apogeeProperties;
    }

    @Bean
    public EtudiantMetierServiceInterface etudiantMetierServiceInterface() throws MalformedURLException {
        return ApogeeEtuFactory.createInstanceEtudiant(apogeeProperties.getEtuUrl());
    }

    @Bean
    public AdministratifMetierServiceInterface administratifMetierServiceInterface() throws MalformedURLException {
        return ApogeeAdministratifFactory.createInstanceAdministratif(apogeeProperties.getAdministratifUrl());
    }

    @Bean
    public PedagogiqueMetierServiceInterface pedagogiqueMetierServiceInterface() throws MalformedURLException {
        return ApogeePedagoFactory.createInstancePedago(apogeeProperties.getPedagoUrl());
    }

}
