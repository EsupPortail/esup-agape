package org.esupportail.esupagape.service.ldap;

import org.esupportail.esupagape.config.ldap.LdapProperties;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.repository.ldap.OrganizationalUnitLdapRepository;
import org.esupportail.esupagape.repository.ldap.PersonLdapRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchControls;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@ConditionalOnProperty({"spring.ldap.base"})
@EnableConfigurationProperties(LdapProperties.class)
public class LdapPersonService {

    private final LdapProperties ldapProperties;

    private final LdapTemplate ldapTemplate;

    private final PersonLdapRepository personLdapRepository;

    private final OrganizationalUnitLdapRepository organizationalUnitLdapRepository;

    public LdapPersonService(LdapProperties ldapProperties, LdapTemplate ldapTemplate, PersonLdapRepository personLdapRepository, OrganizationalUnitLdapRepository organizationalUnitLdapRepository) {
        this.ldapProperties = ldapProperties;
        this.ldapTemplate = ldapTemplate;
        this.personLdapRepository = personLdapRepository;
        this.organizationalUnitLdapRepository = organizationalUnitLdapRepository;
    }

    public List<PersonLdap> search(String searchString) {
        return personLdapRepository.findByDisplayNameStartingWithIgnoreCaseOrCnStartingWithIgnoreCaseOrUidStartingWithOrMailStartingWith(searchString, searchString, searchString, searchString);
    }

    public List<PersonLdap> searchBySupannEtuId(String numEtu) {
        return personLdapRepository.findBySupannEtuId(numEtu);
    }

    public String getPersonLdapAttribute(String authName, String attribute) throws AgapeException {
        if(attribute != null) {
            String formattedFilter = MessageFormat.format(ldapProperties.getUserIdSearchFilter(), (Object[]) new String[]{authName});
            return ldapTemplate.search(ldapProperties.getSearchBase(),
                    formattedFilter,
                    SearchControls.SUBTREE_SCOPE,
                    new String[]{attribute},
                    (AttributesMapper) attrs -> attrs.get(attribute).get().toString()).get(0).toString();
        }else {
            throw new AgapeException("Attribut is null");
        }
    }

    public PersonLdap getPersonLdap(String userName) {
        String formattedFilter = MessageFormat.format(ldapProperties.getUserIdSearchFilter(), (Object[]) new String[] { userName });
        List<PersonLdap> personLdaps = ldapTemplate.search(ldapProperties.getSearchBase(), formattedFilter, new PersonLdapAttributesMapper());
        PersonLdap personLdap = null;
        if(personLdaps.size() > 0) {
            personLdap = personLdaps.get(0);
        }
        return personLdap;
    }

    public List<PersonLdap> searchByProperties(String codeIne, String name, String firstName, LocalDate dateOfBirth) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateOfBirthString = dateOfBirth.format(dateTimeFormatter);
        return personLdapRepository.findBySnAndGivenNameAndSchacDateOfBirth(codeIne, name, firstName, dateOfBirthString);
    }

    public OrganizationalUnitLdap getOrganizationalUnitLdap(String supannCodeEntite) throws AgapeJpaException {
        List<OrganizationalUnitLdap> organizationalUnitLdaps = organizationalUnitLdapRepository.findBySupannCodeEntite(supannCodeEntite);
        if(organizationalUnitLdaps.size() > 0) {
            return organizationalUnitLdaps.get(0);
        } else {
            throw new AgapeJpaException(supannCodeEntite + " not fount in OU");
        }
    }

    public OrganizationalUnitLdap getEtablissement(String supannEtablissement) throws AgapeJpaException {
        List<OrganizationalUnitLdap> organizationalUnitLdaps = organizationalUnitLdapRepository.findBySupannRefIdAndSupannTypeEntite(supannEtablissement, "Etablissement");
        if(organizationalUnitLdaps.size() > 0) {
            return organizationalUnitLdaps.get(0);
        } else {
            throw new AgapeJpaException(supannEtablissement + " not fount in OU");
        }
    }
}