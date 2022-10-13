package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Dossier;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.List;

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
        List<Dossier> dossiers = dossierService.getAllByIndividu(id);
        dossiers.sort(Comparator.comparing(Dossier::getYear).reversed());
        if (!dossiers.isEmpty()) {
            return "redirect:/individus/" + id + "/dossiers/" + dossiers.get(0).getYear();
        }
        return "redirect:/individus";
    }

    @GetMapping("{id}/dossiers/{year}")
    public String show(@PathVariable Long id, @PathVariable Integer year, Model model) {
        Individu individu = individuService.getById(id);
        List<Dossier> dossiers = dossierService.getAllByIndividu(id);
        dossiers.sort(Comparator.comparing(Dossier::getYear).reversed());
        Period agePeriod = Period.between(individu.getDateOfBirth(), LocalDate.now());
        int age = agePeriod.getYears();
        model.addAttribute("individu", individu);
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("currentDossier", dossierService.getByYear(id, year));
        model.addAttribute("age", age);
        return "individus/show";
    }

    @GetMapping("{id}/dossiers/{year}/contacts")
    public String showDossierContacts(@PathVariable Long id, @PathVariable Integer year, Model model) {
        Individu individu = individuService.getById(id);
        List<Dossier> dossiers = dossierService.getAllByIndividu(id);
        dossiers.sort(Comparator.comparing(Dossier::getYear).reversed());
        Period agePeriod = Period.between(individu.getDateOfBirth(), LocalDate.now());
        int age = agePeriod.getYears();
        Dossier dossier = dossierService.getByYear(id, year);

        model.addAttribute("individu", individu);
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("currentDossier", dossierService.getByYear(id, year));
        model.addAttribute("age", age);
        model.addAttribute("dossier", dossier);

        return "dossiers/contacts";
    }

    @GetMapping("{id}/dossiers/{year}/enquete")
    public String showDossierEnquete(@PathVariable Long id, @PathVariable Integer year, Model model) {
        Individu individu = individuService.getById(id);
        List<Dossier> dossiers = dossierService.getAllByIndividu(id);
        dossiers.sort(Comparator.comparing(Dossier::getYear).reversed());
        Period agePeriod = Period.between(individu.getDateOfBirth(), LocalDate.now());
        int age = agePeriod.getYears();
        Dossier dossier = dossierService.getByYear(id, year);

        model.addAttribute("individu", individu);
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("currentDossier", dossierService.getByYear(id, year));
        model.addAttribute("age", age);
        model.addAttribute("dossier", dossier);

        return "dossiers/enquete";
    }

    @GetMapping("{id}/dossiers/{year}/parcours")
    public String showDossierParcours(@PathVariable Long id, @PathVariable Integer year, Model model) {
        Individu individu = individuService.getById(id);
        List<Dossier> dossiers = dossierService.getAllByIndividu(id);
        dossiers.sort(Comparator.comparing(Dossier::getYear).reversed());
        Period agePeriod = Period.between(individu.getDateOfBirth(), LocalDate.now());
        int age = agePeriod.getYears();
        Dossier dossier = dossierService.getByYear(id, year);

        model.addAttribute("individu", individu);
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("currentDossier", dossierService.getByYear(id, year));
        model.addAttribute("age", age);
        model.addAttribute("dossier", dossier);

        return "dossiers/parcours";
    }
    @GetMapping("{id}/dossiers/{year}/situation-fiche")
    public String showDossierSituationFiche(@PathVariable Long id, @PathVariable Integer year, Model model) {
        Individu individu = individuService.getById(id);
        List<Dossier> dossiers = dossierService.getAllByIndividu(id);
        dossiers.sort(Comparator.comparing(Dossier::getYear).reversed());
        Period agePeriod = Period.between(individu.getDateOfBirth(), LocalDate.now());
        int age = agePeriod.getYears();
        Dossier dossier = dossierService.getByYear(id, year);

        model.addAttribute("individu", individu);
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("currentDossier", dossierService.getByYear(id, year));
        model.addAttribute("age", age);
        model.addAttribute("dossier", dossier);

        return "dossiers/situation-fiche";
    }
    @GetMapping("{id}/dossiers/{year}/situation-tableau")
    public String showDossierSituationTableau(@PathVariable Long id, @PathVariable Integer year, Model model) {
        Individu individu = individuService.getById(id);
        List<Dossier> dossiers = dossierService.getAllByIndividu(id);
        dossiers.sort(Comparator.comparing(Dossier::getYear).reversed());
        Period agePeriod = Period.between(individu.getDateOfBirth(), LocalDate.now());
        int age = agePeriod.getYears();
        Dossier dossier = dossierService.getByYear(id, year);

        model.addAttribute("individu", individu);
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("currentDossier", dossierService.getByYear(id, year));
        model.addAttribute("age", age);
        model.addAttribute("dossier", dossier);

        return "dossiers/situation-tableau";
    }

    @GetMapping("{id}/dossiers/{year}/aides-humaines")
    public String showDossierAidesHumaines(@PathVariable Long id, @PathVariable Integer year, Model model) {
        Individu individu = individuService.getById(id);
        List<Dossier> dossiers = dossierService.getAllByIndividu(id);
        dossiers.sort(Comparator.comparing(Dossier::getYear).reversed());
        Period agePeriod = Period.between(individu.getDateOfBirth(), LocalDate.now());
        int age = agePeriod.getYears();
        Dossier dossier = dossierService.getByYear(id, year);

        model.addAttribute("individu", individu);
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("currentDossier", dossierService.getByYear(id, year));
        model.addAttribute("age", age);
        model.addAttribute("dossier", dossier);

        return "dossiers/aides-humaines";
    }
    @GetMapping("{id}/dossiers/{year}/aides-materielles")
    public String showDossierAidesMaterielles(@PathVariable Long id, @PathVariable Integer year, Model model) {
        Individu individu = individuService.getById(id);
        List<Dossier> dossiers = dossierService.getAllByIndividu(id);
        dossiers.sort(Comparator.comparing(Dossier::getYear).reversed());
        Period agePeriod = Period.between(individu.getDateOfBirth(), LocalDate.now());
        int age = agePeriod.getYears();
        Dossier dossier = dossierService.getByYear(id, year);

        model.addAttribute("individu", individu);
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("currentDossier", dossierService.getByYear(id, year));
        model.addAttribute("age", age);
        model.addAttribute("dossier", dossier);

        return "dossiers/aides-materielles";
    }
    @GetMapping("{id}/dossiers/{year}/archives-amenagements-examens")
    public String showDossierArchivesAmenagementsExamens(@PathVariable Long id, @PathVariable Integer year, Model model) {
        Individu individu = individuService.getById(id);
        List<Dossier> dossiers = dossierService.getAllByIndividu(id);
        dossiers.sort(Comparator.comparing(Dossier::getYear).reversed());
        Period agePeriod = Period.between(individu.getDateOfBirth(), LocalDate.now());
        int age = agePeriod.getYears();
        Dossier dossier = dossierService.getByYear(id, year);

        model.addAttribute("individu", individu);
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("currentDossier", dossierService.getByYear(id, year));
        model.addAttribute("age", age);
        model.addAttribute("dossier", dossier);

        return "dossiers/archives-amenagements-examens";
    }
    @GetMapping("{id}/dossiers/{year}/suivi-handisup")
    public String showDossierSuiviHandisup(@PathVariable Long id, @PathVariable Integer year, Model model) {
        Individu individu = individuService.getById(id);
        List<Dossier> dossiers = dossierService.getAllByIndividu(id);
        dossiers.sort(Comparator.comparing(Dossier::getYear).reversed());
        Period agePeriod = Period.between(individu.getDateOfBirth(), LocalDate.now());
        int age = agePeriod.getYears();
        Dossier dossier = dossierService.getByYear(id, year);

        model.addAttribute("individu", individu);
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("currentDossier", dossierService.getByYear(id, year));
        model.addAttribute("age", age);
        model.addAttribute("dossier", dossier);

        return "dossiers/suivi-handisup";
    }
    @GetMapping("{id}/dossiers/{year}/amenagements-examens")
    public String showDossierAmenagementsExamen(@PathVariable Long id, @PathVariable Integer year, Model model) {
        Individu individu = individuService.getById(id);
        List<Dossier> dossiers = dossierService.getAllByIndividu(id);
        dossiers.sort(Comparator.comparing(Dossier::getYear).reversed());
        Period agePeriod = Period.between(individu.getDateOfBirth(), LocalDate.now());
        int age = agePeriod.getYears();
        Dossier dossier = dossierService.getByYear(id, year);

        model.addAttribute("individu", individu);
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("currentDossier", dossierService.getByYear(id, year));
        model.addAttribute("age", age);
        model.addAttribute("dossier", dossier);

        return "dossiers/amenagements-examens";
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

    @RequestMapping(value = "/photo/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getPhoto(@PathVariable("id") Long id) {
        return individuService.getPhoto(id);
    }
}