package org.esupportail.esupagape.web;


import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice(basePackages = "org.esupportail.esupagape.web.controller")
@EnableConfigurationProperties(ApplicationProperties.class)
public class EsupAgapeControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(EsupAgapeControllerAdvice.class);

    private final UtilsService utilsService;

    private final Environment environment;

    private final BuildProperties buildProperties;

    private final ApplicationProperties applicationProperties;

    public EsupAgapeControllerAdvice(UtilsService utilsService, Environment environment, @Autowired(required = false) BuildProperties buildProperties, ApplicationProperties applicationProperties) {
        this.utilsService = utilsService;
        this.environment = environment;
        this.buildProperties = buildProperties;
        this.applicationProperties = applicationProperties;
    }

    @ModelAttribute
    public void globalAttributes(Model model) {

        if (environment.getActiveProfiles().length > 0 && environment.getActiveProfiles()[0].equals("dev")) {
            model.addAttribute("profile", environment.getActiveProfiles()[0]);
        }
        if (buildProperties != null) {
            model.addAttribute("versionApp", buildProperties.getVersion());
        } else {
            model.addAttribute("versionApp", "dev");
        }
        model.addAttribute("applicationEmail", applicationProperties.getApplicationEmail());
        model.addAttribute("currentYear", utilsService.getCurrentYear());
        model.addAttribute("now", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
    }

}
