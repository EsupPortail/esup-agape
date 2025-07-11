package org.esupportail.esupagape.web.controller;

import jakarta.mail.MessagingException;
import org.esupportail.esupagape.entity.UserOthersAffectations;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.repository.UserOthersAffectationsRepository;
import org.esupportail.esupagape.service.*;
import org.esupportail.esupagape.service.ldap.LdapPersonService;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.esupportail.esupagape.service.mail.MailService;
import org.esupportail.esupagape.service.utils.SiseService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.esupportail.esupagape.web.viewentity.Message;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final IndividuService individuService;

    private final DossierService dossierService;

    private final SyncService syncService;

    private final AmenagementService amenagementService;

    private final UtilsService utilsService;

    private final EnumsService enumsService;

    private final CsvImportService csvImportService;

    private final SiseService siseService;

    private final MailService mailService;

    private final SessionRegistry sessionRegistry;

    private final UserOthersAffectationsService userOthersAffectationsService;

    private final UserOthersAffectationsRepository userOthersAffectationsRepository;

    private final LdapPersonService ldapPersonService;

    private final LogService logService;

    public AdminController(
            IndividuService individuService, DossierService dossierService, SyncService syncService,
            AmenagementService amenagementService, UtilsService utilsService, EnumsService enumsService,
            CsvImportService csvImportService, SiseService siseService, MailService mailService, @Qualifier("sessionRegistry") SessionRegistry sessionRegistry, UserOthersAffectationsService userOthersAffectationsService, UserOthersAffectationsRepository userOthersAffectationsRepository, LdapPersonService ldapPersonService, LogService logService) {
        this.individuService = individuService;
        this.dossierService = dossierService;
        this.syncService = syncService;
        this.amenagementService = amenagementService;
        this.utilsService = utilsService;
        this.enumsService = enumsService;
        this.csvImportService = csvImportService;
        this.siseService = siseService;
        this.mailService = mailService;
        this.sessionRegistry = sessionRegistry;
        this.userOthersAffectationsService = userOthersAffectationsService;
        this.userOthersAffectationsRepository = userOthersAffectationsRepository;
        this.ldapPersonService = ldapPersonService;
        this.logService = logService;
    }

    @GetMapping
    public String index(Model model) {
        return "admin/index";
    }

    @GetMapping("/labels")
    public String labels(Model model) {
        model.addAttribute("active", "tasks");
        model.addAttribute("enumEntityAbstracts", enumsService.getAllEnumEntityAbstract());
        model.addAttribute("enumEntityTypes", enumsService.getEnumTypes());
        return "admin/labels";
    }

    @PostMapping(value = "/add-label")
    public String createLabel(@RequestParam String enumEntityType, @RequestParam String code, @RequestParam String label, RedirectAttributes redirectAttributes) {
        try {
            enumsService.addEnumType(enumEntityType, code, label);
            redirectAttributes.addFlashAttribute("message", new Message("success", "label ajoutée"));
        } catch (AgapeJpaException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
        }
        return "redirect:/admin/labels";
    }

    @DeleteMapping(value = "/delete-label")
    public String deleteLabel(@RequestParam Long id, @RequestParam String enumEntityType, RedirectAttributes redirectAttributes) {
        enumsService.deleteEnumType(enumEntityType, id);
        redirectAttributes.addFlashAttribute("message", new Message("success", "Année supprimée"));
        return "redirect:/admin/labels";
    }

    @GetMapping("/tasks")
    public String tasks(Model model) {
        model.addAttribute("active", "tasks");
        return "admin/tasks";
    }

    @GetMapping("/imports")
    public String imports(Model model) {
        model.addAttribute("active", "imports");
        return "admin/imports";
    }

    @GetMapping("/years")
    public String years(Model model) {
        model.addAttribute("active", "years");
        model.addAttribute("years", utilsService.getYears());
        return "admin/years";
    }

    @GetMapping("/logs")
    public String logs(Model model, @PageableDefault(sort = "date", direction = Sort.Direction.DESC, size = 15) Pageable pageable) {
        model.addAttribute("active", "logs");
        model.addAttribute("logs", logService.getAll(pageable));
        return "admin/logs";
    }

    @GetMapping("/sessions")
    public String session(Model model) {
        model.addAttribute("active", "sessions");
        model.addAttribute("years", utilsService.getYears());
        List<SessionInformation> sessions = new ArrayList<>();
        for(Object principal : sessionRegistry.getAllPrincipals()) {
            for(SessionInformation sessionInformation : sessionRegistry.getAllSessions(principal, false)) {
                if(!sessionInformation.isExpired() && sessionInformation.getLastRequest().after(getLastRequestAge()) && sessions.stream().noneMatch(s -> s.getPrincipal().equals(sessionInformation.getPrincipal()))) {
                    sessions.add(sessionInformation);
                }
            }
        }
        model.addAttribute("sessions", sessions);
        model.addAttribute("now", new Date());

        return "admin/sessions";
    }

    @GetMapping("/su")
    public String su(Model model) {
        model.addAttribute("active", "su");
        return "admin/su";
    }

    @GetMapping("/affectations")
    public String affectations(Model model) {
        model.addAttribute("active", "affectations");
        model.addAttribute("userOthersAffectations", userOthersAffectationsRepository.findAll());
        model.addAttribute("codComposantes", dossierService.getCodComposanteLabels());
        return "admin/affectations";
    }

    @GetMapping("/amenagement-resend")
    public String amenagementResend(Model model) {
        model.addAttribute("active", "amenagement-resend");
        model.addAttribute("amenagementsToResend", amenagementService.getAmenagementToResend());
        return "admin/amenagement-resend";
    }

    private Date getLastRequestAge() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -30);
        return calendar.getTime();
    }

    @GetMapping("/import-individus")
    public String forceSync(RedirectAttributes redirectAttributes) throws AgapeException, SQLException {
        redirectAttributes.addFlashAttribute("message", new Message("success", "L'import est terminé"));
        individuService.importIndividus();
        return "redirect:/admin/tasks";
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
        return "redirect:/admin/tasks";
    }

    @GetMapping("/sync-amenagements")
    public String syncAmenagements(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", new Message("success", "La synchro des étudiants est terminée"));
        amenagementService.syncAllAmenagments();
        return "redirect:/admin/tasks";
    }

    @GetMapping("/sync-dossiers")
    public String syncDossier(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", new Message("success", "La synchro des dossiers est terminée"));
        syncService.syncAllDossiers();
        return "redirect:/admin/tasks";
    }

    @GetMapping("/anonymise-dossiers")
    public String anonymiseDossiers(RedirectAttributes redirectAttributes, PersonLdap personLdap) {
        redirectAttributes.addFlashAttribute("message", new Message("success", "L'anonymisation des dossiers est terminée"));
        individuService.anonymiseOldDossiers(personLdap.getEduPersonPrincipalName());
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
        return "redirect:/admin/tasks";
    }

    @GetMapping("/sync-esup-signature")
    public String syncEsupSignature(RedirectAttributes redirectAttributes) throws AgapeException {
        redirectAttributes.addFlashAttribute("message", new Message("success", "La synchro Esup Signature est terminée"));
        amenagementService.syncEsupSignatureAmenagements();
        return "redirect:/admin/tasks";
    }

    @PostMapping(value = "/add-year")
    public String createYear(@RequestParam Integer number, RedirectAttributes redirectAttributes) {
        try {
            utilsService.addYear(number);
            redirectAttributes.addFlashAttribute("message", new Message("success", "Année ajoutée"));
        } catch (AgapeJpaException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
        }
        return "redirect:/admin/years";
    }

    @DeleteMapping(value = "/delete-year")
    public String deleteYear(@RequestParam Long idYear, RedirectAttributes redirectAttributes) {
        utilsService.deleteYear(idYear);
        redirectAttributes.addFlashAttribute("message", new Message("success", "Année supprimée"));
        return "redirect:/admin/years";
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

    @PostMapping("/import-libelles-amenagement")
    public String importLibelleAmenagement(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
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
        return "redirect:/admin/imports";
    }


    @PostMapping(value = "/add-user-others-affectations")
    public String createAffectation(@RequestParam String uid, @RequestParam String[] codComposante, RedirectAttributes redirectAttributes) {
        if (codComposante != null) {
            for (String cod : codComposante) {
                List<UserOthersAffectations> existingAffectation = userOthersAffectationsRepository.findByUidAndCodComposante(uid, cod);

                if (existingAffectation.isEmpty()) {
                    userOthersAffectationsService.addUserOthersAffectations(uid, cod);
                    redirectAttributes.addFlashAttribute("message", new Message("success", "L'affectation a été ajoutée pour " + cod));

                } else {
                    redirectAttributes.addFlashAttribute("message", new Message("danger", "L'affectation existe déjà pour " + cod));
                }
            }
        }
        return "redirect:/admin/affectations";
    }

    @DeleteMapping(value = "/delete-userOthersAffectations")
    public String deleteUserOthersAffectations(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        userOthersAffectationsService.deleteUserOthersAffectations(id);
        redirectAttributes.addFlashAttribute("message", new Message("success", "Affectation supprimée"));
        return "redirect:/admin/affectations";
    }

    @GetMapping(value = "/autocomplete-search-uid", produces = "application/json")
    @ResponseBody
    public List<PersonLdap> autocompleteSearchUid(String uid) {
        return ldapPersonService.findEmployees(uid);
    }

    @PostMapping(value = "/amenagement-resend")
    public String resendAmenagements(RedirectAttributes redirectAttributes) throws Exception {
        amenagementService.resendAmenagements();
        redirectAttributes.addFlashAttribute("message", new Message("success", "Aménagement envoyés"));
        return "redirect:/admin/amenagement-resend";
    }


}