package org.esupportail.esupagape.web;


import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.service.utils.UserService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.esupportail.esupagape.web.viewentity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice(basePackages = "org.esupportail.esupagape.web.controller")
@EnableConfigurationProperties(ApplicationProperties.class)
public class EsupAgapeControllerAdvice extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(EsupAgapeControllerAdvice.class);

    private final UtilsService utilsService;

    private final UserService userService;

    private final Environment environment;

    private final BuildProperties buildProperties;

    private final ApplicationProperties applicationProperties;

    public EsupAgapeControllerAdvice(UtilsService utilsService, UserService userService, Environment environment, @Autowired(required = false) BuildProperties buildProperties, ApplicationProperties applicationProperties) {
        this.utilsService = utilsService;
        this.userService = userService;
        this.environment = environment;
        this.buildProperties = buildProperties;
        this.applicationProperties = applicationProperties;
    }

    @ModelAttribute
    public void globalAttributes(Model model) {
        model.addAttribute("userName", userService.getUserName());
        model.addAttribute("personLdap", userService.getPersonLdap());
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

    @ExceptionHandler(value = { AgapeException.class })
    protected String handleConflict(
            AgapeException ex, WebRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", new Message("danger", ex.getMessage()));
        return "redirect:" + request.getHeader("referer");
    }

}
