package org.esupportail.esupagape.repository.ldap;

import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonLdapRepository extends LdapRepository<PersonLdap> {
    List<PersonLdap> findByEduPersonPrincipalName(String eppn);
    List<PersonLdap> findByMemberOf(String group);
    List<PersonLdap> findByUid(String uid);
    List<PersonLdap> findByMail(String mail);
    List<PersonLdap> findBySupannEtuId(String numEtu);
    List<PersonLdap> findBySupannEmpId(String numEtuAidant);
    List<PersonLdap> findBySupannCodeINE(String codeIne);
    List<PersonLdap> findBySnAndGivenNameAndSchacDateOfBirth(String name, String firstName, String dateOfBirth);
    List<PersonLdap> findByDisplayNameStartingWithIgnoreCaseOrCnStartingWithIgnoreCaseOrUidStartingWithOrMailStartingWith(String displayName, String cn, String uid, String mail);

//    List<PersonLdap> findBySupannEtuIdOrCnStartingWithIgnoreCase(String supannEtuId, String cn);
}

