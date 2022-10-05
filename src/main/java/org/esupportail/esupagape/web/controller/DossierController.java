package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.TypeIndividu;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

@Controller
@RequestMapping("/dossiers")
public class DossierController {

    @Resource
    private DossierService dossierService;

    @Resource
    private UtilsService utilsService;
    @GetMapping()
    public String list(@RequestParam(required = false) String fullTextSearch, @RequestParam(required = false) TypeIndividu typeIndividu, @RequestParam(required = false) StatusDossier statusDossier, @RequestParam(required = false) Integer yearFilter, @PageableDefault Pageable pageable, Model model) {
        //model.addAttribute("individu", new Individu());
        if (yearFilter == null) {
            yearFilter = utilsService.getCurrentYear();
        }
        model.addAttribute("fullTextSearch", fullTextSearch);
        model.addAttribute("typeIndividu", typeIndividu);
        model.addAttribute("statusDossier", statusDossier);
        model.addAttribute("dossiers", dossierService.getFullTextSearch(fullTextSearch, typeIndividu, statusDossier, yearFilter, pageable));
        model.addAttribute("yearFilter", yearFilter);
        model.addAttribute("years", dossierService.getYearDistinct());
        return "dossiers/list";
    }

    @GetMapping("{id}")
    public String show(@RequestParam Long id, Model model) {
        model.addAttribute("dossier", dossierService.getId(id));
        return "dossier/show";
    }

}
