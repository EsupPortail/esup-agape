package org.esupportail.esupagape.web.controller;

import jakarta.mail.MessagingException;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.AmenagementService;
import org.esupportail.esupagape.service.CsvImportService;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.IndividuService;
import org.esupportail.esupagape.service.mail.MailService;
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

import java.io.FileNotFoundException;
import java.io.IOException;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final IndividuService individuService;

    private final DossierService dossierService;

    private final AmenagementService amenagementService;

    private final UtilsService utilsService;

    private final CsvImportService csvImportService;

    private final SiseService siseService;

    private final MailService mailService;

    public AdminController(
            IndividuService individuService,
            DossierService dossierService,
            AmenagementService amenagementService, UtilsService utilsService,
            CsvImportService csvImportService, SiseService siseService, MailService mailService) {
        this.individuService = individuService;
        this.dossierService = dossierService;
        this.amenagementService = amenagementService;
        this.utilsService = utilsService;
        this.csvImportService = csvImportService;
        this.siseService = siseService;
        this.mailService = mailService;
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

    @GetMapping("/test-mail")
    public String testMail(@RequestParam("mail") String mail, RedirectAttributes redirectAttributes) {
        try {
            redirectAttributes.addFlashAttribute("message", new Message("success", "Envoi effectué"));
            mailService.sendTest(mail);
        } catch (MessagingException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", "Erreur lors de l'envoi du mail de test"));
        }
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

    @GetMapping("/anonymise-dossiers")
    public String anonymiseDossiers(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", new Message("success", "L'anonymisation des dossiers est terminée"));
        individuService.anonymiseOldDossiers();
        return "redirect:/admin";
    }

    @GetMapping("/refresh-sise")
    public String refrechSise(RedirectAttributes redirectAttributes) {
        try {
            siseService.refreshAll();
            redirectAttributes.addFlashAttribute("message", new Message("success", "Fichier SISE actualisé"));
        } catch (FileNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", "Erreur lors de l'actualisation SISE : " + e.getMessage()));
        }
        return "redirect:/admin";
    }

    @GetMapping("/sync-esup-signature")
    public String syncEsupSignature(RedirectAttributes redirectAttributes) throws AgapeException {
        redirectAttributes.addFlashAttribute("message", new Message("success", "La synchro Esup Signature est terminée"));
        amenagementService.syncEsupSignatureAmenagements();
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
        if(!file.isEmpty()) {
            try {
                csvImportService.importCsv(file);
                redirectAttributes.addFlashAttribute("message", new Message("success", "L'import des codes du ministère est terminé"));
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("message", new Message("warning", "L'import des codes du ministère a échoué"));
            }
        } else {
            redirectAttributes.addFlashAttribute("message", new Message("danger", "Le fichier csv est vide !"));
        }
        return "redirect:/admin";
    }

    @PostMapping("/import-libelles-ministere")
    public String importCsvLibelle(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        if(!file.isEmpty()) {
            try {
                csvImportService.importCsvLibelle(file);
                redirectAttributes.addFlashAttribute("message", new Message("success", "L'import des libellés du ministère est terminé"));
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("message", new Message("warning", "L'import des libellés du ministère a échoué"));
            }
        } else {
            redirectAttributes.addFlashAttribute("message", new Message("danger", "Le fichier csv est vide !"));
        }
        return "redirect:/admin";
    }

    @PostMapping("/import-libelles-amenagement")
    public String importLibelleAmenagement(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        if(!file.isEmpty()) {
            try {
                csvImportService.importCsvLibelleAmenagement(file);
                redirectAttributes.addFlashAttribute("message", new Message("success", "L'import des libellés aménagements est terminé"));
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("message", new Message("warning", "L'import des libellés aménagements a échoué"));
            }
        } else {
            redirectAttributes.addFlashAttribute("message", new Message("danger", "Le fichier csv est vide !"));
        }
        return "redirect:/admin";
    }
}