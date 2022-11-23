package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.IndividuService;
import org.esupportail.esupagape.web.viewentity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/individus")
public class IndividuController {

    public static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final IndividuService individuService;

    private final DossierService dossierService;

    public IndividuController(IndividuService individuService, DossierService dossierService) {
        this.individuService = individuService;
        this.dossierService = dossierService;
    }

    @GetMapping("{id}")
    public String showRedirect(@PathVariable Long id) {
        List<Dossier> dossiers = dossierService.getAllByIndividu(id);
        dossiers.sort(Comparator.comparing(Dossier::getYear).reversed());
        if (!dossiers.isEmpty()) {
            return "redirect:/dossiers/" + dossiers.get(0).getId();
        }
        return "redirect:/individus";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("individu", new Individu());
        model.addAttribute("genders", Gender.values());
        return "individus/create";
    }

    @PostMapping("/create")
    public String create(@RequestParam(required = false) String force,
                         @Valid Individu individu,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bindingResultError", true);
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

    @PostMapping("/create-by-numetu")
    public String create(@RequestParam(required = false) String force,
                         String numEtu,
                         RedirectAttributes redirectAttributes) {
        try {
            Individu individu = new Individu();
            individu.setNumEtu(numEtu);
            Individu individuOk = individuService.create(individu, force);
            logger.info("Nouvel étudiant" + individuOk.getId());
            return "redirect:/individus/" + individuOk.getId();
        } catch (AgapeException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
            return "redirect:/individus/create";
        }
    }

}