package org.esupportail.esupagape.service.ldap;

import org.esupportail.esupagape.config.ldap.LdapProperties;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.repository.ldap.PersonLdapRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
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

    public LdapPersonService(LdapProperties ldapProperties, LdapTemplate ldapTemplate, PersonLdapRepository personLdapRepository) {
        this.ldapProperties = ldapProperties;
        this.ldapTemplate = ldapTemplate;
        this.personLdapRepository = personLdapRepository;
    }

    public List<PersonLdap> search(String searchString) {
        return personLdapRepository.findByDisplayNameStartingWithIgnoreCaseOrCnStartingWithIgnoreCaseOrUidStartingWithOrMailStartingWith(searchString, searchString, searchString, searchString);
    }

    public List<PersonLdap> searchBySupannEtuId(String numEtu) {
        return personLdapRepository.findBySupannEtuId(numEtu);
    }

    public List<PersonLdap> searchBySupannCodeINE(String codeIne) {
        return personLdapRepository.findBySupannCodeINE(codeIne);

    }

    public String getPersonLdapAttribute(String authName, String attribute) throws AgapeException {
        if (attribute != null) {
            String formattedFilter = MessageFormat.format(ldapProperties.getUserIdSearchFilter(), (Object[]) new String[]{authName});
            return ldapTemplate.search(ldapProperties.getSearchBase(),
                    formattedFilter,
                    SearchControls.SUBTREE_SCOPE,
                    new String[]{attribute},
                    (AttributesMapper) attrs -> attrs.get(attribute).get().toString()).get(0).toString();
        } else {
            throw new AgapeException("Attribut is null");
        }
    }

    public PersonLdap getPersonLdap(String userName) {
        String formattedFilter = MessageFormat.format(ldapProperties.getUserIdSearchFilter(), (Object[]) new String[]{userName});
        List<PersonLdap> personLdaps = ldapTemplate.search(ldapProperties.getSearchBase(), formattedFilter, new PersonLdapAttributesMapper());
        PersonLdap personLdap = null;
        if (personLdaps.size() > 0) {
            personLdap = personLdaps.get(0);
        }
        return personLdap;
    }

    public List<PersonLdap> searchByProperties(String name, String firstName, LocalDate dateOfBirth) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateOfBirthString = dateOfBirth.format(dateTimeFormatter);
        return personLdapRepository.findBySnAndGivenNameAndSchacDateOfBirth(name, firstName, dateOfBirthString);
    }

//    public List<PersonLdap> searchBySupannEtuIdOrCn(String numEtu, String name) {
//        return personLdapRepository.findBySupannEtuIdOrCnStartingWithIgnoreCase(numEtu, name);
//    }

    public List<PersonLdap> findStudents(String search) {
        AndFilter andFilter = new AndFilter();
        andFilter.and(new EqualsFilter("eduPersonAffiliation", "student"));
        andFilter.and(new OrFilter()
                .or(new LikeFilter("cn", search + "*"))
                .or(new LikeFilter("supannEtuId", search + "*"))
                .or(new LikeFilter("supannCodeINE", search + "*")));

        LdapQuery query = LdapQueryBuilder.query()
                .searchScope(SearchScope.ONELEVEL)
                .base(ldapProperties.getSearchBase())
                .countLimit(20)
                .filter(andFilter);

        return personLdapRepository.findAll(query);

    }

    public List<PersonLdap> findEmployees(String search) {
        AndFilter andFilter = new AndFilter();
        andFilter.and(new OrFilter()
                .or(new EqualsFilter("eduPersonAffiliation", "employee"))
                .or(new EqualsFilter("eduPersonAffiliation", "member")));
        andFilter.and(new OrFilter()
                .or(new LikeFilter("cn", search + "*"))
                .or(new LikeFilter("uid", search + "*")));

        LdapQuery query = LdapQueryBuilder.query()
                .searchScope(SearchScope.ONELEVEL)
                .base(ldapProperties.getSearchBase())
                .countLimit(20)
                .filter(andFilter);
        return personLdapRepository.findAll(query);
    }

}