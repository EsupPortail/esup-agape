package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.exception.AgapeRuntimeException;
import org.esupportail.esupagape.repository.IndividuRepository;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.IndividuService;
import org.esupportail.esupagape.web.viewentity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
    private final IndividuRepository individuRepository;

    public IndividuController(IndividuService individuService, DossierService dossierService, IndividuRepository individuRepository) {
        this.individuService = individuService;
        this.dossierService = dossierService;
        this.individuRepository = individuRepository;
    }

    @GetMapping("/{individuId}/redirect")
    public String showRedirect(@PathVariable Long individuId) {
        List<Dossier> dossiers = dossierService.getAllByIndividu(individuId);
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
        } catch (AgapeRuntimeException e) {
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
        } catch (AgapeRuntimeException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
            return "redirect:/individus/create";
        }
    }

    @RequestMapping(value = "/photo/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getPhoto(@PathVariable("id") Long id) {
        return individuService.getPhoto(id);
    }
    @GetMapping("/{id}")
    public String getIndividu(@PathVariable("id") Long id, Model model) {
        Individu individu = individuRepository.findById(id).orElse(null);
        model.addAttribute("individu", individu);
        return "individu";
    }

    @GetMapping("/{individuId}/anonymise")
    public String anonymiseIndividu(@PathVariable("individuId") Long individuId) {
        individuService.anonymiseIndividu(individuId);
        return "redirect:/individus/" + individuId + "/redirect";
    }

}


