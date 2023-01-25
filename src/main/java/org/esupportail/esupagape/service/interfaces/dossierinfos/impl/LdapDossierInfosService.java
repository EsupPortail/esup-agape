package org.esupportail.esupagape.service.interfaces.dossierinfos.impl;

import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfos;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfosService;
import org.esupportail.esupagape.service.ldap.LdapPersonService;
import org.esupportail.esupagape.service.ldap.OrganizationalUnitLdap;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Order(1)
public class LdapDossierInfosService implements DossierInfosService {

    private static final Logger logger = LoggerFactory.getLogger(LdapDossierInfosService.class);

    private final LdapPersonService ldapPersonService;

    public LdapDossierInfosService(LdapPersonService ldapPersonService) {
        this.ldapPersonService = ldapPersonService;
    }

    @Override
    public DossierInfos getDossierProperties(Individu individu, Integer annee, boolean getAllSteps, boolean getNotes, DossierInfos dossierInfos) {
        if(individu.getNumEtu() != null) {
            List<PersonLdap> personLdaps = ldapPersonService.searchBySupannEtuId(individu.getNumEtu());
            if (personLdaps.size() > 0) {
                PersonLdap personLdap = personLdaps.get(0);
                try {
                    OrganizationalUnitLdap organizationalUnitLdap = ldapPersonService.getOrganizationalUnitLdap(personLdap.getSupannEntiteAffectationPrincipale());
                    dossierInfos.setCodComposante(organizationalUnitLdap.getSupannCodeEntite());
                    dossierInfos.setComposante(organizationalUnitLdap.getDescription());
                    dossierInfos.setFormAddress(organizationalUnitLdap.getPostalAddress());
                    dossierInfos.setEtablissement(ldapPersonService.getEtablissement(personLdap.getSupannEtablissement()).getDescription());
                } catch (AgapeJpaException e) {
                    logger.debug(e.getMessage());
                }
            }
        }
        return dossierInfos;
    }
}
