package org.esupportail.esupagape.service.interfaces.importindividu.impl;

import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuSourceService;
import org.esupportail.esupagape.service.ldap.LdapPersonService;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
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
@Order(1)
@ConditionalOnProperty(value = "spring.ldap.base")
public class LdapIndividuSourceService implements IndividuSourceService {

    private static final Logger logger = LoggerFactory.getLogger(LdapIndividuSourceService.class);

    Map<String, String> sexMap  = new HashMap<>() {{
        put("MME", "F");
        put("M.", "M");
    }};

    @Resource
    private ApplicationProperties applicationProperties;

    @Resource
    private LdapPersonService ldapPersonService;

    @Override
    public Map<String, Object> getIndividuProperties(String numEtu) {
        List<PersonLdap> personLdaps = ldapPersonService.searchBySupannEtuId(numEtu);
        if(personLdaps.size() > 0) {
            PersonLdap personLdap = personLdaps.get(0);
            Map<String, Object> individuDatas = new HashMap<>();
            individuDatas.put("eppn", personLdap.getEduPersonPrincipalName());
            String address = personLdap.getPostalAddress();
            if (address != null && !address.isEmpty()) {
                individuDatas.put("fixAddress", address.split("\\$")[0].trim());
                if(address.split("\\$")[1].split(" ").length > 0) {
                    individuDatas.put("fixCP", address.split("\\$")[1].split(" ")[0].trim());
                    individuDatas.put("fixCity", address.split("\\$")[1].replace(address.split("\\$")[1].split(" ")[0], "").trim());
                }
                individuDatas.put("fixCountry", address.split("\\$")[address.split("\\$").length - 1].trim());
            }
            individuDatas.put("sex", sexMap.get(personLdaps.get(0).getSupannCivilite().toUpperCase()));
            individuDatas.put("emailEtu", personLdap.getMail());
            individuDatas.put("emailPerso", personLdap.getSupannMailPerso());
            individuDatas.put("fixPhone", personLdap.getTelephoneNumber());
            individuDatas.put("contactPhone", personLdap.getSupannAutreTelephone());
            individuDatas.put("photoId", ldapPersonService.getPersonLdapAttribute(personLdap.getUid(), applicationProperties.getMappingPhotoIdToLdapField()));
            return individuDatas;
        }
        return null;
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
    public Map<String, String> getIndividuProperties(String name, String firstname, LocalDateTime dateOfBirth) {
        return null;
    }

    @Override
    public List<Individu> getAllIndividuNums() {
        return new ArrayList<>();
    }
}
