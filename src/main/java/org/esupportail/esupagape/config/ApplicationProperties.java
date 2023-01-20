package org.esupportail.esupagape.config;

import org.esupportail.esupagape.annotation.AgapeLdapAttributExist;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix="application")
@Validated
public class ApplicationProperties {

    /**
     * Adresse email du contact technique de l’application
     */
    private String applicationEmail = "esup.agape@univ-ville.fr";

    /**
     * Template de l’url de récupération de la photo
     * Ex : https://sgc.univ-ville.fr/wsrest/photo/{0}/photo
     */
    private String displayPhotoUriPattern = "";

    /**
     * Champ ldap utilisé pour la récupération de la photo
     */
    @AgapeLdapAttributExist
    private String mappingPhotoIdToLdapField;

    /**
     * Adresse de l'instance esup-signature
     * Ex : https://esup-signature.univ-ville.fr
     */
    private String esupSignatureUrl = "";

    public String getApplicationEmail() {
        return applicationEmail;
    }

    public void setApplicationEmail(String applicationEmail) {
        this.applicationEmail = applicationEmail;
    }

    public String getDisplayPhotoUriPattern() {
        return displayPhotoUriPattern;
    }

    public void setDisplayPhotoUriPattern(String displayPhotoUriPattern) {
        this.displayPhotoUriPattern = displayPhotoUriPattern;
    }

    public String getMappingPhotoIdToLdapField() {
        return mappingPhotoIdToLdapField;
    }

    public void setMappingPhotoIdToLdapField(String mappingPhotoIdToLdapField) {
        this.mappingPhotoIdToLdapField = mappingPhotoIdToLdapField;
    }

    public String getEsupSignatureUrl() {
        return esupSignatureUrl;
    }

    public void setEsupSignatureUrl(String esupSignatureUrl) {
        this.esupSignatureUrl = esupSignatureUrl;
    }
}
