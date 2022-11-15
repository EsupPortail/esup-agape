package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.IndividuService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/dossiers")
public class DossierController {

    private final DossierService dossierService;

    private final IndividuService individuService;

    private final UtilsService utilsService;

    public DossierController(DossierService dossierService,
                             IndividuService individuService, UtilsService utilsService) {
        this.dossierService = dossierService;
        this.individuService = individuService;
        this.utilsService = utilsService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String fullTextSearch,
                       @RequestParam(required = false) TypeIndividu typeIndividu,
                       @RequestParam(required = false) StatusDossier statusDossier,
                       @RequestParam(required = false) Integer yearFilter,
                       @PageableDefault(sort = "name") Pageable pageable, Model model) {
        if (yearFilter == null) {
            yearFilter = utilsService.getCurrentYear();
        }
        model.addAttribute("fullTextSearch", fullTextSearch);
        model.addAttribute("typeIndividu", typeIndividu);
        model.addAttribute("statusDossier", statusDossier);
        model.addAttribute("dossiers", dossierService.getFullTextSearch(fullTextSearch, typeIndividu, statusDossier, yearFilter, pageable));
        model.addAttribute("yearFilter", yearFilter);
        model.addAttribute("years", utilsService.getYears());
        model.addAttribute("statusDossierList", StatusDossier.values());
        model.addAttribute("typeIndividuList", TypeIndividu.values());

        return "dossiers/list";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        dossierService.syncDossier(id);
        Dossier dossier = dossierService.getById(id);
        List<Dossier> dossiers = dossierService.getAllByIndividu(dossier.getIndividu().getId());
        dossiers.sort(Comparator.comparing(Dossier::getYear).reversed());
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("extendedInfos", dossierService.getInfos(dossier));
        model.addAttribute("classifications", Classification.values());
        model.addAttribute("typeSuiviHandisups", TypeSuiviHandisup.values());
        model.addAttribute("rentreeProchaines", RentreeProchaine.values());
        model.addAttribute("mdphs", Mdph.values());
        model.addAttribute("etats", Etat.values());
        model.addAttribute("currentDossier", dossierService.getById(id));
        model.addAttribute("age", individuService.computeAge(dossier.getIndividu()));
        return "dossiers/show";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @Valid Dossier dossier, Model model) {
        dossierService.update(id, dossier);
        return "redirect:/dossiers/" + id;
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/delete-dossier/{id}")
    public String deleteDossier(@PathVariable("id") long id) {
        dossierService.deleteDossier(id);
        return "redirect:/dossiers";
    }

    @RequestMapping(value = "/photo/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getPhoto(@PathVariable("id") Long id) {
        return individuService.getPhoto(id);
    }

}
