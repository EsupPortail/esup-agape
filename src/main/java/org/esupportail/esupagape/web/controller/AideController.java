package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.AideMaterielle;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.enums.TypeAideMaterielle;
import org.esupportail.esupagape.service.AideMaterielleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/dossiers/{id}/aides")
public class AideController {

    private final AideMaterielleService aideMaterielleService;

    public AideController(AideMaterielleService aideMaterielleService) {
        this.aideMaterielleService = aideMaterielleService;
    }

    @GetMapping
    public String list(Dossier dossier, Model model) {
        setModel(model, dossier);
        model.addAttribute("aideMaterielle", new AideMaterielle());
        return "aides/list";
    }

    @PostMapping("/create-aide-materiel")
    public String createAideMaterielle(@Valid AideMaterielle aideMaterielle, BindingResult bindingResult, Model model, Dossier dossier) {
        if (bindingResult.hasErrors()) {
            setModel(model, dossier);
            return "aides/list";
        }
        aideMaterielle.setDossier(dossier);
        aideMaterielleService.create(aideMaterielle);
        return "redirect:/dossiers/" + dossier.getId() + "/aides";
    }

    @DeleteMapping("/aides-materielles/{aideMaterielleId}/delete")
    public String deleteAideMaterielle(@PathVariable Long aideMaterielleId, Dossier dossier) {
        aideMaterielleService.delete(aideMaterielleId);
        return "redirect:/dossiers/" + dossier.getId() + "/aides";
    }

    private void setModel(Model model, Dossier dossier) {
        model.addAttribute("aideMaterielles", aideMaterielleService.findByDossier(dossier));
        model.addAttribute("typeAideMaterielles", TypeAideMaterielle.values());
    }

}
