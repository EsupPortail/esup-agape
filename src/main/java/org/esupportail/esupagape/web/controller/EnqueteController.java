package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.enums.enquete.CodMeahF;
import org.esupportail.esupagape.entity.enums.enquete.CodPfpp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.invoke.MethodHandles;

@Controller
@RequestMapping("/dossiers/{id}/enquetes")

public class EnqueteController {

    public static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /*@GetMapping
    public String list(@PathVariable Long id,Dossier dossier, Model model) throws AgapeJpaException {
        List<Enquete> enquetes = enqueteService.findEntretiensByDossierId(id);
        model.addAttribute("enquetes", enquetes);
        model.addAttribute("dossier", dossier);
        return "enquetes/list";
    }*/

    @GetMapping
    public String create(Model model) {
        model.addAttribute("codPfpps", CodPfpp.values());
        model.addAttribute("codeMeahFs", CodMeahF.values());
        return "enquetes/create";
    }

}
