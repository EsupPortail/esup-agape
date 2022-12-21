package org.esupportail.esupagape.service.utils;

import org.esupportail.esupagape.config.ldap.LdapProperties;
import org.esupportail.esupagape.service.ldap.LdapPersonService;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final LdapPersonService ldapPersonService;

    private final LdapProperties ldapProperties;

    public UserService(LdapPersonService ldapPersonService, LdapProperties ldapProperties) {
        this.ldapPersonService = ldapPersonService;
        this.ldapProperties = ldapProperties;
    }

    public String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public PersonLdap getPersonLdap() {
        return ldapPersonService.getPersonLdap(getUserName());
    }

}
