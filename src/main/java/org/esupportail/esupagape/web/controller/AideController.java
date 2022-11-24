package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.AideHumaine;
import org.esupportail.esupagape.entity.AideMaterielle;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.PeriodeAideHumaine;
import org.esupportail.esupagape.entity.enums.FonctionAidant;
import org.esupportail.esupagape.entity.enums.StatusAideHumaine;
import org.esupportail.esupagape.entity.enums.TypeAideMaterielle;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.AideHumaineService;
import org.esupportail.esupagape.service.AideMaterielleService;
import org.esupportail.esupagape.service.PeriodeAideHumaineService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/dossiers/{id}/aides")
public class AideController {

    private final AideMaterielleService aideMaterielleService;

    private final AideHumaineService aideHumaineService;

    private final PeriodeAideHumaineService periodeAideHumaineService;

    public AideController(AideMaterielleService aideMaterielleService, AideHumaineService aideHumaineService, PeriodeAideHumaineService periodeAideHumaineService) {
        this.aideMaterielleService = aideMaterielleService;
        this.aideHumaineService = aideHumaineService;
        this.periodeAideHumaineService = periodeAideHumaineService;
    }

    @GetMapping
    public String list(Dossier dossier, Model model) {
        setModel(model);
        model.addAttribute("aideMaterielle", new AideMaterielle());
        model.addAttribute("aideHumaine", new AideHumaine());
        model.addAttribute("aideMaterielles", aideMaterielleService.findByDossier(dossier));
        model.addAttribute("aideHumaines", aideHumaineService.findByDossier(dossier));

        return "aides/list";
    }

    @PostMapping("/create-aide-materiel")
    public String createAideMaterielle(@Valid AideMaterielle aideMaterielle, BindingResult bindingResult, Model model, Dossier dossier) {
        if (bindingResult.hasErrors()) {
            setModel(model);
            return "aides/list";
        }
        aideMaterielle.setDossier(dossier);
        aideMaterielleService.create(aideMaterielle);
        return "redirect:/dossiers/" + dossier.getId() + "/aides";
    }


    @PostMapping("/create-aide-humaine")
    public String createAideHumaine(@Valid AideHumaine aideHumaine, BindingResult bindingResult, Model model, Dossier dossier) {
        if (bindingResult.hasErrors()) {
            setModel(model);
            return "aides/list";
        }
        aideHumaine.setDossier(dossier);
        aideHumaineService.create(aideHumaine);
        return "redirect:/dossiers/" + dossier.getId() + "/aides";
    }

    @PutMapping("/aides-materielles/{aideMaterielleId}/update")
    public String updateAideMaterielle(@PathVariable Long aideMaterielleId, @Valid AideMaterielle aideMaterielle, Dossier dossier, RedirectAttributes redirectAttributes) throws AgapeJpaException {
        aideMaterielleService.save(aideMaterielleId, aideMaterielle);
        redirectAttributes.addFlashAttribute("lastEdit", aideMaterielleId);
        return "redirect:/dossiers/" + dossier.getId() + "/aides";
    }

    @DeleteMapping("/aides-materielles/{aideMaterielleId}/delete")
    public String deleteAideMaterielle(@PathVariable Long aideMaterielleId, Dossier dossier) {
        aideMaterielleService.delete(aideMaterielleId);
        return "redirect:/dossiers/" + dossier.getId() + "/aides";
    }

    @GetMapping("/aides-humaines/{aideHumaineId}/update")
    public String editAideHumaine(@PathVariable Long aideHumaineId, Dossier dossier, Model model) {
        setModel(model);
        model.addAttribute("aideHumaine", aideHumaineService.getById(aideHumaineId));
        model.addAttribute("periodeAideHumaineServiceMap", periodeAideHumaineService.getPeriodeAideHumaineServiceMapByAideHumaine(aideHumaineId));
        model.addAttribute("aideHumainePeriodeSums", periodeAideHumaineService.getAideHumainePeriodeSums(aideHumaineId));
        return "aides/update-aide-humaine";
    }

    @PutMapping("/aides-humaines/{aideHumaineId}/update")
    public String updateAideHumaine(@PathVariable Long aideHumaineId, @Valid AideHumaine aideHumaine, BindingResult bindingResult, Dossier dossier, Model model) {
        if (bindingResult.hasErrors()) {
            setModel(model);
            return "aides/update-aide-humaine";
        }
        aideHumaineService.save(aideHumaineId, aideHumaine);
        return "redirect:/dossiers/" + dossier.getId() + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @PutMapping("/aides-humaines/{aideHumaineId}/update-periode/{month}")
    public String updatePeriode(@PathVariable Long aideHumaineId, @PathVariable Integer month, @Valid PeriodeAideHumaine periodeAideHumaine, BindingResult bindingResult, Dossier dossier, Model model) {
        if (bindingResult.hasErrors()) {
            setModel(model);
            return "aides/update-aide-humaine";
        }
        periodeAideHumaineService.save(aideHumaineId, month, periodeAideHumaine);
        return "redirect:/dossiers/" + dossier.getId() + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    private void setModel(Model model) {
        model.addAttribute("typeAideMaterielles", TypeAideMaterielle.values());
        model.addAttribute("fonctionAidants", FonctionAidant.values());
        model.addAttribute("statusAideHumaines", StatusAideHumaine.values());
    }

}
