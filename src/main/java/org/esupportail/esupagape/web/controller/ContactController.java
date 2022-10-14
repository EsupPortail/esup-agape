package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.service.ContactService;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.IndividuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/individus/{id}/dossiers/{year}/contacts")
public class ContactController {

    @Resource
    private ContactService contactService;

    @Resource
    private IndividuService individuService;

    @Resource
    private DossierService dossierService;

    @GetMapping
    public String list(@PathVariable Long id, @PathVariable Integer year, Model model) {
        Individu individu = individuService.getById(id);
        List<Dossier> dossiers = dossierService.getAllByIndividu(id);
        dossiers.sort(Comparator.comparing(Dossier::getYear).reversed());
        Period agePeriod = Period.between(individu.getDateOfBirth(), LocalDate.now());
        int age = agePeriod.getYears();
        Dossier currentDossier = dossierService.getCurrent(id);
        model.addAttribute("individu", individu);
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("contacts", contactService.getContactsByDossier(currentDossier.getId()));
        model.addAttribute("currentDossier", currentDossier);
        model.addAttribute("age", age);
        return "contacts/list";
    }

    @GetMapping("/create")
    public String create(@PathVariable Long id, @PathVariable Integer year, Model model) {
        Individu individu = individuService.getById(id);
        List<Dossier> dossiers = dossierService.getAllByIndividu(id);
        return "contacts/create";
    }
}
