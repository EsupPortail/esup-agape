package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.service.IndividuService;
import org.esupportail.esupagape.web.viewentity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

@Controller
@RequestMapping("/individus")
public class IndividuController {

    public static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Resource
    private IndividuService individuService;

    @GetMapping()
    public String list(Model model, @PageableDefault(size = 10) Pageable pageable) {
        model.addAttribute("individus", individuService.getAllIndividus(pageable));
        return "individus/list";
    }

    @GetMapping("{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("individu", individuService.getById(id));
        return "individus/show";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("individu", new Individu());
        return "individus/create";
    }

    @PostMapping("/create")
    public String create(@Valid Individu individu, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            return "individus/create";
        }
        Individu individuOk = individuService.create(individu);
        if(individuOk != null && individuOk.getId() != null) {
            logger.info("Nouvel étudiant" + individuOk.getId());
            return "redirect:/individus/" + individuOk.getId();
        } else {
            redirectAttributes.addFlashAttribute("message", new Message("danger", "Aucun individu ajouté..."));
            return "redirect:/individus/create";
        }
    }

    @RequestMapping("/search")
    public String searchIndividu(Model model,
                                 @Param("name") String name,
                                 Pageable pageable) {
        Page<Individu> individus = individuService.searchByName(name, pageable);
        model.addAttribute("individus", individus);
        model.addAttribute("name", name);
        return "individus/list";
    }
}
