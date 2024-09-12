package org.esupportail.esupagape.config;

import org.esupportail.esupagape.annotation.AgapeLdapAttributExist;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;


@ConfigurationProperties(prefix="application")
@Validated
public class ApplicationProperties {

    /**
     * Chemin d'accès à l'application
     */
    private String path;

    /**
     * Code établissement
     */
    private String codeEtab;

    /**
     * Adresse email du from
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

    /**
     * Adresse email du valideur
     */
    private List<String> esupSignatureValideursEmails = new ArrayList<>();

    /**
     * Filtre ldap pour récupérer le campus depuis memberOf
     */
    private String memberOfCampusFilter = "cn=adhoc.campus";

    /**
     * Affiche un texte en filigrane, ex : Plateforme de test
     */
    private String filigrane = "";

    private String modelsPath;

    private String signaturesPath;

    private String testEmail = "toto@univ-ville.fr";

    private Boolean enableSchedulerAnonymise = false;

    private Boolean enableSchedulerEsupSignature = false;

    private Boolean enableSchedulerAmenagement = false;

    private Boolean enableSchedulerIndividu = false;

    private Boolean activateSendEmails = false;

    private String papercutAuthToken;

    private String papercutServer;

    private String papercutScheme = "http";

    private int papercutPort;

    private String papercutAccountName = "";

    private Integer nbDossierNullBeforeAnonymise = -1;

    private Boolean enableSuiviHandisup = true;

    private Boolean enableAtypie = true;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

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

    public List<String> getEsupSignatureValideursEmails() {
        return esupSignatureValideursEmails;
    }

    public void setEsupSignatureValideursEmails(List<String> esupSignatureValideursEmails) {
        this.esupSignatureValideursEmails = esupSignatureValideursEmails;
    }

    public String getMemberOfCampusFilter() {
        return memberOfCampusFilter;
    }

    public void setMemberOfCampusFilter(String memberOfCampusFilter) {
        this.memberOfCampusFilter = memberOfCampusFilter;
    }

    public String getFiligrane() {
        return filigrane;
    }

    public void setFiligrane(String filigrane) {
        this.filigrane = filigrane;
    }

    public String getModelsPath() {
        return modelsPath;
    }

    public void setModelsPath(String modelsPath) {
        this.modelsPath = modelsPath;
    }

    public String getSignaturesPath() {
        return signaturesPath;
    }

    public void setSignaturesPath(String signaturesPath) {
        this.signaturesPath = signaturesPath;
    }

    public String getTestEmail() {
        return testEmail;
    }

    public void setTestEmail(String testEmail) {
        this.testEmail = testEmail;
    }

    public Boolean getEnableSchedulerAnonymise() {
        return enableSchedulerAnonymise;
    }

    public void setEnableSchedulerAnonymise(Boolean enableSchedulerAnonymise) {
        this.enableSchedulerAnonymise = enableSchedulerAnonymise;
    }

    public Boolean getEnableSchedulerEsupSignature() {
        return enableSchedulerEsupSignature;
    }

    public void setEnableSchedulerEsupSignature(Boolean enableSchedulerEsupSignature) {
        this.enableSchedulerEsupSignature = enableSchedulerEsupSignature;
    }

    public Boolean getEnableSchedulerAmenagement() {
        return enableSchedulerAmenagement;
    }

    public void setEnableSchedulerAmenagement(Boolean enableSchedulerAmenagement) {
        this.enableSchedulerAmenagement = enableSchedulerAmenagement;
    }

    public Boolean getEnableSchedulerIndividu() {
        return enableSchedulerIndividu;
    }

    public void setEnableSchedulerIndividu(Boolean enableSchedulerIndividu) {
        this.enableSchedulerIndividu = enableSchedulerIndividu;
    }

    public Boolean getActivateSendEmails() {
        return activateSendEmails;
    }

    public void setActivateSendEmails(Boolean activateSendEmails) {
        this.activateSendEmails = activateSendEmails;
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

    public Integer getNbDossierNullBeforeAnonymise() {
        return nbDossierNullBeforeAnonymise;
    }

    public void setNbDossierNullBeforeAnonymise(Integer nbDossierNullBeforeAnonymise) {
        this.nbDossierNullBeforeAnonymise = nbDossierNullBeforeAnonymise;
    }

    public Boolean getEnableSuiviHandisup() {
        return enableSuiviHandisup;
    }

    public void setEnableSuiviHandisup(Boolean enableSuiviHandisup) {
        this.enableSuiviHandisup = enableSuiviHandisup;
    }

    public Boolean getEnableAtypie() {
        return enableAtypie;
    }

    public void setEnableAtypie(Boolean enableAtypie) {
        this.enableAtypie = enableAtypie;
    }
}
