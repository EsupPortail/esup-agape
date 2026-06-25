package org.esupportail.esupagape.web;

import org.esupportail.esupagape.config.ApplicationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
public class IndexController {

    private final ApplicationProperties applicationProperties;

    public IndexController(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @GetMapping
    public String index(HttpServletRequest httpServletRequest) {
        if(httpServletRequest.isUserInRole("ROLE_ADMIN")
                || httpServletRequest.isUserInRole("ROLE_MANAGER")
                || httpServletRequest.isUserInRole("ROLE_MEDECIN")) {
            return "redirect:/dossiers";
        }
        if(httpServletRequest.isUserInRole("ROLE_SCOLARITE")) {
            return "redirect:/scolarite/amenagements";
        }
        if(Boolean.TRUE.equals(applicationProperties.getValidationReferents()) && httpServletRequest.isUserInRole("ROLE_REFERENT")) {
            return "redirect:/referent/amenagements";
        }
        if(httpServletRequest.isUserInRole("ROLE_ADMINISTRATIF")) {
            return "redirect:/administratif/amenagements";
        }
        return "redirect:/dossiers";
    }

    @GetMapping("/logged-out")
    public String loggedOut() {
        return "logged-out";
    }

}
