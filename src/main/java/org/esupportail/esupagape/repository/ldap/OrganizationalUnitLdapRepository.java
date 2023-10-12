package org.esupportail.esupagape.repository.ldap;

import org.esupportail.esupagape.service.ldap.OrganizationalUnitLdap;
import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationalUnitLdapRepository extends LdapRepository<OrganizationalUnitLdap> {
    List<OrganizationalUnitLdap> findBySupannRefIdAndSupannTypeEntite(String supannRefId, String supanntypeEntity);
    List<OrganizationalUnitLdap> findBySupannCodeEntite(String supannCodeEntite);
    List<OrganizationalUnitLdap> findBySupannRefId(String supannRefId);
}
