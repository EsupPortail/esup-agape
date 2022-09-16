package org.esupportail.esupagape.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix="application")
public class ApplicationProperties {

    /**
     * Liste des interfaces actives pour la récupération des individus
     */
    private List<String> enabledImportInterfaces = new ArrayList<>();

    public List<String> getEnabledImportInterfaces() {
        return enabledImportInterfaces;
    }

    public void setEnabledImportInterfaces(List<String> enabledImportInterfaces) {
        this.enabledImportInterfaces = enabledImportInterfaces;
    }
}
