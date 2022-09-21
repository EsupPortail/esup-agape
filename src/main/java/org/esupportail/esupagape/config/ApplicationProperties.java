package org.esupportail.esupagape.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="application")
public class ApplicationProperties {

    /**
     * Adresse email du contact technique de l’application
     */
    private String applicationEmail = "esup.agape@univ-ville.fr";

    public String getApplicationEmail() {
        return applicationEmail;
    }

    public void setApplicationEmail(String applicationEmail) {
        this.applicationEmail = applicationEmail;
    }
}
