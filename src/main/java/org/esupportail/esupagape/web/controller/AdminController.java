package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.IndividuService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.esupportail.esupagape.web.viewentity.Message;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final IndividuService individuService;

    private final DossierService dossierService;

    private final UtilsService utilsService;

    public AdminController(IndividuService individuService, DossierService dossierService, UtilsService utilsService) {
        this.individuService = individuService;
        this.dossierService = dossierService;
        this.utilsService = utilsService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("years", utilsService.getYears());
        return "admin/index";
    }

    @GetMapping("/import-individus")
    public String forceSync(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", new Message("success", "L'import est terminé"));
        individuService.importIndividus();
        return "redirect:/admin";
    }

    @GetMapping("/sync-individus")
    public String syncIndividus(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", new Message("success", "La synchro des étudiants est terminée"));
        individuService.syncAllIndividus();
        return "redirect:/admin";
    }

    @GetMapping("/sync-dossiers")
    public String syncDossier(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", new Message("success", "La synchro des dossiers est terminée"));
        dossierService.syncAllDossiers();
        return "redirect:/admin";
    }

    @PostMapping(value = "/add-year")
    public String createYear(@RequestParam Integer number, RedirectAttributes redirectAttributes) {
        try {
            utilsService.addYear(number);
        } catch (AgapeJpaException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
        }
        return "redirect:/admin";
    }

    @DeleteMapping(value = "/delete-year")
    public String deleteYear(@RequestParam Long idYear) {
        utilsService.deleteYear(idYear);
        return "redirect:/admin";
    }

    @DeleteMapping(value = "/delete-individu/{idIndividu}")
    public String deleteIndividu(@PathVariable("idIndividu") long idIndividu) {
        individuService.deleteIndividu(idIndividu);
        return "redirect:/dossiers";
    }

    @DeleteMapping(value = "/delete-dossier/{idIndividu}")
    public String deleteDossier(@PathVariable("idIndividu") long idIndividu) {
        dossierService.deleteDossier(idIndividu);
        return "redirect:/dossiers";
    }

}
