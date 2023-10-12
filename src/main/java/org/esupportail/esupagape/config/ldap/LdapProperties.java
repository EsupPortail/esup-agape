package org.esupportail.esupagape.config.ldap;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "ldap")
public class LdapProperties {

    private String searchBase;
    private String listSearchBase;
    private String listSearchFilter;
    private String groupSearchBase;
    private String groupSearchFilter;
    private String allGroupsSearchFilter;
    private String groupNameAttribut;
    private String membersOfGroupSearchFilter;
    private String memberSearchFilter;
    private String userIdSearchFilter;
    private String affectationPrincipaleRefIdPrefixFromApo;
    private String affectationPrincipaleRefIdPrefixFromRh;
    private String scolariteMemberOfSearch;

    private Map<String, String> mappingFiltersGroups = new HashMap<>();

    public String getSearchBase() {
        return searchBase;
    }

    public void setSearchBase(String searchBase) {
        this.searchBase = searchBase;
    }

    public String getListSearchBase() {
        return listSearchBase;
    }

    public void setListSearchBase(String listSearchBase) {
        this.listSearchBase = listSearchBase;
    }

    public String getListSearchFilter() {
        return listSearchFilter;
    }

    public void setListSearchFilter(String listSearchFilter) {
        this.listSearchFilter = listSearchFilter;
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

    public String getAllGroupsSearchFilter() {
        return allGroupsSearchFilter;
    }

    public void setAllGroupsSearchFilter(String allGroupsSearchFilter) {
        this.allGroupsSearchFilter = allGroupsSearchFilter;
    }

    public String getGroupNameAttribut() {
        return groupNameAttribut;
    }

    public void setGroupNameAttribut(String groupNameAttribut) {
        this.groupNameAttribut = groupNameAttribut;
    }

    public String getMembersOfGroupSearchFilter() {
        return membersOfGroupSearchFilter;
    }

    public void setMembersOfGroupSearchFilter(String membersOfGroupSearchFilter) {
        this.membersOfGroupSearchFilter = membersOfGroupSearchFilter;
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
