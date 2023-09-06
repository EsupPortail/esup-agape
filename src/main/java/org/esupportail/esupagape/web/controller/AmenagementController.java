package org.esupportail.esupagape.web.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.exception.AgapeRuntimeException;
import org.esupportail.esupagape.repository.LibelleAmenagementRepository;
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

import java.io.IOException;

@Controller
@RequestMapping("/dossiers/{dossierId}/amenagements")
public class AmenagementController {

    private final AmenagementService amenagementService;
    private final LibelleAmenagementRepository libelleAmenagementRepository;

    public AmenagementController(AmenagementService amenagementService,
                                 LibelleAmenagementRepository libelleAmenagementRepository) {
        this.amenagementService = amenagementService;
        this.libelleAmenagementRepository = libelleAmenagementRepository;
    }

    @GetMapping
    public String list(@PathVariable Long dossierId, Model model) {
        model.addAttribute("amenagements", amenagementService.findByDossier(dossierId));
        return "amenagements/list";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ROLE_MEDECIN') or hasRole('ROLE_ADMIN')")
    public String create(Model model) {
        setModel(model);
        model.addAttribute("amenagement", new Amenagement());
        model.addAttribute("libellesAmenagement", libelleAmenagementRepository.findAll());
        return "amenagements/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_MEDECIN') or hasRole('ROLE_ADMIN')")
    public String createSave(@PathVariable Long dossierId, @Valid Amenagement amenagement, PersonLdap personLdap, RedirectAttributes redirectAttributes) {
        amenagement.setId(null);
        try {
            amenagementService.create(amenagement, dossierId, personLdap);
        } catch (AgapeException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
        }
        return "redirect:/dossiers/" + dossierId + "/amenagements/" + amenagement.getId() + "/update";
    }

    @GetMapping("{amenagementId}/show")
    public String show(@PathVariable Long amenagementId, Model model) throws MessagingException {
        setModel(model);
        model.addAttribute("amenagement", amenagementService.getById(amenagementId));
        return "amenagements/show";
    }
    @GetMapping("/{amenagementId}/update")
    @PreAuthorize("hasRole('ROLE_MEDECIN') or hasRole('ROLE_ADMIN')")
    public String update(@PathVariable Long amenagementId, Model model) throws AgapeJpaException {
        model.addAttribute("amenagement", amenagementService.getById(amenagementId));
        model.addAttribute("libellesAmenagement", libelleAmenagementRepository.findAll());
        setModel(model);
        return "amenagements/update";
    }

    @PutMapping("/{amenagementId}/update")
    @PreAuthorize("hasRole('ROLE_MEDECIN') or hasRole('ROLE_ADMIN')")
    public  String update(@PathVariable Long dossierId, @PathVariable Long amenagementId, @Valid Amenagement amenagement, PersonLdap personLdap, @RequestParam Boolean send, RedirectAttributes redirectAttributes) throws AgapeJpaException {
        amenagementService.update(amenagementId, amenagement);
        if(send) {
            try {
                amenagementService.validationMedecin(amenagementId, personLdap);
                redirectAttributes.addFlashAttribute("message", new Message("success", "L'aménagement a été transmis à l'administration"));
                return "redirect:/dossiers/" + dossierId + "/amenagements/" + amenagementId + "/show";
            } catch (AgapeException | AgapeRuntimeException e) {
                redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
            }
        }
        return "redirect:/dossiers/" + dossierId + "/amenagements/" + amenagementId + "/update";
    }

    private void setModel(Model model) {
        model.addAttribute("typeAmenagements" , TypeAmenagement.values());
        model.addAttribute("tempsMajores" , TempsMajore.values());
        model.addAttribute("typeEpreuves" , TypeEpreuve.values());
        model.addAttribute("classifications", Classification.values());
        model.addAttribute("autorisations", Autorisation.values());
    }
    @DeleteMapping(value = "/{amenagementId}/delete")
    @PreAuthorize("hasRole('ROLE_MEDECIN') or hasRole('ROLE_ADMIN')")
    public String deleteAmenagement(@PathVariable Long dossierId, @PathVariable Long amenagementId, RedirectAttributes redirectAttributes) {
        try {
            amenagementService.softDeleteAmenagement(amenagementId);
            redirectAttributes.addFlashAttribute("message", new Message("success", "L'aménagement a été supprimé"));
        } catch (AgapeException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
        }
        return "redirect:/dossiers/" + dossierId + "/amenagements";
    }

//    @PostMapping("/{amenagementId}/validation-medecin")
//    @PreAuthorize("hasRole('ROLE_MEDECIN') or hasRole('ROLE_ADMIN')")
//    public String validationMedecin(@PathVariable Long dossierId, @PathVariable Long amenagementId, PersonLdap personLdap, RedirectAttributes redirectAttributes) {
//        try {
//            amenagementService.validationMedecin(amenagementId, personLdap);
//            redirectAttributes.addFlashAttribute("message", new Message("success", "L'aménagement a été transmis à l'administration"));
//        } catch (AgapeException | AgapeRuntimeException e) {
//            redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
//        }
//
//        return "redirect:/dossiers/" + dossierId + "/amenagements/" + amenagementId + "/show";
//    }

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
    public ResponseEntity<Void> getAvis(@PathVariable("amenagementId") Long amenagementId, @RequestParam String disposition, HttpServletResponse httpServletResponse) throws IOException, AgapeException {
        httpServletResponse.setContentType("application/pdf");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.setHeader("Content-Disposition", disposition + "; filename=\"certificat_" + amenagementId + ".pdf\"");
        amenagementService.getAvis(amenagementId, httpServletResponse);
        httpServletResponse.flushBuffer();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/add-libelle")
    @PreAuthorize("hasRole('ROLE_MEDECIN') or hasRole('ROLE_ADMIN')")
    public String addLibelle(String newLibelle, Integer previousIndex, RedirectAttributes redirectAttributes) {
        amenagementService.addLibelle(newLibelle, previousIndex);
        redirectAttributes.addFlashAttribute("message", new Message("success", "Libellé ajouté"));
        return "redirect:/";
    }

}