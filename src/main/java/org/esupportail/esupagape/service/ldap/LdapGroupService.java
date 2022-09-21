package org.esupportail.esupagape.service.ldap;

import org.esupportail.esupagape.service.security.GroupService;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.text.MessageFormat;
import java.util.*;

public class LdapGroupService implements GroupService {

    Map<String, String> ldapFiltersGroups;

    private LdapTemplate ldapTemplate;

    private String groupSearchBase;

    private String groupSearchFilter;

    private String memberSearchFilter;

    public void setLdapFiltersGroups(Map<String, String> ldapFiltersGroups) {
        this.ldapFiltersGroups = ldapFiltersGroups;
    }

    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    public void setGroupSearchBase(String groupSearchBase) {
        this.groupSearchBase = groupSearchBase;
    }

    public void setGroupSearchFilter(String groupSearchFilter) {
        this.groupSearchFilter = groupSearchFilter;
    }

    public void setMemberSearchFilter(String memberSearchFilter) {
        this.memberSearchFilter = memberSearchFilter;
    }

    @Override
    public List<String> getGroups(String eppn) {
        String username = eppn.replaceAll("@.*", "");
        List<String> dns = ldapTemplate.search(LdapQueryBuilder.query().attributes("dn").where("uid").is(username),
                (ContextMapper<String>) ctx -> {
                    DirContextAdapter searchResultContext = (DirContextAdapter) ctx;
                    return searchResultContext.getNameInNamespace();
                });
        List<String> groups = new ArrayList<>();
        if(!dns.isEmpty()) {
            String userDn = dns.get(0);
            String formattedGroupSearchFilter = MessageFormat.format(groupSearchFilter, userDn, username);
            LdapQuery groupSearchQuery = LdapQueryBuilder.query().attributes("cn").base(groupSearchBase).filter(formattedGroupSearchFilter);
            groups = ldapTemplate.search(groupSearchQuery, (ContextMapper<String>) ctx -> {
                        DirContextAdapter searchResultContext = (DirContextAdapter) ctx;
                        return searchResultContext.getStringAttribute("cn");
                    });
        }
        for(String ldapFilter: ldapFiltersGroups.keySet()) {
            String hardcodedFilter = MessageFormat.format(memberSearchFilter, username, ldapFilter);
            List<String> filterDns = ldapTemplate.search(LdapQueryBuilder.query().attributes("dn").filter(hardcodedFilter),
                    (ContextMapper<String>) ctx -> {
                        DirContextAdapter searchResultContext = (DirContextAdapter) ctx;
                        return searchResultContext.getNameInNamespace();
                    });

            if(!filterDns.isEmpty()) {
                groups.add(ldapFiltersGroups.get(ldapFilter));
            }
        }
        return groups;
    }

    public void addLdapRoles(Set<GrantedAuthority> grantedAuthorities, List<String> ldapGroups, Map<String, String> mappingGroupesRoles) {
        for(String groupName : ldapGroups) {
            if(groupName != null) {
                if (mappingGroupesRoles != null && mappingGroupesRoles.containsKey(groupName)) {
                    grantedAuthorities.add(new SimpleGrantedAuthority(mappingGroupesRoles.get(groupName)));
                }
            }
        }
    }

}
