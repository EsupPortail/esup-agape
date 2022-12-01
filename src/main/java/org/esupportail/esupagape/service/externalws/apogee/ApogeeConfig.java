package org.esupportail.esupagape.service.externalws.apogee;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ApogeeProperties.class})
@ConditionalOnProperty(value = {"apogee.etu-url", "apogee.administratif-url", "apogee.pedago-url"})
public class ApogeeConfig {

    private final ApogeeProperties apogeeProperties;

    public ApogeeConfig(ApogeeProperties apogeeProperties) {
        this.apogeeProperties = apogeeProperties;
    }

    @Bean
    public ApogeeEtuFactory etudiantMetierServiceInterface() {
        return new ApogeeEtuFactory(apogeeProperties.getEtuUrl());
    }

    @Bean
    public ApogeeAdministratifFactory apogeeAdministratifFactory() {
        return new ApogeeAdministratifFactory(apogeeProperties.getAdministratifUrl());
    }

    @Bean
    public ApogeePedagoFactory apogeePedagoFactory() {
        return new ApogeePedagoFactory(apogeeProperties.getPedagoUrl());
    }

}
