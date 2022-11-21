package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.dtos.EntretienAttachement;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Entretien;
import org.esupportail.esupagape.entity.enums.TypeContact;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.DocumentService;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.EntretienService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

    private final DossierService dossierService;

    public EntretienController(EntretienService entretienService, DocumentService documentService, DossierService dossierService) {
        this.entretienService = entretienService;
        this.documentService = documentService;
        this.dossierService = dossierService;
    }

   /* @GetMapping
    public String list(@PathVariable Long id, @PageableDefault(
            sort = "date",
            direction = Sort.Direction.DESC) Pageable pageable,
                       Model model) {
        Page<Entretien> entretiens = entretienService.findEntretiensByDossierId(id, pageable);
        model.addAttribute("entretiens", entretiens);
        return "entretiens/list";
    }*/

    @GetMapping("/{entretienId}")
    public String showEntretien(@PathVariable Long entretienId, Model model) throws AgapeException {
        Entretien entretien = entretienService.getById(entretienId);
        model.addAttribute("entretien", entretien);
        model.addAttribute("attachments", entretienService.getAttachements(entretienId));
        return "entretiens/show";
    }

    @GetMapping
    public String list(Dossier dossier, @PageableDefault(
            sort = "date",
            direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        Page<EntretienAttachement> entretiens = entretienService.findEntretiensWithAttachementsByDossierId(dossier.getId(), pageable);
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
    public String create(@Valid Entretien entretien, BindingResult bindingResult, Dossier dossier, Model model) {
        if (bindingResult.hasErrors()) {
            setModel(model, dossier);
            model.addAttribute("typeContacts", Arrays.asList(TypeContact.values()));
            return "entretiens/list";
        }
        entretien.setDossier(dossier);
        entretienService.create(entretien);
        return "redirect:/dossiers/" + dossier.getId() + "/entretiens";
    }

    private void setModel(Model model, Dossier dossier) {
        model.addAttribute("entretiens", entretienService.findByDossier(dossier, Pageable.unpaged()));
    }

    @GetMapping("/{entretienId}/update")
    public String updateEntretien(@PathVariable Long entretienId, Model model) throws AgapeException {
        Entretien entretien = entretienService.getById(entretienId);
        model.addAttribute("entretien", entretien);
        return "entretiens/update";
    }

    /*@PutMapping("/{entretienId}")
    public String update(@PathVariable Long entretienId, @Valid Entretien entretien, BindingResult bindingResult, Dossier dossier) {
        if (bindingResult.hasErrors()) {
            return "entretiens/update";
        }
        entretienService.save(entretien);
        return "redirect:/dossiers/" + entretienId;
    }*/

    @PutMapping("/{entretienId}/update")
    public String update(@PathVariable Long entretienId, @Valid Entretien entretien, Model model) throws AgapeJpaException {
        entretienService.update(entretienId, entretien);
        return "redirect:/dossiers/{id}/entretiens/";
    }

    @DeleteMapping(value = "/{entretienId}/delete")
    public String deleteDossier(@PathVariable Long entretienId, Dossier dossier) {
        entretienService.deleteEntretien(entretienId);
        return "redirect:/dossiers/" + dossier.getId() + "/entretiens";
    }

    @PostMapping("/{entretienId}/add-attachments")
    public String addAttachments(@PathVariable Long entretienId, @RequestParam("multipartFiles") MultipartFile[] multipartFiles, Dossier dossier) throws AgapeException {
        entretienService.addAttachment(entretienId, multipartFiles);
        return "redirect:/dossiers/" + dossier.getId() + "/entretiens/" + entretienId;
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
        return "redirect:/dossiers/" + dossier.getId() + "/entretiens/" + entretienId;
    }

}
