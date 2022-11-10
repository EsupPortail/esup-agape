package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Enquete;
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
        model.addAttribute("enquete", new Enquete());
        model.addAttribute("typefrmns", TypeFrmn.values());
        model.addAttribute("modfrmns", ModFrmn.values());
        model.addAttribute("codfils", CodFil.values());
        model.addAttribute("codfmts", CodFmt.values());
        model.addAttribute("codscos", CodSco.values());
        model.addAttribute("codHds", CodHd.values());
        model.addAttribute("codpfpps", CodPfpp.values());
        model.addAttribute("codpfass", CodPfas.values());
        model.addAttribute("codMeahFs", CodMeahF.values());
        model.addAttribute("codmeaes", CodMeae.values());
        model.addAttribute("codmeaas", CodMeaa.values());
        model.addAttribute("codamls", CodAmL.values());
        return "enquetes/create";
    }

}
