package org.esupportail.esupagape.config.ldap;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "ldap")
public class LdapProperties {

    /**
     * LDAP base de recherche des utilisateurs.
     */
    private String searchBase = "ou=people";
    /**
     * LDAP base de recherche des groupes.
     */
    private String groupSearchBase = "ou=groups";
    /**
     * LDAP filtre de recherche des groupes d’un utilisateur.
     */
    private String groupSearchFilter = "member={0}";
    /**
     * LDAP filtre de recherche des membres d’un groupe.
     */
    private String memberSearchFilter = "(&(uid={0})({1}))";
    /**
     * LDAP attribut de recherche des utilisateurs.
     */
    private String userIdSearchFilter = "(uid={0})";
    /**
     * LDAP prefix de l’attribut affectationPrincipale supannRefId pour apogee.
     */
    private String affectationPrincipaleRefIdPrefixFromApo;
    /**
     * LDAP prefix de l’attribut affectationPrincipale supannRefId pour l’application rh.
     */
    private String affectationPrincipaleRefIdPrefixFromRh;
    /**
     * LDAP filtre de recherche des membres du groupe scolarité.
     */
    private String scolariteMemberOfSearch;
    /**
     * LDAP filtre pour la création de groupes dynamiques
     */
    private Map<String, String> mappingFiltersGroups = new HashMap<>();

    public String getSearchBase() {
        return searchBase;
    }

    public void setSearchBase(String searchBase) {
        this.searchBase = searchBase;
    }


    public String getGroupSearchBase() {
        return groupSearchBase;
    }

    public void setGroupSearchBase(String groupSearchBase) {
        this.groupSearchBase = groupSearchBase;
    }

    public String getGroupSearchFilter() {
        return groupSearchFilter;
    }

    public void setGroupSearchFilter(String groupSearchFilter) {
        this.groupSearchFilter = groupSearchFilter;
    }

    public String getMemberSearchFilter() {
        return memberSearchFilter;
    }

    public void setMemberSearchFilter(String memberSearchFilter) {
        this.memberSearchFilter = memberSearchFilter;
    }

    public String getUserIdSearchFilter() {
        return userIdSearchFilter;
    }

    public void setUserIdSearchFilter(String userIdSearchFilter) {
        this.userIdSearchFilter = userIdSearchFilter;
    }

    public Map<String, String> getMappingFiltersGroups() {
        return mappingFiltersGroups;
    }

    public void setMappingFiltersGroups(Map<String, String> mappingFiltersGroups) {
        this.mappingFiltersGroups = mappingFiltersGroups;
    }

    public String getAffectationPrincipaleRefIdPrefixFromApo() {
        return affectationPrincipaleRefIdPrefixFromApo;
    }

    public void setAffectationPrincipaleRefIdPrefixFromApo(String affectationPrincipaleRefIdPrefixFromApo) {
        this.affectationPrincipaleRefIdPrefixFromApo = affectationPrincipaleRefIdPrefixFromApo;
    }

    public String getAffectationPrincipaleRefIdPrefixFromRh() {
        return affectationPrincipaleRefIdPrefixFromRh;
    }

    public void setAffectationPrincipaleRefIdPrefixFromRh(String affectationPrincipaleRefIdPrefixFromRh) {
        this.affectationPrincipaleRefIdPrefixFromRh = affectationPrincipaleRefIdPrefixFromRh;
    }

    public String getScolariteMemberOfSearch() {
        return scolariteMemberOfSearch;
    }

    public void setScolariteMemberOfSearch(String scolariteMemberOfSearch) {
        this.scolariteMemberOfSearch = scolariteMemberOfSearch;
    }
}
