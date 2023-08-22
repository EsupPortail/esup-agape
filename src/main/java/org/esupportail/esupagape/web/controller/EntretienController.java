package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Entretien;
import org.esupportail.esupagape.entity.enums.TypeContact;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.EntretienService;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.esupportail.esupagape.web.viewentity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/dossiers/{dossierId}/entretiens")
public class EntretienController {

    private final EntretienService entretienService;

    public EntretienController(EntretienService entretienService) {
        this.entretienService = entretienService;
    }

    @GetMapping
    public String list(@PathVariable Long dossierId, @PageableDefault(
            sort = "date",
            direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        Page<Entretien> entretiens = entretienService.findByDossier(dossierId, pageable);
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

    @PostMapping("/create")
    public String create(@PathVariable Long dossierId, @Valid Entretien entretien, BindingResult bindingResult, PersonLdap personLdap, Model model) {
        if (bindingResult.hasErrors()) {
            setModel(model, dossierId);
            model.addAttribute("typeContacts", Arrays.asList(TypeContact.values()));
            return "entretiens/list";
        }
        entretienService.create(entretien, dossierId, personLdap);
        return "redirect:/dossiers/" + dossierId + "/entretiens";
    }

    private void setModel(Model model, Long dossierId) {
        model.addAttribute("entretiens", entretienService.findByDossier(dossierId, Pageable.unpaged()));
    }

    @GetMapping("/{entretienId}/update")
    public String updateEntretien(@PathVariable Long entretienId, Model model) throws AgapeException {
        Entretien entretien = entretienService.getById(entretienId);
        model.addAttribute("entretien", entretien);
        model.addAttribute("typeContacts", TypeContact.values());
        return "entretiens/update";
    }

    @PutMapping("/{entretienId}/update")
    public String update(@PathVariable Long dossierId, @PathVariable Long entretienId, @Valid Entretien entretien, PersonLdap personLdap, RedirectAttributes redirectAttributes) throws AgapeJpaException {
        if(StringUtils.hasText(entretien.getCompteRendu())) {
            entretienService.update(entretienId, entretien, personLdap);
        } else {
            redirectAttributes.addFlashAttribute("message", new Message("danger", "Il manque un champ"));
        }
        return "redirect:/dossiers/" + dossierId + "/entretiens/" + entretienId + "/update";
    }

    @DeleteMapping(value = "/{entretienId}/delete")
    public String deleteDossier(@PathVariable Long dossierId, @PathVariable Long entretienId) {
        entretienService.deleteEntretien(entretienId);
        return "redirect:/dossiers/" + dossierId + "/entretiens";
    }
}
