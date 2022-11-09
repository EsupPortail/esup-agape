package org.esupportail.esupagape.service.interfaces.dossierinfos.impl;

import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfosService;
import org.esupportail.esupagape.service.ldap.LdapPersonService;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Order(2)
public class LdapDossierInfosService implements DossierInfosService {

    private final LdapPersonService ldapPersonService;

    public LdapDossierInfosService(LdapPersonService ldapPersonService) {
        this.ldapPersonService = ldapPersonService;
    }

    @Override
    public List<Map<String, Object>> getDossierProperties(Individu individu, Integer annee, boolean getAllSteps) {
        List<PersonLdap> personLdaps = ldapPersonService.searchBySupannEtuId(individu.getNumEtu());
        List<Map<String, Object>> dossierProperties = new ArrayList<>();
        if(personLdaps.size() > 0) {
            PersonLdap personLdap = personLdaps.get(0);
            Map<String, Object> dossierDatas = new HashMap<>();
            //TODO recup du nom du lieu ldap
            dossierDatas.put("etablissement", personLdap.getSupannEtablissement());
            dossierProperties.add(dossierDatas);
        }
        return dossierProperties;
    }
}
