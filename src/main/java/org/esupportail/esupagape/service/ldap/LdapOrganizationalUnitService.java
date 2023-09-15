package org.esupportail.esupagape.service.ldap;

import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.repository.ldap.OrganizationalUnitLdapRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LdapOrganizationalUnitService {

    private final OrganizationalUnitLdapRepository organizationalUnitLdapRepository;

    public LdapOrganizationalUnitService(OrganizationalUnitLdapRepository organizationalUnitLdapRepository) {
        this.organizationalUnitLdapRepository = organizationalUnitLdapRepository;
    }

    public OrganizationalUnitLdap getOrganizationalUnitLdap(String supannCodeEntite) throws AgapeJpaException {
        List<OrganizationalUnitLdap> organizationalUnitLdaps = organizationalUnitLdapRepository.findBySupannCodeEntite(supannCodeEntite);
        if(organizationalUnitLdaps.size() > 0) {
            return organizationalUnitLdaps.get(0);
        } else {
            throw new AgapeJpaException(supannCodeEntite + " not fount in OU");
        }
    }

    public OrganizationalUnitLdap getEtablissement(String supannEtablissement) {
        List<OrganizationalUnitLdap> organizationalUnitLdaps = organizationalUnitLdapRepository.findBySupannRefIdAndSupannTypeEntite(supannEtablissement, "Etablissement");
        if(organizationalUnitLdaps.size() > 0) {
            return organizationalUnitLdaps.get(0);
        } else {
            return null;
        }
    }

}
