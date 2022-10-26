package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Entretien;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.esupportail.esupagape.service.DocumentService;
import org.esupportail.esupagape.service.EntretienService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
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
    public String list(@PathVariable Long id,  Model model) {
        List<Entretien> entretiens = entretienService.getEntretiensByDossier(id);
        model.addAttribute("entretiens", entretiens);
        return "entretiens/list";
    }

    @GetMapping("/{entretienId}")
    public String showEntretien(@PathVariable Long entretienId, Model model) throws AgapeException {
        Entretien entretien = entretienService.getById(entretienId);
        model.addAttribute("entretien", entretien);
        model.addAttribute("attachments", entretienService.getAttachements(entretienId));
        return "entretiens/show";
    }

    @GetMapping("/{entretienId}/update")
    public String updateEntretien(@PathVariable Long entretienId, Model model) throws AgapeException {
        Entretien entretien = entretienService.getById(entretienId);
        model.addAttribute("entretien", entretien);
        return "entretiens/update";
    }

    @PutMapping("/entretiens")
    public String update(@Valid Entretien entretien, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "entretiens/update";
        }
        entretienService.save(entretien);
        return "redirect:/dossiers/{id}/entretiens";
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
