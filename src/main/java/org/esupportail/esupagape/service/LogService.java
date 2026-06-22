package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.Log;
import org.esupportail.esupagape.repository.LogRepository;
import org.esupportail.esupagape.service.ldap.LdapPersonService;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class LogService {

    private final LogRepository logRepository;
    private final ObjectProvider<LdapPersonService> ldapPersonService;

    public LogService(LogRepository logRepository, ObjectProvider<LdapPersonService> ldapPersonService) {
        this.logRepository = logRepository;
        this.ldapPersonService = ldapPersonService;
    }

    @Transactional
    public void create(String eppn, Long dossierId, String initalStatutDossier, String finalStatutDossier) {
        create(eppn, resolveUserDisplayName(eppn), dossierId, "DOSSIER", initalStatutDossier, finalStatutDossier);
    }

    @Transactional
    public void create(String eppn, Long dossierId, String entityType, String initalStatutDossier, String finalStatutDossier) {
        create(eppn, resolveUserDisplayName(eppn), dossierId, entityType, initalStatutDossier, finalStatutDossier);
    }

    @Transactional
    public void create(PersonLdap personLdap, Long dossierId, String entityType, String initalStatutDossier, String finalStatutDossier) {
        String eppn = personLdap != null ? personLdap.getUid() : null;
        String userDisplayName = personLdap != null ? getPersonDisplayName(personLdap) : resolveUserDisplayName(eppn);
        create(eppn, userDisplayName, dossierId, entityType, initalStatutDossier, finalStatutDossier);
    }

    private void create(String eppn, String userDisplayName, Long dossierId, String entityType, String initalStatutDossier, String finalStatutDossier) {
        Log log = new Log();
        log.setDate(LocalDateTime.now());
        log.setEppn(eppn);
        log.setUserDisplayName(userDisplayName);
        log.setEntityType(entityType);
        log.setInitialStatusDossier(cleanStatus(initalStatutDossier));
        log.setFinalStatusDossier(cleanStatus(finalStatutDossier));
        log.setDossierId(dossierId);
        logRepository.save(log);
    }

    public Page<Log> getAll(Pageable pageable) {
        return logRepository.findAll(pageable);
    }

    private String resolveUserDisplayName(String eppn) {
        if (!StringUtils.hasText(eppn) || "SYSTEM".equalsIgnoreCase(eppn)) {
            return "SYSTEM";
        }
        LdapPersonService service = ldapPersonService.getIfAvailable();
        if (service == null) {
            return eppn;
        }
        try {
            PersonLdap personLdap = service.getPersonLdap(eppn);
            if (personLdap != null) {
                return getPersonDisplayName(personLdap);
            }
        } catch (RuntimeException ignored) {
            return eppn;
        }
        return eppn;
    }

    private String getPersonDisplayName(PersonLdap personLdap) {
        if (StringUtils.hasText(personLdap.getDisplayName())) {
            return personLdap.getDisplayName();
        }
        String fullName = String.join(" ",
                StringUtils.hasText(personLdap.getGivenName()) ? personLdap.getGivenName() : "",
                StringUtils.hasText(personLdap.getSn()) ? personLdap.getSn() : "").trim();
        if (StringUtils.hasText(fullName)) {
            return fullName;
        }
        if (StringUtils.hasText(personLdap.getCn())) {
            return personLdap.getCn();
        }
        return personLdap.getUid();
    }

    private String cleanStatus(String status) {
        if (status != null && status.startsWith("org.esupportail.esupagape.entity.") && status.contains("@")) {
            return "";
        }
        return status;
    }
}
