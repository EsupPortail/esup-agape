package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.CsvImportService;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.IndividuService;
import org.esupportail.esupagape.service.utils.SiseService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.esupportail.esupagape.web.viewentity.Message;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final IndividuService individuService;

    private final DossierService dossierService;

    private final UtilsService utilsService;

    private final CsvImportService csvImportService;

    private final SiseService siseService;

    public AdminController(
            IndividuService individuService,
            DossierService dossierService,
            UtilsService utilsService,
            CsvImportService csvImportService, SiseService siseService) {
        this.individuService = individuService;
        this.dossierService = dossierService;
        this.utilsService = utilsService;
        this.csvImportService = csvImportService;
        this.siseService = siseService;
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

    @GetMapping("/refresh-sise")
    public String refrechSise(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", new Message("success", "Fichier SISE actualisé"));
        siseService.refreshAll();
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

    @PostMapping("/import-codes-ministere")
    public String importCsv(@RequestParam("file") MultipartFile file,
                            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", new Message("success", "L'import des codes du ministère est terminé"));
        try {
            csvImportService.importCsv(file);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", new Message("warning", "L'import des codes du ministère a échoué"));
        }
        return "redirect:/admin";
    }

    @PostMapping("/import-libelles-ministere")
    public String importCsvLibelle(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException {
        redirectAttributes.addFlashAttribute("message", new Message("success", "L'import des libellés du ministère est terminé"));
        try {
            csvImportService.importCsvLibelle(file);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", new Message("warning", "L'import des libellés du ministère a échoué"));
        }
        return "redirect:/admin";
    }
}