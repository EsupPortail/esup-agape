package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.dtos.EntretienAttachement;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Entretien;
import org.esupportail.esupagape.entity.enums.TypeContact;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.DocumentService;
import org.esupportail.esupagape.service.EntretienService;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/dossiers/{id}/entretiens")
public class EntretienController {

    private final EntretienService entretienService;

    private final DocumentService documentService;

    public EntretienController(EntretienService entretienService, DocumentService documentService) {
        this.entretienService = entretienService;
        this.documentService = documentService;
    }

    @GetMapping
    public String list(Dossier dossier, @PageableDefault(
            sort = "date",
            direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        Page<EntretienAttachement> entretiens = entretienService.findEntretiensWithAttachementsByDossierId(dossier.getId(),pageable);
        model.addAttribute("entretiens", entretiens);
        model.addAttribute("entretien", new Entretien());
        model.addAttribute("typeContacts", Arrays.asList(TypeContact.values()));
        return "entretiens/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        List<TypeContact> typeContacts = Arrays.asList(TypeContact.values());
        model.addAttribute("entretien", new Entretien());
        model.addAttribute("typeContacts", typeContacts);
        return "entretiens/list";
    }

    @PostMapping("/create-entretien")
    @PreAuthorize("@dossierService.isDossierOfThisYear(#dossier)")
    public String create(@Valid Entretien entretien, BindingResult bindingResult, Dossier dossier, PersonLdap personLdap, Model model) {
        if (bindingResult.hasErrors()) {
            setModel(model, dossier);
            model.addAttribute("typeContacts", Arrays.asList(TypeContact.values()));
            return "entretiens/list";
        }
        entretien.setDossier(dossier);
        entretienService.create(entretien, dossier.getId(), personLdap);
        return "redirect:/dossiers/" + dossier.getId() + "/entretiens";
    }

    private void setModel(Model model, Dossier dossier) {
        model.addAttribute("entretiens", entretienService.findByDossier(dossier, Pageable.unpaged()));
    }

    @GetMapping("/{entretienId}/update")
    public String updateEntretien(@PathVariable Long entretienId, Model model) throws AgapeException {
        Entretien entretien = entretienService.getById(entretienId);
        model.addAttribute("entretien", entretien);
        model.addAttribute("attachments", entretienService.getAttachements(entretienId));
        model.addAttribute("typeContacts", TypeContact.values());
        return "entretiens/update";
    }

    @PutMapping("/{entretienId}/update")
    @PreAuthorize("@dossierService.isDossierOfThisYear(#dossier)")
    public String update(@PathVariable Long entretienId, @Valid Entretien entretien, PersonLdap personLdap,  Dossier dossier) throws AgapeJpaException {
        entretienService.update(entretienId, entretien, personLdap);
        return "redirect:/dossiers/" + dossier.getId() + "/entretiens/" + entretienId + "/update";
    }

    @DeleteMapping(value = "/{entretienId}/delete")
    @PreAuthorize("@dossierService.isDossierOfThisYear(#dossier)")
    public String deleteDossier(@PathVariable Long entretienId, Dossier dossier) {
        entretienService.deleteEntretien(entretienId);
        return "redirect:/dossiers/" + dossier.getId() + "/entretiens";
    }

    @PostMapping("/{entretienId}/add-attachments")
    public String addAttachments(@PathVariable Long entretienId, @RequestParam("multipartFiles") MultipartFile[] multipartFiles, Dossier dossier) throws AgapeException {
        entretienService.addAttachment(entretienId, multipartFiles);
        return "redirect:/dossiers/" + dossier.getId() + "/entretiens/" + entretienId + "/update";
    }

    @GetMapping(value = "/{entretienId}/get-attachment/{attachmentId}")
    @ResponseBody
    public ResponseEntity<Void> getLastFileFromSignRequest(@PathVariable("attachmentId") Long attachmentId, HttpServletResponse httpServletResponse) throws AgapeIOException {
        documentService.getDocumentHttpResponse(attachmentId, httpServletResponse);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{entretienId}/delete-attachment/{attachmentId}")
    public String getLastFileFromSignRequest(@PathVariable("entretienId") Long entretienId, @PathVariable("attachmentId") Long attachmentId, Dossier dossier) throws AgapeException {
        entretienService.deleteAttachment(entretienId, attachmentId);
        return "redirect:/dossiers/" + dossier.getId() + "/entretiens/" + entretienId + "/update";
    }

}
