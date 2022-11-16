package org.esupportail.esupagape.service.interfaces.dossierinfos.impl;

import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfos;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfosService;
import org.esupportail.esupagape.service.ldap.LdapPersonService;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Order(2)
public class LdapDossierInfosService implements DossierInfosService {

    private final LdapPersonService ldapPersonService;

    public LdapDossierInfosService(LdapPersonService ldapPersonService) {
        this.ldapPersonService = ldapPersonService;
    }

    @Override
    public DossierInfos getDossierProperties(Individu individu, Integer annee, boolean getAllSteps, DossierInfos dossierInfos) {
        List<PersonLdap> personLdaps = ldapPersonService.searchBySupannEtuId(individu.getNumEtu());
        if(personLdaps.size() > 0) {
            PersonLdap personLdap = personLdaps.get(0);
            //TODO recup du nom du lieu ldap
            dossierInfos.setEtablissement(personLdap.getSupannEtablissement());
        }
        return dossierInfos;
    }
}
