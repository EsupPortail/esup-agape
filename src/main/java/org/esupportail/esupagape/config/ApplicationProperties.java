package org.esupportail.esupagape.config;

import org.esupportail.esupagape.annotation.AgapeLdapAttributExist;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix="application")
@Validated
public class ApplicationProperties {

    /**
     * Code établissement
     */
    private String codeEtab;

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
     * Délai avant anonymisation des individus (en années)
     */
    private Integer anonymiseDelay = 3;
    /**
     * Adresse de l'instance esup-signature
     * Ex : https://esup-signature.univ-ville.fr
     */
    private String esupSignatureUrl = "";

    /**
     * Id du circuit des avis
     */
    private String esupSignatureAvisWorkflowId = "";

    /**
     * Id du circuit des certificats
     */
    private String esupSignatureCertificatsWorkflowId = "";

    String papercutAuthToken;

    String papercutServer;

    String papercutScheme = "http";

    int papercutPort;

    String papercutAccountName = "";

    public String getCodeEtab() {
        return codeEtab;
    }

    public void setCodeEtab(String codeEtab) {
        this.codeEtab = codeEtab;
    }

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

    public Integer getAnonymiseDelay() {
        return anonymiseDelay;
    }

    public void setAnonymiseDelay(Integer anonymiseDelay) {
        this.anonymiseDelay = anonymiseDelay;
    }

    public String getEsupSignatureUrl() {
        return esupSignatureUrl;
    }

    public void setEsupSignatureUrl(String esupSignatureUrl) {
        this.esupSignatureUrl = esupSignatureUrl;
    }

    public String getEsupSignatureAvisWorkflowId() {
        return esupSignatureAvisWorkflowId;
    }

    public void setEsupSignatureAvisWorkflowId(String esupSignatureAvisWorkflowId) {
        this.esupSignatureAvisWorkflowId = esupSignatureAvisWorkflowId;
    }

    public String getEsupSignatureCertificatsWorkflowId() {
        return esupSignatureCertificatsWorkflowId;
    }

    public void setEsupSignatureCertificatsWorkflowId(String esupSignatureCertificatsWorkflowId) {
        this.esupSignatureCertificatsWorkflowId = esupSignatureCertificatsWorkflowId;
    }

    public String getPapercutAuthToken() {
        return papercutAuthToken;
    }

    public void setPapercutAuthToken(String papercutAuthToken) {
        this.papercutAuthToken = papercutAuthToken;
    }

    public String getPapercutServer() {
        return papercutServer;
    }

    public void setPapercutServer(String papercutServer) {
        this.papercutServer = papercutServer;
    }

    public String getPapercutScheme() {
        return papercutScheme;
    }

    public void setPapercutScheme(String papercutScheme) {
        this.papercutScheme = papercutScheme;
    }

    public int getPapercutPort() {
        return papercutPort;
    }

    public void setPapercutPort(int papercutPort) {
        this.papercutPort = papercutPort;
    }

    public String getPapercutAccountName() {
        return papercutAccountName;
    }

    public void setPapercutAccountName(String papercutAccountName) {
        this.papercutAccountName = papercutAccountName;
    }
}
