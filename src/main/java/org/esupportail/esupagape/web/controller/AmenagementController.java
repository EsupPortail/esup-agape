package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.service.AmenagementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dossiers/{id}/amenagements")
public class AmenagementController {

    private final AmenagementService amenagementService;

    public AmenagementController(AmenagementService amenagementService) {
        this.amenagementService = amenagementService;
    }


    @GetMapping
    public String list(Dossier dossier, Model model) {
        model.addAttribute("amenagements", amenagementService.findByDossier(dossier));
        return "amenagements/list";
    }

}
