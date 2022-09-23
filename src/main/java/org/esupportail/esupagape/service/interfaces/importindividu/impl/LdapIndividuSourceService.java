package org.esupportail.esupagape.service.interfaces.importindividu.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuSourceService;
import org.esupportail.esupagape.service.ldap.LdapPersonService;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(value = "spring.ldap.base")
public class LdapIndividuSourceService implements IndividuSourceService {

    private static final Logger logger = LoggerFactory.getLogger(LdapIndividuSourceService.class);

    Map<String, String> sexMap  = new HashMap<>() {{
        put("MME", "F");
        put("M.", "M");
    }};

    @Resource
    private LdapPersonService ldapPersonService;

    @Override
    public Map<String, String> getSourcesPropertiesMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("eppn", "eduPersonPrincipalName");
        mapping.put("fixAddress", "postalAddress");
        mapping.put("emailEtu", "mail");
        mapping.put("emailPerso", "supannMailPerso");
        mapping.put("fixCP", "eduPersonPrincipalName");
        mapping.put("fixCity", "eduPersonPrincipalName");
        mapping.put("fixPhone", "telephoneNumber");
        return mapping;
    }

    @Override
    public Map<String, Object> getIndividuProperties(String numEtu) {
        Map<String, Object> properties;
        List<PersonLdap> personLdaps = ldapPersonService.searchBySupannEtuId(numEtu);
        ObjectMapper mapper = new ObjectMapper();
        properties = mapper.convertValue(personLdaps.get(0), new TypeReference<>() {});
        return properties;
    }

    @Override
    public Individu getIndividuByNumEtu(String numEtu) {
        List<PersonLdap> personLdaps = ldapPersonService.searchBySupannEtuId(numEtu);
        return getIndividuFromPersonLdap(personLdaps);
    }

    @Override
    public Individu getIndividuByProperties(String name, String firstName, LocalDate dateOfBirth) {
        List<PersonLdap> personLdaps = ldapPersonService.searchByProperties(name, firstName, dateOfBirth);
        return getIndividuFromPersonLdap(personLdaps);
    }

    private Individu getIndividuFromPersonLdap(List<PersonLdap> personLdaps) {
        if (!personLdaps.isEmpty()) {
            Individu individu = new Individu();
            individu.setNumEtu(personLdaps.get(0).getSupannEtuId());
            individu.setName(personLdaps.get(0).getSn());
            individu.setFirstName(personLdaps.get(0).getGivenName());
            individu.setSex(sexMap.get(personLdaps.get(0).getSupannCivilite().toUpperCase()));
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            individu.setDateOfBirth(LocalDate.parse(personLdaps.get(0).getSchacDateOfBirth(), dateTimeFormatter));
            return individu;
        }
        return null;
    }

    @Override
    public void updateIndividu(Individu individu) {
        List<PersonLdap> personLdaps = ldapPersonService.searchBySupannEtuId(individu.getNumEtu());
        if(personLdaps.size() > 0) {
            PersonLdap personLdap = personLdaps.get(0);
            individu.setEppn(personLdap.getEduPersonPrincipalName());
            String address = personLdap.getPostalAddress();
            if (address != null && !address.isEmpty()) {
                individu.setFixAddress(address.split("\\$")[0].trim());
                if(address.split("\\$")[1].split(" ").length > 0) {
                    individu.setFixCP(address.split("\\$")[1].split(" ")[0].trim());
                    individu.setFixCity(address.split("\\$")[1].replace(address.split("\\$")[1].split(" ")[0], "").trim());
                }
                individu.setFixCountry(address.split("\\$")[address.split("\\$").length - 1].trim());
            }
            individu.setSex(sexMap.get(personLdaps.get(0).getSupannCivilite().toUpperCase()));
            individu.setEmailEtu(personLdap.getMail());
            individu.setEmailPerso(personLdap.getSupannMailPerso());
            individu.setFixPhone(personLdap.getTelephoneNumber());
            individu.setContactPhone(personLdap.getSupannAutreTelephone());
        }
    }

    @Override
    public void updateDossier(Individu individu, int year) {

    }

    @Override
    public Map<String, String> getIndividuProperties(String name, String firstname, LocalDateTime dateOfBirth) {
        return null;
    }

    @Override
    public List<Individu> getAllIndividuNums() {
        return new ArrayList<>();
    }
}
