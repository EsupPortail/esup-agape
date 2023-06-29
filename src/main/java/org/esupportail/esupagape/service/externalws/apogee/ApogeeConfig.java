package org.esupportail.esupagape.service.externalws.apogee;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ApogeeProperties.class})
public class ApogeeConfig {

    private final ApogeeProperties apogeeProperties;

    public ApogeeConfig(ApogeeProperties apogeeProperties) {
        this.apogeeProperties = apogeeProperties;
    }

    @Bean
    @ConditionalOnProperty(value = {"apogee.etu-url"})
    public ApogeeEtuFactory etudiantMetierServiceInterface() {
        return new ApogeeEtuFactory(apogeeProperties.getEtuUrl());
    }

    @Bean
    @ConditionalOnProperty("apogee.administratif-url")
    public ApogeeAdministratifFactory apogeeAdministratifFactory() {
        return new ApogeeAdministratifFactory(apogeeProperties.getAdministratifUrl());
    }

    @Bean
    @ConditionalOnProperty(value = {"apogee.pedago-url"})
    public ApogeePedagoFactory apogeePedagoFactory() {
        return new ApogeePedagoFactory(apogeeProperties.getPedagoUrl());
    }

}
