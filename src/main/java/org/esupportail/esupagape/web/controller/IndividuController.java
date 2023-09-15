package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.entity.enums.TypeIndividu;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeRuntimeException;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.IndividuService;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.esupportail.esupagape.web.viewentity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
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

    @GetMapping("/{individuId}/redirect")
    public String showRedirect(@PathVariable Long individuId) {
        List<Dossier> dossiers = dossierService.getAllByIndividu(individuId);
        dossiers.sort(Comparator.comparing(Dossier::getYear).reversed());
        if (!dossiers.isEmpty()) {
            return "redirect:/dossiers/" + dossiers.get(0).getId();
        }
        return "redirect:/dossiers";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("individu", new Individu());
        model.addAttribute("genders", Gender.values());
        return "individus/create";
    }

    @PostMapping("/create")
    public String create(@RequestParam(required = false) String force,
                         @RequestParam(required = false) TypeIndividu typeIndividu,
                         @Valid Individu individu,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes, PersonLdap personLdap) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bindingResult", bindingResult);
            model.addAttribute("individu", new Individu());
            model.addAttribute("genders", Gender.values());
            return "individus/create";
        }
        try {
            Individu individuOk = individuService.create(personLdap, individu, typeIndividu, force);
            logger.info("Nouvel étudiant" + individuOk.getId());
            return "redirect:/individus/" + individuOk.getId() + "/redirect";
        } catch (AgapeRuntimeException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
            return "redirect:/individus/create";
        }
    }

    @PostMapping("/create-by-numetu")
    public String create(@RequestParam(required = false) String force,
                         String numEtu, PersonLdap personLdap,
                         RedirectAttributes redirectAttributes) {
        try {
            Individu individu = new Individu();
            individu.setNumEtu(numEtu);
            Individu individuOk = individuService.create(personLdap, individu, null, force);
            if(individuOk != null) {
                logger.info("Nouvel étudiant" + individuOk.getId());
                return "redirect:/individus/" + individuOk.getId() + "/redirect";
            } else {
                redirectAttributes.addFlashAttribute("message", new Message("danger", "Individu non trouvé dans les référentiels"));
                return "redirect:/dossiers/";
            }
        } catch (AgapeRuntimeException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
            return "redirect:/dossiers";
        }
    }

    @RequestMapping(value = "/photo/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getPhoto(@PathVariable("id") Long id) {
        return individuService.getPhoto(id);
    }

    @PostMapping("/{individuId}/anonymise")
    public String anonymiseIndividu(@PathVariable("individuId") Long individuId) {
        individuService.anonymiseIndividu(individuId);
        return "redirect:/individus/" + individuId + "/redirect";
    }

    @PostMapping("/fusion")
    @ResponseBody
    public void fusionIndividus(@RequestBody List<Long> ids) throws AgapeException {
        if(ids.size() != 2) {
            throw new AgapeRuntimeException("non !!");
        }
        individuService.fusion(ids);
    }

}


