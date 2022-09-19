package org.esupportail.esupagape.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix="application")
public class ApplicationProperties {


    private String casTitle;
    private String casUrl;
    private String casService;

    /**
     * Liste des interfaces actives pour la récupération des individus
     */
    private List<String> enabledImportInterfaces = new ArrayList<>();

    public String getCasTitle() {
        return casTitle;
    }

    public void setCasTitle(String casTitle) {
        this.casTitle = casTitle;
    }

    public String getCasUrl() {
        return casUrl;
    }

    public void setCasUrl(String casUrl) {
        this.casUrl = casUrl;
    }

    public String getCasService() {
        return casService;
    }

    public void setCasService(String casService) {
        this.casService = casService;
    }

    public List<String> getEnabledImportInterfaces() {
        return enabledImportInterfaces;
    }

    public void setEnabledImportInterfaces(List<String> enabledImportInterfaces) {
        this.enabledImportInterfaces = enabledImportInterfaces;
    }
}
