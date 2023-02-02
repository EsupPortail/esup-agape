package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.AmenagementService;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.esupportail.esupagape.web.viewentity.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping("/dossiers/{id}/amenagements")
public class AmenagementController {

    private final AmenagementService amenagementService;

    public AmenagementController(AmenagementService amenagementService) {
        this.amenagementService = amenagementService;
    }

    @GetMapping
    public String list(Dossier dossier, Model model) {
        model.addAttribute("amenagements", amenagementService.findByDossier(dossier));
        return "amenagements/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        setModel(model);
        model.addAttribute("amenagement", new Amenagement());
        return "amenagements/create";
    }

    @PostMapping("/create")
    public String createSave(@Valid Amenagement amenagement, Dossier dossier, PersonLdap personLdap, RedirectAttributes redirectAttributes) {
        amenagement.setId(null);
        amenagementService.create(amenagement, dossier.getId(), personLdap);
        return "redirect:/dossiers/" + dossier.getId() + "/amenagements/" + amenagement.getId() + "/update";
    }

    @GetMapping("{amenagementId}/show")
    public String show(@PathVariable Long amenagementId, Model model) {
        setModel(model);
        model.addAttribute("amenagement",amenagementService.getById(amenagementId));
        model.addAttribute("currentDossier",amenagementService.getById(amenagementId).getDossier());
        return "amenagements/show";
    }
    @GetMapping("/{amenagementId}/update")
    public String update(@PathVariable Long amenagementId, Model model) throws AgapeJpaException {
        model.addAttribute("amenagement", amenagementService.getById(amenagementId));
        setModel(model);
        return "amenagements/update";
    }

    @PutMapping("/{amenagementId}/update")
    public  String update(@PathVariable Long amenagementId, @Valid Amenagement amenagement, Dossier dossier) throws AgapeJpaException {
        amenagementService.update(amenagementId, amenagement);
        return "redirect:/dossiers/" + dossier.getId() + "/amenagements/" + amenagementId + "/update";
    }

    private void setModel(Model model) {
        model.addAttribute("typeAmenagements" , TypeAmenagement.values());
        model.addAttribute("tempsMajores" , TempsMajore.values());
        model.addAttribute("typeEpreuves" , TypeEpreuve.values());
        model.addAttribute("classifications", Classification.values());
        model.addAttribute("autorisations", Autorisation.values());
    }
    @DeleteMapping(value = "/{amenagementId}/delete")
    public String deleteAmenagement(@PathVariable Long amenagementId, Dossier dossier, RedirectAttributes redirectAttributes) {
        try {
            amenagementService.softDeleteAmenagement(amenagementId);
            redirectAttributes.addFlashAttribute("message", new Message("success", "L'aménagement a été supprimé"));
        } catch (AgapeException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
        }
        return "redirect:/dossiers/" + dossier.getId() + "/amenagements";
    }

    @PostMapping("/{amenagementId}/validation-medecin")
    public String validationMedecin(@PathVariable Long amenagementId, Dossier dossier, RedirectAttributes redirectAttributes) {
        try {
            amenagementService.validationMedecin(amenagementId);
            redirectAttributes.addFlashAttribute("message", new Message("success", "L'aménagement a été transmis à l'administration"));
        } catch (AgapeException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
        }

        return "redirect:/dossiers/" + dossier.getId() + "/amenagements/" + amenagementId + "/update";
    }

    @GetMapping(value = "/{amenagementId}/get-certificat", produces = "application/zip")
    @ResponseBody
    public ResponseEntity<Void> getCertificat(@PathVariable("amenagementId") Long amenagementId, HttpServletResponse httpServletResponse) throws IOException, AgapeException {
        httpServletResponse.setContentType("application/pdf");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"certificat_" + amenagementId + ".pdf\"");
        amenagementService.getCertificat(amenagementId, httpServletResponse);
        httpServletResponse.flushBuffer();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/{amenagementId}/get-avis", produces = "application/zip")
    @ResponseBody
    public ResponseEntity<Void> getAvis(@PathVariable("amenagementId") Long amenagementId, HttpServletResponse httpServletResponse) throws IOException, AgapeException {
        httpServletResponse.setContentType("application/pdf");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"certificat_" + amenagementId + ".pdf\"");
        amenagementService.getAvis(amenagementId, httpServletResponse);
        httpServletResponse.flushBuffer();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}