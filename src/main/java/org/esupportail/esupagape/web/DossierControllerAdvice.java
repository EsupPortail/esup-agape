package org.esupportail.esupagape.web;

import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.TypeIndividu;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.IndividuService;
import org.esupportail.esupagape.web.controller.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Comparator;
import java.util.List;

@ControllerAdvice(assignableTypes = { DossierController.class, EntretienController.class, EnqueteController.class, AideController.class, AmenagementController.class })
public class DossierControllerAdvice {

    private final DossierService dossierService;

    private final IndividuService individuService;

    public DossierControllerAdvice(DossierService dossierService, IndividuService individuService) {
        this.dossierService = dossierService;
        this.individuService = individuService;
    }

    @ModelAttribute
    public void globalAttributes(@PathVariable(required = false) Long dossierId, Model model) {
        if(dossierId != null) {
            Dossier dossier = dossierService.getById(dossierId);
            List<Dossier> dossiers = dossierService.getAllByIndividu(dossier.getIndividu().getId());
            dossiers.sort(Comparator.comparing(Dossier::getYear).reversed());
            model.addAttribute("currentsDossiers", dossiers);
            model.addAttribute("currentDossier", dossier);
            model.addAttribute("age", individuService.computeAge(dossier.getIndividu()));
        }
        model.addAttribute("statusDossiers", StatusDossier.values());
        model.addAttribute("typeIndividus", TypeIndividu.values());
    }

}
