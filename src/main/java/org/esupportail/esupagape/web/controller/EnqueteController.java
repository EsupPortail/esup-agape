package org.esupportail.esupagape.web.controller;

import jakarta.validation.Valid;
import org.esupportail.esupagape.dtos.forms.EnqueteForm;
import org.esupportail.esupagape.entity.Enquete;
import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.entity.enums.enquete.*;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.EnqueteService;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.esupportail.esupagape.web.viewentity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.invoke.MethodHandles;

@Controller
@RequestMapping("/dossiers/{dossierId}/enquete")
public class EnqueteController {

    public static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EnqueteService enqueteService;

    public EnqueteController(EnqueteService enqueteService) {
        this.enqueteService = enqueteService;
    }

    @GetMapping
    public String show(@PathVariable Long dossierId, PersonLdap personLdap, Model model, RedirectAttributes redirectAttributes) {
        setModel(model);
        Enquete enquete = enqueteService.getAndUpdateByDossierId(dossierId, personLdap.getEduPersonPrincipalName());
        if(enquete != null) {
            model.addAttribute("enquete", enquete);
            return "enquete/update";
        } else {
            redirectAttributes.addFlashAttribute("message", new Message("danger", "Impossible de charger l'enquête, l'individu n'est pas étudiant"));
            return "redirect:/dossiers/" + dossierId;
        }

    }

    @PutMapping("/{enqueteId}/update")
    public String update(@PathVariable Long dossierId, @PathVariable Long enqueteId, @Valid EnqueteForm enqueteForm) throws AgapeJpaException {
        enqueteService.update(enqueteId, enqueteForm, dossierId);
        return "redirect:/dossiers/" + dossierId + "/enquete";
    }

    @PostMapping("/{enqueteId}/finished")
    public String finished(@PathVariable Long dossierId, @PathVariable Long enqueteId) throws AgapeJpaException {
        enqueteService.finished(enqueteId);
        return "redirect:/dossiers/" + dossierId + "/enquete";
    }

    private void setModel(Model model) {
        model.addAttribute("typFrmns", TypFrmn.values());
        model.addAttribute("modFrmns", ModFrmn.values());
        model.addAttribute("codFils", CodFil.values());
        model.addAttribute("codFmts", CodFmt.values());
        model.addAttribute("codScos", CodSco.values());
        model.addAttribute("codHds", CodHd.values());
        model.addAttribute("codPfpps", CodPfpp.values());
        model.addAttribute("codPfass", CodPfas.values());
        model.addAttribute("codMeahFs", CodMeahF.values());
        model.addAttribute("codMeaes", CodMeae.values());
        model.addAttribute("codMeaas", CodMeaa.values());
        model.addAttribute("codamls", CodAmL.values());
        model.addAttribute("libelleCodAmLs", LibelleCodAmL.values());
        model.addAttribute("genders", Gender.values());
        model.addAttribute("enqueteEnumFilFmtSco", CodFmt.values());
    }

}

