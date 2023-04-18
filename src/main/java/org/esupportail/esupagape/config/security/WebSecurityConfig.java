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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.*;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties({LdapProperties.class, CasProperties.class, WebSecurityProperties.class})
@EnableLdapRepositories(basePackages = "org.esupportail.esupagape.repository.ldap")
@EnableJpaRepositories(basePackages = "org.esupportail.esupagape.repository",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org.esupportail.esupagape.repository.ldap.*")
        })
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class WebSecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    private final LdapProperties ldapProperties;

    private final CasProperties casProperties;

    private final WebSecurityProperties webSecurityProperties;

    public WebSecurityConfig(LdapProperties ldapProperties, CasProperties casProperties, WebSecurityProperties webSecurityProperties, LdapContextSource ldapContextSource, SpelGroupService spelGroupService, LdapTemplate ldapTemplate) {
        this.ldapProperties = ldapProperties;
        this.casProperties = casProperties;
        this.webSecurityProperties = webSecurityProperties;
        this.ldapContextSource = ldapContextSource;
        this.spelGroupService = spelGroupService;
        this.ldapTemplate = ldapTemplate;
    }

    private final LdapContextSource ldapContextSource;

    private final SpelGroupService spelGroupService;

    private final LdapTemplate ldapTemplate;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionAuthenticationStrategy(sessionAuthenticationStrategy()).maximumSessions(5).sessionRegistry(sessionRegistry());
        http.exceptionHandling().authenticationEntryPoint(getAuthenticationEntryPoint());

        http.addFilterBefore(requestSingleLogoutFilter(), LogoutFilter.class);
        http.addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class);
        http.addFilterBefore(casAuthenticationFilter(), BasicAuthenticationFilter.class);
        http.logout().invalidateHttpSession(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .addLogoutHandler(securityContextLogoutHandler());
        http.authorizeRequests()
                .antMatchers("/logged-out").permitAll()
                .antMatchers("/webjars", "/webjars/**").permitAll()
                .antMatchers("/css", "/css/**").permitAll()
                .antMatchers("/images", "/images/**").permitAll()
                .antMatchers("/js", "/js/**").permitAll()
                .antMatchers("/").hasAnyRole("ADMIN", "MANAGER", "ESPACE_HANDI", "MEDECIN", "ADMINISTRATIF", "SCOLARITE")
                .antMatchers("/ws-secure", "/ws-secure/**").hasAnyRole("USER")
                .antMatchers("/admin", "/admin/**").hasAnyRole("ADMIN")
                .antMatchers("/individus", "/individus/**").hasAnyRole("ADMIN", "MANAGER", "ESPACE_HANDI", "MEDECIN")
                .antMatchers("/dossiers").hasAnyRole("ADMIN", "MANAGER", "ESPACE_HANDI", "MEDECIN")
                .antMatchers("/dossiers/*").hasAnyRole("ADMIN", "MANAGER", "ESPACE_HANDI", "MEDECIN")
                .antMatchers("/dossiers/*/entretiens").hasAnyRole("ADMIN", "MANAGER", "ESPACE_HANDI")
                .antMatchers("/dossiers/*/aides").hasAnyRole("ADMIN", "MANAGER", "ESPACE_HANDI")
                .antMatchers("/dossiers/*/enquete").hasAnyRole("ADMIN", "MANAGER", "ESPACE_HANDI")
                .antMatchers("/dossiers/*/amenagements").hasAnyRole("ADMIN", "MANAGER", "MEDECIN")
                .antMatchers("/exports", "/exports/**").hasAnyRole("ADMIN", "MANAGER")
                .antMatchers("/statistiques", "/statistiques/**").hasAnyRole("ADMIN", "MANAGER")
                .antMatchers("/dossiers/*/entretiens", "/dossiers/*/entretiens/**").hasAnyRole("ADMIN", "MANAGER", "ESPACE_HANDI")
                .antMatchers("/dossiers/*/aides", "/dossiers/*/aides/**").hasAnyRole("ADMIN", "MANAGER", "ESPACE_HANDI")
                .antMatchers("/dossiers/*/enquete", "/dossiers/*/enquete/**").hasAnyRole("ADMIN", "MANAGER", "ESPACE_HANDI")
                .antMatchers("/dossiers/*/amenagements", "/dossiers/*/amenagements/**").hasAnyRole("ADMIN", "MEDECIN")
                .antMatchers("/administratif/amenagements", "/administratif/amenagements/**").hasAnyRole("ADMIN", "ADMINISTRATIF")
                .antMatchers("/scolarite/amenagements", "/scolarite/amenagements/**").hasAnyRole("ADMIN", "SCOLARITE");
        http.headers().frameOptions().sameOrigin();
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
    public CasAuthenticationFilter casAuthenticationFilter() {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setAuthenticationManager(casAuthenticationManager());
        filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy());
        filter.setServiceProperties(serviceProperties());
        return filter;
    }

    public AuthenticationManager casAuthenticationManager() {
        List<AuthenticationProvider> authenticatedAuthenticationProviders = new ArrayList<>();
        authenticatedAuthenticationProviders.add(casAuthenticationProvider());
        return new ProviderManager(authenticatedAuthenticationProviders);
    }

    @Bean
    public ServiceProperties serviceProperties() {
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
    public SessionRegistryImpl sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public ConcurrentSessionFilter concurrencyFilter() {
        return new ConcurrentSessionFilter(sessionRegistry());
    }

    @Bean
    public RegisterSessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(sessionRegistry());
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

    @Bean
    public SecurityContextLogoutHandler securityContextLogoutHandler() {
        return new SecurityContextLogoutHandler();
    }

    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        singleSignOutFilter.setLogoutCallbackPath("/logged-out");
        return singleSignOutFilter;
    }

    @Bean
    public LogoutFilter requestSingleLogoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(casProperties.getUrl() + "/logout",
                new SecurityContextLogoutHandler());
        logoutFilter.setFilterProcessesUrl("/logout");
        return logoutFilter;
    }

}
