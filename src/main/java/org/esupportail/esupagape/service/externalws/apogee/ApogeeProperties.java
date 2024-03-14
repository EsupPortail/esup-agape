package org.esupportail.esupagape.service.externalws.apogee;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="apogee")
public class ApogeeProperties {

    private String etuUrl;

    private String administratifUrl;

    private String pedagoUrl;

    public String getEtuUrl() {
        return etuUrl;
    }

    public void setEtuUrl(String etuUrl) {
        this.etuUrl = etuUrl;
    }

    public String getAdministratifUrl() {
        return administratifUrl;
    }

    public void setAdministratifUrl(String administratifUrl) {
        this.administratifUrl = administratifUrl;
    }

    public String getPedagoUrl() {
        return pedagoUrl;
    }

    public void setPedagoUrl(String pedagoUrl) {
        this.pedagoUrl = pedagoUrl;
    }
}
