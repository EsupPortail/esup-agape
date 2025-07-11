package org.esupportail.esupagape.service.interfaces.importindividu.impl;

import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.DataType;
import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.service.DataMappingService;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuInfos;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuSourceService;
import org.esupportail.esupagape.service.ldap.LdapPersonService;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Order(1)
@ConditionalOnProperty(value = "spring.ldap.base")
public class LdapIndividuSourceService implements IndividuSourceService {

    private static final Logger logger = LoggerFactory.getLogger(LdapIndividuSourceService.class);


    private final ApplicationProperties applicationProperties;

    private final LdapPersonService ldapPersonService;
    private final DataMappingService dataMappingService;

    public LdapIndividuSourceService(ApplicationProperties applicationProperties, LdapPersonService ldapPersonService, DataMappingService dataMappingService) {
        this.applicationProperties = applicationProperties;
        this.ldapPersonService = ldapPersonService;
        this.dataMappingService = dataMappingService;
    }

    @Override
    public IndividuInfos getIndividuProperties(String numEtu, IndividuInfos individuInfos, int annee) {
        List<PersonLdap> personLdaps = ldapPersonService.searchBySupannEtuId(numEtu);
        if(!personLdaps.isEmpty()) {
            PersonLdap personLdap = personLdaps.get(0);
            individuInfos.setEppn(personLdap.getEduPersonPrincipalName());
            String address = personLdap.getPostalAddress();
            if (StringUtils.hasText(address)) {
                individuInfos.setFixAddress(address.split("\\$")[0].trim());
                if(address.split("\\$")[1].split(" ").length > 0) {
                    individuInfos.setFixCP(address.split("\\$")[1].split(" ")[0].trim());
                    individuInfos.setFixCity(address.split("\\$")[1].replace(address.split("\\$")[1].split(" ")[0], "").trim());
                }
                individuInfos.setFixCountry(address.split("\\$")[address.split("\\$").length - 1].trim());
            }
            individuInfos.setCodeIne(personLdaps.get(0).getSupannCodeINE());
            individuInfos.setName(personLdap.getSn());
            individuInfos.setFirstName(personLdap.getGivenName());
            individuInfos.setGenre(dataMappingService.getValue("Individu", "genre", DataType.ldap, DataType.agape, personLdap.getSupannCivilite().toUpperCase()));
            individuInfos.setEmailEtu(personLdap.getMail());
            individuInfos.setFixPhone(personLdap.getTelephoneNumber());
            individuInfos.setContactPhone(personLdap.getSupannAutreTelephone());
            individuInfos.setDateOfBirth(LocalDate.parse(personLdap.getSchacDateOfBirth(), DateTimeFormatter.ofPattern("yyyyMMdd")));
            individuInfos.setAffectation(personLdap.getSupannEntiteAffectationPrincipale());
            individuInfos.setYear(personLdap.getSupannEtuAnneeInscription().stream().max(Comparator.comparing(Integer::valueOf)).orElse(null));
            try {
                individuInfos.setPhotoId(ldapPersonService.getPersonLdapAttribute(personLdap.getUid(), applicationProperties.getMappingPhotoIdToLdapField()));
            } catch (AgapeException e) {
                logger.debug(e.getMessage());
            }
        }
        return individuInfos;

    }

    @Override
    public Individu getIndividuByNumEtu(String numEtu) {
        List<PersonLdap> personLdaps = ldapPersonService.searchBySupannEtuId(numEtu);
        return getIndividuFromPersonLdap(personLdaps);
    }

    @Override
    public Individu getIndividuByCodeIne(String codeIne) {
        List<PersonLdap> personLdaps = ldapPersonService.searchBySupannCodeINE(codeIne);
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
            individu.setCodeIne(personLdaps.get(0).getSupannCodeINE());
            individu.setName(personLdaps.get(0).getSn());
            individu.setFirstName(personLdaps.get(0).getGivenName());
            individu.setSex(dataMappingService.getValue("Individu", "genre", DataType.ldap, DataType.agape, personLdaps.get(0).getSupannCivilite().toUpperCase()));
            individu.setGender(Gender.valueOf(dataMappingService.getValue("Individu", "genre", DataType.ldap, DataType.agape, personLdaps.get(0).getSupannCivilite().toUpperCase())));
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            individu.setDateOfBirth(LocalDate.parse(personLdaps.get(0).getSchacDateOfBirth(), dateTimeFormatter));
            return individu;
        }
        return null;
    }

    @Override
    public List<Individu> getAllIndividuNums() {
        return new ArrayList<>();
    }

}
