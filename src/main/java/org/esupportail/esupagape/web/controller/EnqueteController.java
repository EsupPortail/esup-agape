package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Enquete;
import org.esupportail.esupagape.entity.enums.Civilite;
import org.esupportail.esupagape.entity.enums.enquete.CodAmL;
import org.esupportail.esupagape.entity.enums.enquete.CodFil;
import org.esupportail.esupagape.entity.enums.enquete.CodFmt;
import org.esupportail.esupagape.entity.enums.enquete.CodHd;
import org.esupportail.esupagape.entity.enums.enquete.CodMeaa;
import org.esupportail.esupagape.entity.enums.enquete.CodMeae;
import org.esupportail.esupagape.entity.enums.enquete.CodMeahF;
import org.esupportail.esupagape.entity.enums.enquete.CodPfas;
import org.esupportail.esupagape.entity.enums.enquete.CodPfpp;
import org.esupportail.esupagape.entity.enums.enquete.CodSco;
import org.esupportail.esupagape.entity.enums.enquete.ModFrmn;
import org.esupportail.esupagape.entity.enums.enquete.TypeFrmn;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.EnqueteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

@Controller
@RequestMapping("/dossiers/{id}/enquetes")

public class EnqueteController {

    public static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EnqueteService enqueteService;

    public EnqueteController(EnqueteService enqueteService) {
        this.enqueteService = enqueteService;
    }

    @GetMapping
    public String show(Dossier dossier, Model model) {
        Enquete enquete = enqueteService.findByDossierId(dossier.getId());
        model.addAttribute("enquete", enquete);
        model.addAttribute("typeFrmns", TypeFrmn.values());
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
        model.addAttribute("civilites", Civilite.values());
        return "enquetes/update";
    }

    @PutMapping("/{enqueteId}/update")
    public String update(@PathVariable Long id, @PathVariable Long enqueteId, @Valid Enquete enquete) throws AgapeJpaException {
        enqueteService.save(enqueteId, enquete);
        return "redirect:/dossiers/" + id + "/enquetes";
    }
}
