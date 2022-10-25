package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Entretien;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.service.EntretienService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/dossiers/{id}/entretiens")
public class EntretienController {

    private final EntretienService entretienService;

    public EntretienController(EntretienService entretienService) {
        this.entretienService = entretienService;
    }

    @GetMapping
    public String list(@PathVariable Long id,  Model model) {
        List<Entretien> entretiens = entretienService.getEntretiensByDossier(id);
        model.addAttribute("entretiens", entretiens);
        return "entretiens/list";
    }

    @GetMapping("/{entretienId}")
    public String showEntretien(@PathVariable Long entretienId, Model model) throws AgapeException {
        Entretien entretien = entretienService.findById(entretienId);
        model.addAttribute("entretien", entretien);
        return "entretiens/show";
    }

    @GetMapping("/{entretienId}/update")
    public String updateEntretien(@PathVariable Long entretienId, Model model) throws AgapeException {
        Entretien entretien = entretienService.findById(entretienId);
        model.addAttribute("entretien", entretien);
        return "entretiens/update";
    }

    @PutMapping("/entretiens")
    public String update(@Valid Entretien entretien, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "entretiens/update";
        }
        entretienService.save(entretien);
        return "redirect:/dossiers/{id}/entretiens";
    }

    @DeleteMapping(value = "/{entretienId}/delete")
    public String deleteDossier(@PathVariable Long entretienId) {
        entretienService.deleteEntretien(entretienId);
        return "redirect:/dossiers/{id}/entretiens";
    }
}
