package org.esupportail.esupagape.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.esupportail.esupagape.dtos.forms.DossierFilter;
import org.esupportail.esupagape.dtos.forms.DossierIndividuForm;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.entity.enums.enquete.ModFrmn;
import org.esupportail.esupagape.entity.enums.enquete.TypFrmn;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.repository.LogRepository;
import org.esupportail.esupagape.service.*;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.esupportail.esupagape.web.viewentity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dossiers")
public class DossierController {

    private final DossierService dossierService;

    private final IndividuService individuService;

    private final SyncService syncService;

    private final UtilsService utilsService;

    private final DocumentService documentService;

    private final EnqueteService enqueteService;

    private final EnumsService enumsService;
    private final LogRepository logRepository;

    public DossierController(DossierService dossierService, IndividuService individuService, SyncService syncService, UtilsService utilsService, DocumentService documentService, EnqueteService enqueteService, EnumsService enumsService, LogRepository logRepository) {
        this.dossierService = dossierService;
        this.individuService = individuService;
        this.syncService = syncService;
        this.utilsService = utilsService;
        this.documentService = documentService;
        this.enqueteService = enqueteService;
        this.enumsService = enumsService;
        this.logRepository = logRepository;
    }

    @GetMapping
    public String list(
            @Valid DossierFilter dossierFilter,
            @RequestParam(required = false) String fullTextSearch,
            @RequestParam(required = false) TypeIndividu typeIndividu,
            @RequestParam(required = false) StatusDossier statusDossier,
            @RequestParam(required = false) StatusDossierAmenagement statusDossierAmenagement,
            @RequestParam(required = false) Integer yearFilter,
            @PageableDefault(sort = "name") Pageable pageable, Model model) {
        if (yearFilter == null) {
            yearFilter = utilsService.getCurrentYear();
        }
        model.addAttribute("dossierFilter", new DossierFilter());
        model.addAttribute("fullTextSearch", fullTextSearch);
        model.addAttribute("typeIndividu", typeIndividu);
        model.addAttribute("statusDossier", statusDossier);
        model.addAttribute("statusDossierAmenagement", statusDossierAmenagement);
        if(dossierFilter != null && dossierFilter.getYear() != null) {
            model.addAttribute("dossiers", dossierService.dossierIndividuClassDtoPage(dossierFilter, pageable));
            model.addAttribute("mails", String.join("\n", dossierService.filteredEmails(dossierFilter)));
            model.addAttribute("dossierFilter", dossierFilter);
        } else {
//            Pageable newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("name").and(Sort.by("firstName")).and(Sort.by("year").descending()));
            model.addAttribute("dossiers", dossierService.getFullTextSearch(fullTextSearch, typeIndividu, statusDossier, statusDossierAmenagement, yearFilter, pageable));
        }
        model.addAttribute("yearFilter", yearFilter);
        model.addAttribute("years", utilsService.getYears());
        model.addAttribute("statusDossierList", StatusDossier.values());
        model.addAttribute("statusDossierAmenagements", StatusDossierAmenagement.values());
        model.addAttribute("typeIndividuList", TypeIndividu.values());
        model.addAttribute("typFrmns", TypFrmn.values());
        model.addAttribute("modFrmns", ModFrmn.values());
        model.addAttribute("genders", Gender.values());
        model.addAttribute("classifications", Classification.values());
        model.addAttribute("codComposantes", dossierService.getCodComposanteLabels());
        model.addAttribute("niveauEtudes", dossierService.getAllNiveauEtudes());
        model.addAttribute("secteurDisciplinaires", dossierService.getAllSecteurDisciplinaire());
        model.addAttribute("libelleFormations", dossierService.getAllLibelleFormation());
        model.addAttribute("mdphs", Mdph.values());
        model.addAttribute("fixCPs", individuService.getAllFixCP());
        model.addAttribute("yearOfBirths", individuService.getAllDateOfBirth());
        model.addAttribute("typeAideMaterielles", enumsService.getAllTypeAideMaterielle());
        model.addAttribute("fonctionAidants", FonctionAidant.values());
        model.addAttribute("individu", new Individu());
        model.addAttribute("genders", Gender.values());
        return "dossiers/list";
    }

    @GetMapping("/{dossierId}")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MEDECIN')")
    public String update(@PathVariable Long dossierId, Model model) {
        Dossier dossier = dossierService.getById(dossierId);
        model.addAttribute("classifications", Classification.values());
        model.addAttribute("typeSuiviHandisups", TypeSuiviHandisup.values());
        model.addAttribute("rentreeProchaines", RentreeProchaine.values());
        model.addAttribute("tauxs", Taux.values());
        model.addAttribute("mdphs", Mdph.values());
        model.addAttribute("etats", Etat.values());
        model.addAttribute("statusDossierAmenagements", StatusDossierAmenagement.values());
        model.addAttribute("typeFormations", TypFrmn.values());
        model.addAttribute("modeFormations", ModFrmn.values());
        model.addAttribute("age", individuService.computeAge(dossier.getIndividu()));
        model.addAttribute("dossierIndividuForm", new DossierIndividuForm());
        model.addAttribute("attachments", dossierService.getAttachments(dossier.getId()));
        model.addAttribute("logs", logRepository.findByDossierId(dossierId));
        return "dossiers/update";
    }

    @GetMapping("/{dossierId}/sync")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String sync(@PathVariable Long dossierId, RedirectAttributes redirectAttributes) {
        dossierService.syncDossier(dossierId);
        try {
            syncService.syncIndividu(dossierService.getById(dossierId).getIndividu().getId());
        } catch (AgapeJpaException e) {
            throw new RuntimeException(e);
        }
        redirectAttributes.addFlashAttribute("message", new Message("success", "Synchronisation effectuée"));
        return "redirect:/dossiers/" + dossierId;
    }

    @PutMapping("/{dossierId}")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public String update(@PathVariable Long dossierId, @Valid Dossier dossier, PersonLdap personLdap) {
        dossierService.update(dossierId, dossier, personLdap.getEduPersonPrincipalName());
        enqueteService.getAndUpdateByDossierId(dossierId, personLdap.getEduPersonPrincipalName());
        return "redirect:/dossiers/" + dossierId;
    }

    @PutMapping("/{dossierId}/update-dossier-individu")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public String update(@PathVariable Long dossierId, @Valid DossierIndividuForm dossierIndividuForm, PersonLdap personLdap) {
        dossierService.updateDossierIndividu(dossierId, dossierIndividuForm, personLdap.getEduPersonPrincipalName());
        return "redirect:/dossiers/" + dossierId;
    }

    @DeleteMapping(value = "/delete-dossier/{dossierId}")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public String deleteDossier(@PathVariable Long dossierId, HttpServletRequest request) {
        dossierService.deleteDossier(dossierId);
        return "redirect:" + request.getHeader("referer");
    }

    @PostMapping("/{dossierId}/add-attachments")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public String addAttachments(
            @PathVariable Long dossierId,
            @RequestParam("multipartFiles") MultipartFile[] multipartFiles,
            RedirectAttributes redirectAttributes) throws AgapeException {
        dossierService.addAttachment(dossierId, multipartFiles);
        redirectAttributes.addFlashAttribute("returnModPJ", true);
        return "redirect:/dossiers/" + dossierId;
    }

    @GetMapping(value = "/{dossierId}/get-attachment/{attachmentId}")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @ResponseBody
    public ResponseEntity<Void> getLastFileFromSignRequest(
            @PathVariable("attachmentId") Long attachmentId,
            HttpServletResponse httpServletResponse) throws AgapeIOException {
        documentService.getDocumentHttpResponse(attachmentId, httpServletResponse);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{dossierId}/delete-attachment/{attachmentId}")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public String getLastFileFromSignRequest(
            @PathVariable Long dossierId,
            @PathVariable("attachmentId") Long attachmentId,
            RedirectAttributes redirectAttributes) {
        dossierService.deleteAttachment(dossierId, attachmentId);
        redirectAttributes.addFlashAttribute("returnModPJ", true);
        return "redirect:/dossiers/" + dossierId;
    }

//    @PostMapping(value = "/{dossierId}/anonymise-unsubscribe")
//    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
//    public String anonymiseUnsubscribeDossier(@PathVariable Long dossierId) {
//        dossierService.anonymiseUnsubscribeDossier (dossierId);
//        return "redirect:/dossiers/";
//    }

    @GetMapping("/notes/{dossierId}")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public String getNotes(@PathVariable Long dossierId, Model model) {
        Dossier dossier = dossierService.getById(dossierId);
        model.addAttribute("extendedInfos", dossierService.getInfos(dossier.getIndividu(), dossier.getYear()));
        return "dossiers/notes";
    }
}
