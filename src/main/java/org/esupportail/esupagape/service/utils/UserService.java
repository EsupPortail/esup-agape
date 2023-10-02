package org.esupportail.esupagape.service.utils;

import org.esupportail.esupagape.config.ldap.LdapProperties;
import org.esupportail.esupagape.service.ldap.LdapOrganizationalUnitService;
import org.esupportail.esupagape.service.ldap.LdapPersonService;
import org.esupportail.esupagape.service.ldap.OrganizationalUnitLdap;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final LdapPersonService ldapPersonService;

    private final LdapOrganizationalUnitService ldapOrganizationalUnitService;

    private final LdapProperties ldapProperties;

    public UserService(LdapPersonService ldapPersonService, LdapOrganizationalUnitService ldapOrganizationalUnitService, LdapProperties ldapProperties) {
        this.ldapPersonService = ldapPersonService;
        this.ldapOrganizationalUnitService = ldapOrganizationalUnitService;
        this.ldapProperties = ldapProperties;
    }

    public String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public PersonLdap getPersonLdap() {
        return ldapPersonService.getPersonLdap(getUserName());
    }

    public String getComposanteLibelle(PersonLdap personLdap) {
        OrganizationalUnitLdap organizationalUnitLdap = ldapOrganizationalUnitService.getOrganizationalUnitLdap(personLdap.getSupannEntiteAffectationPrincipale());
        if(organizationalUnitLdap != null) {
            return organizationalUnitLdap.getDescription();
        }
        return "";
    }

    public String getComposante(PersonLdap personLdap) {
        OrganizationalUnitLdap organizationalUnitLdap = ldapOrganizationalUnitService.getOrganizationalUnitLdap(personLdap.getSupannEntiteAffectationPrincipale());
        List<String> codComposantes = organizationalUnitLdap.getSupannRefId().stream().filter(s -> s.toUpperCase().startsWith(ldapProperties.getAffectationPrincipaleRefIdPrefix())).toList();
        if (!codComposantes.isEmpty()) {
            return codComposantes.get(0).split("}")[1];
        }
        return null;
    }
}
