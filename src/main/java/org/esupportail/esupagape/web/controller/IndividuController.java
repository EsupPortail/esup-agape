package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.service.IndividuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

@Controller
@RequestMapping("/individus")
public class IndividuController {

    public static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Resource
    private IndividuService individuService;

    @GetMapping()
    public String list(Model model) {
        model.addAttribute("individus", individuService.getAllIndividus());
        return "individus/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("individu", new Individu());
        return "individus/create";
    }

    @PostMapping("/create")
    public String create(@Valid Individu individu, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "students/create";
        }
       individuService.save(individu);
        LOGGER.info("Nouvel étudiant" +  individu.getId());
        return "redirect:/individus";
    }

}
