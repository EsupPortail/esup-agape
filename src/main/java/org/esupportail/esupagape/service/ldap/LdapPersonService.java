package org.esupportail.esupagape.service.ldap;

import org.esupportail.esupagape.config.ldap.LdapProperties;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.repository.ldap.PersonLdapRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.naming.directory.SearchControls;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@ConditionalOnProperty({"spring.ldap.base"})
@EnableConfigurationProperties(LdapProperties.class)
public class LdapPersonService {

    @Resource
    private LdapProperties ldapProperties;

    @Resource
    private LdapTemplate ldapTemplate;

    @Resource
    private PersonLdapRepository personLdapRepository;

    public List<PersonLdap> search(String searchString) {
        return personLdapRepository.findByDisplayNameStartingWithIgnoreCaseOrCnStartingWithIgnoreCaseOrUidStartingWithOrMailStartingWith(searchString, searchString, searchString, searchString);
    }

    public List<PersonLdap> searchBySupannEtuId(String numEtu) {
        return personLdapRepository.findBySupannEtuId(numEtu);
    }

    public String getPersonLdapAttribute(String authName, String attribute) throws AgapeException {
        if(attribute != null) {
            String formattedFilter = MessageFormat.format("(uid={0})", (Object[]) new String[]{authName});
            return ldapTemplate.search(ldapProperties.getSearchBase(),
                    formattedFilter,
                    SearchControls.SUBTREE_SCOPE,
                    new String[]{attribute},
                    (AttributesMapper) attrs -> attrs.get(attribute).get().toString()).get(0).toString();
        }else {
            throw new AgapeException("Attribut is null");
        }
    }

    public List<PersonLdap> searchByProperties(String name, String firstName, LocalDate dateOfBirth) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateOfBirthString = dateOfBirth.format(dateTimeFormatter);
        return personLdapRepository.findBySnAndGivenNameAndSchacDateOfBirth(name, firstName, dateOfBirthString);
    }
}