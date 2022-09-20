package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.repository.DossierRepository;
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
    private DossierRepository dossierRepository;

    @GetMapping("{id}")
    public String show(@RequestParam Long id, Model model) {
        model.addAttribute("dossier", dossierRepository.findById(id).get());
        return "dossier/show";
    }

}
