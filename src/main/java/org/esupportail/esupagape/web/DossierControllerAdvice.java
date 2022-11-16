package org.esupportail.esupagape.web;

import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.IndividuService;
import org.esupportail.esupagape.web.controller.AideController;
import org.esupportail.esupagape.web.controller.EnqueteController;
import org.esupportail.esupagape.web.controller.EntretienController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Comparator;
import java.util.List;

@ControllerAdvice(assignableTypes = { EntretienController.class, EnqueteController.class, AideController.class })
public class DossierControllerAdvice {

    private final DossierService dossierService;

    private final IndividuService individuService;

    public DossierControllerAdvice(DossierService dossierService, IndividuService individuService) {
        this.dossierService = dossierService;
        this.individuService = individuService;
    }

    @ModelAttribute
    public void globalAttributes(@PathVariable Long id, Model model) {
        Dossier dossier = dossierService.getById(id);
        List<Dossier> dossiers = dossierService.getAllByIndividu(dossier.getIndividu().getId());
        dossiers.sort(Comparator.comparing(Dossier::getYear).reversed());
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("currentDossier", dossier);
        model.addAttribute("age", individuService.computeAge(dossier.getIndividu()));
    }

}
