package org.esupportail.esupagape.service.interfaces.dossierinfos.impl;

import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfos;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfosService;
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

@Service
@Order(2)
public class LdapDossierInfosService implements DossierInfosService {

    private static final Logger logger = LoggerFactory.getLogger(LdapDossierInfosService.class);

    private final LdapPersonService ldapPersonService;

    private final SiseService siseService;

    public LdapDossierInfosService(LdapPersonService ldapPersonService, SiseService siseService) {
        this.ldapPersonService = ldapPersonService;
        this.siseService = siseService;
    }

    @Override
    public DossierInfos getDossierProperties(Individu individu, Integer annee, boolean getAllSteps, boolean getNotes, DossierInfos dossierInfos) {
        if(individu.getNumEtu() != null) {
            List<PersonLdap> personLdaps = ldapPersonService.searchBySupannEtuId(individu.getNumEtu());
            if (personLdaps.size() > 0) {
                PersonLdap personLdap = personLdaps.get(0);
                try {
                    if(StringUtils.hasText(personLdap.getSupannEntiteAffectationPrincipale())) {
                        OrganizationalUnitLdap organizationalUnitLdap = ldapPersonService.getOrganizationalUnitLdap(personLdap.getSupannEntiteAffectationPrincipale());
                        dossierInfos.setCodComposante(organizationalUnitLdap.getSupannCodeEntite());
                        dossierInfos.setComposante(organizationalUnitLdap.getDescription());
                        dossierInfos.setFormAddress(organizationalUnitLdap.getPostalAddress());
                    }
                    OrganizationalUnitLdap organizationalUnitLdapEtab = ldapPersonService.getEtablissement(personLdap.getSupannEtablissement());
                    if(organizationalUnitLdapEtab != null) {
                        dossierInfos.setEtablissement(ldapPersonService.getEtablissement(personLdap.getSupannEtablissement()).getDescription());
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
                    if(personLdap.getSupannEtuCursusAnnee() != null && personLdap.getSupannEtuCursusAnnee().size() > 0) {
                        dossierInfos.setNiveauEtudes(personLdap.getSupannEtuCursusAnnee().get(0).substring(personLdap.getSupannEtuCursusAnnee().get(0).lastIndexOf("}") + 1));
                    }
                } catch (AgapeJpaException e) {
                    logger.info(e.getMessage(), e);
                }
            }
        }
        return dossierInfos;
    }
}
