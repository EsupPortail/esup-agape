package org.esupportail.esupagape.config.security;

import org.esupportail.esupagape.config.ldap.LdapProperties;
import org.esupportail.esupagape.service.ldap.LdapGroupService;
import org.esupportail.esupagape.service.security.CasLdapAuthoritiesPopulator;
import org.esupportail.esupagape.service.security.Group2UserRoleService;
import org.esupportail.esupagape.service.security.SpelGroupService;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({LdapProperties.class, CasProperties.class, WebSecurityProperties.class})
public class WebSecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    private final LdapProperties ldapProperties;

    private final CasProperties casProperties;

    private final WebSecurityProperties webSecurityProperties;

    public WebSecurityConfig(LdapProperties ldapProperties, CasProperties casProperties, WebSecurityProperties webSecurityProperties) {
        this.ldapProperties = ldapProperties;
        this.casProperties = casProperties;
        this.webSecurityProperties = webSecurityProperties;
    }

    @Resource
    private LdapContextSource ldapContextSource;

    @Resource
    private SpelGroupService spelGroupService;

    @Resource
    private LdapTemplate ldapTemplate;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/").authenticated()
                .and().exceptionHandling()
                .authenticationEntryPoint(getAuthenticationEntryPoint());
        return http.build();
    }

    public LdapUserDetailsService ldapUserDetailsService() {
        LdapUserSearch ldapUserSearch = new FilterBasedLdapUserSearch(ldapProperties.getSearchBase(), ldapProperties.getUserIdSearchFilter(), ldapContextSource);
        CasLdapAuthoritiesPopulator casLdapAuthoritiesPopulator = new CasLdapAuthoritiesPopulator(ldapContextSource, ldapProperties.getGroupSearchBase());
        casLdapAuthoritiesPopulator.setRolePrefix("");
        casLdapAuthoritiesPopulator.setMappingGroupesRoles(webSecurityProperties.getMappingGroupsRoles());
        casLdapAuthoritiesPopulator.setLdapGroupService(ldapGroupService());
        Group2UserRoleService group2UserRoleService = new Group2UserRoleService();
        group2UserRoleService.setMappingGroupesRoles(webSecurityProperties.getMappingGroupsRoles());
        group2UserRoleService.setGroupService(spelGroupService);
        casLdapAuthoritiesPopulator.setGroup2UserRoleService(group2UserRoleService);
        LdapUserDetailsService ldapUserDetailsService = new LdapUserDetailsService(ldapUserSearch, casLdapAuthoritiesPopulator);
        LdapUserDetailsMapper ldapUserDetailsMapper = new LdapUserDetailsMapper();
        ldapUserDetailsMapper.setRoleAttributes(new String[] {});
        ldapUserDetailsService.setUserDetailsMapper(ldapUserDetailsMapper);
        return ldapUserDetailsService;
    }

    public UserDetailsByNameServiceWrapper<CasAssertionAuthenticationToken> casAuthUserDetailsService() {
        UserDetailsByNameServiceWrapper<CasAssertionAuthenticationToken> byNameServiceWrapper = new UserDetailsByNameServiceWrapper<>();
        byNameServiceWrapper.setUserDetailsService(ldapUserDetailsService());
        return byNameServiceWrapper;
    }

    @Bean
    public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
        return new Cas20ServiceTicketValidator(casProperties.getUrl());
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider authenticationProvider = new CasAuthenticationProvider();
        authenticationProvider.setAuthenticationUserDetailsService(casAuthUserDetailsService());
        authenticationProvider.setServiceProperties(serviceProperties());
        authenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
        authenticationProvider.setKey("EsupAgapeCAS");
        return authenticationProvider;
    }

    @Bean
    protected AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(casAuthenticationProvider()));
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter (AuthenticationManager authenticationManager, ServiceProperties serviceProperties) {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setServiceProperties(serviceProperties);
        return filter;
    }

    @Bean
    public ServiceProperties serviceProperties() {
        logger.info("service properties");
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(casProperties.getService());
        serviceProperties.setSendRenew(false);
        return serviceProperties;
    }

    @Bean
    public CasAuthenticationEntryPoint getAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint authenticationEntryPoint = new CasAuthenticationEntryPoint();
        authenticationEntryPoint.setLoginUrl(casProperties.getUrl() + "/login");
        authenticationEntryPoint.setServiceProperties(serviceProperties());
        return authenticationEntryPoint;
    }

    @Bean
    public SecurityContextLogoutHandler securityContextLogoutHandler() {
        return new SecurityContextLogoutHandler();
    }

    @Bean
    public LogoutFilter logoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(casProperties.getUrl() + "/logout",
                securityContextLogoutHandler());
        logoutFilter.setFilterProcessesUrl("/logout/cas");
        return logoutFilter;
    }

    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
                singleSignOutFilter.setLogoutCallbackPath("/");
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        return singleSignOutFilter;
    }

    @Bean
    public LdapGroupService ldapGroupService() {
        Map<String, String> ldapFiltersGroups = new HashMap<>();
        for(Map.Entry<String, String> entry : ldapProperties.getMappingFiltersGroups().entrySet()) {
            ldapFiltersGroups.put(entry.getValue(), entry.getKey());
        }
        LdapGroupService ldapGroupService = new LdapGroupService();
        ldapGroupService.setLdapFiltersGroups(ldapFiltersGroups);
        ldapGroupService.setLdapTemplate(ldapTemplate);
        ldapGroupService.setGroupSearchBase(ldapProperties.getGroupSearchBase());
        ldapGroupService.setGroupSearchFilter(ldapProperties.getGroupSearchFilter());
        ldapGroupService.setMemberSearchFilter(ldapProperties.getMemberSearchFilter());
        return ldapGroupService;
    }

}
