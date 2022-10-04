package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.service.DossierService;
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

    @GetMapping()
    public String list(Model model, @PageableDefault(size = 10) Pageable pageable) {
        //model.addAttribute("individu", new Individu());
        model.addAttribute("dossiers", dossierService.getAllCurrent(pageable));
        return "dossiers/list";
    }

    @GetMapping("{id}")
    public String show(@RequestParam Long id, Model model) {
        model.addAttribute("dossier", dossierService.getId(id));
        return "dossier/show";
    }

}
