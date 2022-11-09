package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.TypeIndividu;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.EnqueteService;
import org.esupportail.esupagape.service.IndividuService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.invoke.MethodHandles;

@Controller
@RequestMapping("/dossiers/{id}/enquetes")

public class EnqueteController {
    public static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    private final DossierService dossierService;
    private final IndividuService individuService;
    private final UtilsService utilsService;
    private final EnqueteService enqueteService;

    public EnqueteController(DossierService dossierService, IndividuService individuService, UtilsService utilsService, EnqueteService enqueteService) {
        this.dossierService = dossierService;
        this.individuService = individuService;
        this.utilsService = utilsService;
        this.enqueteService = enqueteService;
    }


    /*@GetMapping
    public String list(@PathVariable Long id,Dossier dossier, Model model) throws AgapeJpaException {
        List<Enquete> enquetes = enqueteService.findEntretiensByDossierId(id);
        model.addAttribute("enquetes", enquetes);
        model.addAttribute("dossier", dossier);
        return "enquetes/list";
    }*/

    @GetMapping
    public String list(@RequestParam(required = false) TypeIndividu typeIndividu,
                       @RequestParam(required = false) StatusDossier statusDossier,
                       @RequestParam(required = false) Integer yearFilter, Model model) {
        if (yearFilter == null) {
            yearFilter = utilsService.getCurrentYear();
        }
        model.addAttribute("typeIndividu", typeIndividu);
        model.addAttribute("statusDossier", statusDossier);
        model.addAttribute("yearFilter", yearFilter);
        model.addAttribute("years", utilsService.getYears());
        model.addAttribute("statusDossierList", StatusDossier.values());
        model.addAttribute("typeIndividuList", TypeIndividu.values());
        return "enquetes/list";
    }

}
