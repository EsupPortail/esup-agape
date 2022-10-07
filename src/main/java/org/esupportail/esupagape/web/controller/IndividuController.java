package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.IndividuService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.esupportail.esupagape.web.viewentity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
    private UtilsService utilsService;

    @Resource
    private IndividuService individuService;

    @Resource
    private DossierService dossierService;

    @GetMapping("{id}")
    public String showRedirect(@PathVariable Long id) {
        return "redirect:/individus/" + id + "/dossiers/" + utilsService.getCurrentYear();
    }

    @GetMapping("{id}/dossiers/{year}")
    public String show(@PathVariable Long id, @PathVariable Integer year, Model model) {
        Individu individu = individuService.getById(id);
        model.addAttribute("individu", individu);
        model.addAttribute("dossiers", dossierService.getAllByIndividu(id));
        model.addAttribute("currentDossier", dossierService.getByYear(id, year));
        Period agePeriod = Period.between(individu.getDateOfBirth(), LocalDate.now());
        int age = agePeriod.getYears();
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

    @RequestMapping(value = "/photo/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getPhoto(@PathVariable("id") Long id) {
        return individuService.getPhoto(id);
    }
}