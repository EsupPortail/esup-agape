package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.enums.Autorisation;
import org.esupportail.esupagape.entity.enums.Classification;
import org.esupportail.esupagape.entity.enums.TypeAmenagement;
import org.esupportail.esupagape.entity.enums.TypeEpreuve;
import org.esupportail.esupagape.service.AmenagementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

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

    @GetMapping("/create")
    public String create(Dossier dossier, Model model) {
        setModel(model);
        Amenagement amenagement = new Amenagement();
        amenagement.setDossier(dossier);
        model.addAttribute("amenagement", amenagement);
        return "amenagements/create";
    }

    @PostMapping("/create")
    public String createSave(@Valid Amenagement amenagement, Dossier dossier, RedirectAttributes redirectAttributes) {
        amenagement.setDossier(dossier);
        amenagementService.create(amenagement);
//        redirectAttributes.addAllAttributes("message", new Message("success", ))
        return "redirect:/dossiers/" + dossier.getId() + "/amenagements/" + amenagement.getId() + "/update";
    }

    @GetMapping("/{amenagementId}/update")
    public String update(@PathVariable Long amenagementId, Dossier dossier, Model model) {
        model.addAttribute("amenagement", amenagementService.getById(amenagementId));
        return "amenagements/create";
    }

    private void setModel(Model model) {
        model.addAttribute("typeAmenagements" , TypeAmenagement.values());
        model.addAttribute("typeEpreuves" , TypeEpreuve.values());
        model.addAttribute("classifications", Classification.values());
        model.addAttribute("autorisations", Autorisation.values());
    }

}