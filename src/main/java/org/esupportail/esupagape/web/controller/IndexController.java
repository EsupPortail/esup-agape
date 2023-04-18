package org.esupportail.esupagape.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
public class IndexController {

    @GetMapping
    public String index(HttpServletRequest httpServletRequest) {
        if(httpServletRequest.isUserInRole("ROLE_SCOLARITE")) {
            return "redirect:/scolarite/amenagements";
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
