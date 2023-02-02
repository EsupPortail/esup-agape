package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.dtos.DossierIndividuForm;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.entity.enums.enquete.ModFrmn;
import org.esupportail.esupagape.entity.enums.enquete.TypeFrmn;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.DocumentService;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.IndividuService;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/dossiers")
public class DossierController {

    private final DossierService dossierService;

    private final IndividuService individuService;

    private final UtilsService utilsService;

    private final DocumentService documentService;

    public DossierController(DossierService dossierService,
                             IndividuService individuService, UtilsService utilsService, DocumentService documentService) {
        this.dossierService = dossierService;
        this.individuService = individuService;
        this.utilsService = utilsService;
        this.documentService = documentService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String fullTextSearch,
                       @RequestParam(required = false) TypeIndividu typeIndividu,
                       @RequestParam(required = false) StatusDossier statusDossier,
                       @RequestParam(required = false) StatusDossierAmenagement statusDossierAmenagement,
                       @RequestParam(required = false) Integer yearFilter,
                       @PageableDefault(sort = "name") Pageable pageable, Model model) {
        if (yearFilter == null) {
            yearFilter = utilsService.getCurrentYear();
        }
        model.addAttribute("fullTextSearch", fullTextSearch);
        model.addAttribute("typeIndividu", typeIndividu);
        model.addAttribute("statusDossier", statusDossier);
        model.addAttribute("statusDossierAmenagement", statusDossierAmenagement);
        model.addAttribute("dossiers", dossierService.getFullTextSearch(fullTextSearch, typeIndividu, statusDossier, statusDossierAmenagement, yearFilter, pageable));
        model.addAttribute("yearFilter", yearFilter);
        model.addAttribute("years", utilsService.getYears());
        model.addAttribute("statusDossierList", StatusDossier.values());
        model.addAttribute("statusDossierAmenagements", StatusDossierAmenagement.values());
        model.addAttribute("typeIndividuList", TypeIndividu.values());
        return "dossiers/list";
    }

    @GetMapping("/{id}")
    public String update(@PathVariable Long id, Model model) {
        Dossier dossier = dossierService.getById(id);
        model.addAttribute("extendedInfos", dossierService.getInfos(dossier));
        model.addAttribute("classifications", Classification.values());
        model.addAttribute("typeSuiviHandisups", TypeSuiviHandisup.values());
        model.addAttribute("rentreeProchaines", RentreeProchaine.values());
        model.addAttribute("tauxs", Taux.values());
        model.addAttribute("mdphs", Mdph.values());
        model.addAttribute("etats", Etat.values());
        model.addAttribute("statusDossierAmenagements", StatusDossierAmenagement.values());
        model.addAttribute("typeFormations", TypeFrmn.values());
        model.addAttribute("modeFormations", ModFrmn.values());
//        model.addAttribute("currentDossier", dossierService.getById(id));
        model.addAttribute("age", individuService.computeAge(dossier.getIndividu()));
        model.addAttribute("dossierIndividuFrom", new DossierIndividuForm());
        model.addAttribute("attachments", dossierService.getAttachements(dossier.getId()));
        return "dossiers/update";
    }

    @GetMapping("/{id}/sync")
    public String sync(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        dossierService.syncDossier(id);
        try {
            individuService.syncIndividu(dossierService.getById(id).getIndividu().getId());
        } catch (AgapeJpaException e) {
            throw new RuntimeException(e);
        }
        redirectAttributes.addFlashAttribute("message", new Message("success", "Synchonisation effectuée"));
        return "redirect:/dossiers/" + id;
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @Valid Dossier dossier) {
        dossierService.update(id, dossier);
        return "redirect:/dossiers/" + id;
    }

    @PutMapping("/{id}/update-dossier-individu")
    public String update(@PathVariable Long id, @Valid DossierIndividuForm dossierIndividuForm) {
        dossierService.updateDossierIndividu(id, dossierIndividuForm);
        return "redirect:/dossiers/" + id;
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/delete-dossier/{id}")
    public String deleteDossier(@PathVariable("id") long id) {
        dossierService.deleteDossier(id);
        return "redirect:/dossiers";
    }

    @PostMapping("/{id}/add-attachments")
    public String addAttachments(@PathVariable Long id, @RequestParam("multipartFiles") MultipartFile[] multipartFiles, RedirectAttributes redirectAttributes, Dossier dossier) throws AgapeException {
        dossierService.addAttachment(id, multipartFiles);
        redirectAttributes.addFlashAttribute("returnModPJ", true);
        return "redirect:/dossiers/" + dossier.getId();
    }
    @GetMapping(value = "/{id}/get-attachment/{attachmentId}")
    @ResponseBody
    public ResponseEntity<Void> getLastFileFromSignRequest(@PathVariable("attachmentId") Long attachmentId, HttpServletResponse httpServletResponse) throws AgapeIOException {
        documentService.getDocumentHttpResponse(attachmentId, httpServletResponse);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}/delete-attachment/{attachmentId}")
    public String getLastFileFromSignRequest(@PathVariable("id") Long dossierId, @PathVariable("attachmentId") Long attachmentId, RedirectAttributes redirectAttributes, Dossier dossier) throws AgapeException {
        dossierService.deleteAttachment(dossierId, attachmentId);
        redirectAttributes.addFlashAttribute("returnModPJ", true);
        return "redirect:/dossiers/" + dossier.getId();
    }
}
