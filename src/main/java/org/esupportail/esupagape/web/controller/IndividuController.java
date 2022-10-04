package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.exception.AgapeException;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.Period;

@Controller
@RequestMapping("/individus")
public class IndividuController {

    public static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Resource
    private IndividuService individuService;

    @GetMapping()
    public String list(Model model, @PageableDefault(size = 10) Pageable pageable) {
        //model.addAttribute("individu", new Individu());
        model.addAttribute("individus", individuService.getAllIndividus(pageable));
        return "individus/list";
    }

    @GetMapping("{id}")
    public String show(@PathVariable Long id, Model model) {
        Individu individu = individuService.getById(id);
        model.addAttribute("individu", individu);
        Period agePeriod = Period.between(individu.getDateOfBirth(), LocalDate.now());
        int age = agePeriod.getYears();
        logger.info(String.format("Age : %s", age));
        model.addAttribute("age", age);

        return "individus/show";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("individu", new Individu());
        return "individus/create";
    }

    @PostMapping("/create")
    public String create(@RequestParam(required = false) String force,
                         @Valid Individu individu,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "individus/create";
        }

        try {
            Individu individuOk = individuService.create(individu, force);
            logger.info("Nouvel étudiant" + individuOk.getId());
            return "redirect:/individus/" + individuOk.getId();
        } catch (AgapeException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
            return "redirect:/individus/create";
        }
    }

    @RequestMapping("/search")
    public String searchIndividu(Model model,
                                 @Param("name") String name,
                                 @PageableDefault(size = 10)
                                 Pageable pageable) {
        Page<Individu> individus = individuService.searchByName(name, pageable);
        model.addAttribute("individus", individus);
        model.addAttribute("name", name);
        model.addAttribute("individu", new Individu());
        return "individus/list";
    }

}