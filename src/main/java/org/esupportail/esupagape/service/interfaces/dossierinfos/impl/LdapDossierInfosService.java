package org.esupportail.esupagape.service.interfaces.dossierinfos.impl;

import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfos;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfosService;
import org.esupportail.esupagape.service.ldap.LdapOrganizationalUnitService;
import org.esupportail.esupagape.service.ldap.LdapPersonService;
import org.esupportail.esupagape.service.ldap.OrganizationalUnitLdap;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.esupportail.esupagape.service.utils.SiseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Order(2)
public class LdapDossierInfosService implements DossierInfosService {

    private static final Logger logger = LoggerFactory.getLogger(LdapDossierInfosService.class);

    private final LdapPersonService ldapPersonService;

    private final LdapOrganizationalUnitService ldapOrganizationalUnitService;

    private final SiseService siseService;

    private final ApplicationProperties applicationProperties;

    public LdapDossierInfosService(LdapPersonService ldapPersonService, LdapOrganizationalUnitService ldapOrganizationalUnitService, SiseService siseService, ApplicationProperties applicationProperties) {
        this.ldapPersonService = ldapPersonService;
        this.ldapOrganizationalUnitService = ldapOrganizationalUnitService;
        this.siseService = siseService;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public DossierInfos getDossierProperties(Individu individu, Integer annee, boolean getAllSteps, boolean getNotes, DossierInfos dossierInfos) {
        if(individu.getNumEtu() != null) {
            List<PersonLdap> personLdaps = ldapPersonService.searchBySupannEtuId(individu.getNumEtu());
            if (!personLdaps.isEmpty()) {
                PersonLdap personLdap = personLdaps.get(0);
                if(!personLdap.getMemberOf().isEmpty()) {
                    Pattern pattern = Pattern.compile("cn=adhoc\\.campus\\.([^.-]+)-");
                    List<String> campus = personLdap.getMemberOf().stream().filter(s -> s.contains(applicationProperties.getMemberOfCampusFilter())).toList();
                    if(!campus.isEmpty()) {
                        for (String campu : campus) {
                            Matcher matcher = pattern.matcher(campu);
                            if (matcher.find()) {
                                dossierInfos.getCampus().add(StringUtils.capitalize(matcher.group(1)));
                            }
                        }
                    }
                }
                try {
                    if(StringUtils.hasText(personLdap.getSupannEntiteAffectationPrincipale())) {
                        OrganizationalUnitLdap organizationalUnitLdap = ldapOrganizationalUnitService.getOrganizationalUnitLdap(personLdap.getSupannEntiteAffectationPrincipale());
                        dossierInfos.setCodComposante(organizationalUnitLdap.getSupannCodeEntite());
                        dossierInfos.setComposante(organizationalUnitLdap.getDescription());
                        dossierInfos.setFormAddress(organizationalUnitLdap.getPostalAddress());
                    }
                    OrganizationalUnitLdap organizationalUnitLdapEtab = ldapOrganizationalUnitService.getEtablissement(personLdap.getSupannEtablissement());
                    if(organizationalUnitLdapEtab != null) {
                        dossierInfos.setEtablissement(ldapOrganizationalUnitService.getEtablissement(personLdap.getSupannEtablissement()).getDescription());
                    }
                    if(StringUtils.hasText(personLdap.getSupannEtuDiplome())) {
                        String code = personLdap.getSupannEtuDiplome().substring(personLdap.getSupannEtuDiplome().lastIndexOf("}") + 1);
                        String libelleDiplome = siseService.getLibelleDiplome(code);
                        if(StringUtils.hasText(libelleDiplome)) {
                            dossierInfos.setLibelleFormation(libelleDiplome);
                        }
                    }
                    if(StringUtils.hasText(personLdap.getSupannEtuSecteurDisciplinaire())) {
                        String code = personLdap.getSupannEtuSecteurDisciplinaire().substring(personLdap.getSupannEtuSecteurDisciplinaire().length() - 2);
                        String libelleSecteur = siseService.getLibelleSecteurDisciplinaire(code);
                        if(StringUtils.hasText(libelleSecteur)) {
                            dossierInfos.setSecteurDisciplinaire(libelleSecteur);
                        }
                    }
                    if(StringUtils.hasText(personLdap.getSupannEtuTypeDiplome())) {
                        String code = personLdap.getSupannEtuTypeDiplome().substring(personLdap.getSupannEtuTypeDiplome().length() - 2);
                        String libelleTypeDiplome = siseService.getLibelleTypeDiplome(code);
                        if(StringUtils.hasText(libelleTypeDiplome)) {
                            dossierInfos.setTypeDiplome(libelleTypeDiplome);
                        }
                    }
                    if(personLdap.getSupannEtuCursusAnnee() != null && !personLdap.getSupannEtuCursusAnnee().isEmpty()) {
                        dossierInfos.setNiveauEtudes(personLdap.getSupannEtuCursusAnnee().get(0).substring(personLdap.getSupannEtuCursusAnnee().get(0).lastIndexOf("}") + 1));
                    }
                } catch (AgapeJpaException e) {
                    logger.info(e.getMessage(), e);
                }
            }
        }
        return dossierInfos;
    }

    @Override
    public Map<String, String> getCodComposanteLabels() {
        return Map.of();
    }
}
