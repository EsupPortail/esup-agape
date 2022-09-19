package org.esupportail.esupagape.config;

import org.esupportail.esupagape.config.security.ApplicationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.dialect.springdata.SpringDataDialect;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties({ApplicationProperties.class})
public class WebAppConfig {

    @Resource
    private ApplicationProperties applicationProperties;

    @Bean
    public SpringDataDialect springDataDialect() {
        return new SpringDataDialect();
    }

//    @PostConstruct
//    public void toto() {
//        for(String inter : applicationProperties.getEnabledImportInterfaces()) {
//            if(inter.equals("APO")) {
//                new ApoImportIndividuService();
//            }
//        }
//
//    }

}
