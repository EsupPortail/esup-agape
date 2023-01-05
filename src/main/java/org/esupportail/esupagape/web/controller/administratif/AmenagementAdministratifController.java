package org.esupportail.esupagape.web.controller.administratif;

import org.esupportail.esupagape.dtos.ComposanteDto;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.service.AmenagementService;
import org.esupportail.esupagape.service.DossierService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/administratif/amenagements")
public class AmenagementAdministratifController {

    private final AmenagementService amenagementService;

    private final DossierService dossierService;

    public AmenagementAdministratifController(AmenagementService amenagementService, DossierService dossierService) {
        this.amenagementService = amenagementService;
        this.dossierService = dossierService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) StatusAmenagement statusAmenagement,
                       @RequestParam(required = false) String codComposante,
                       @PageableDefault(size = 10,
                               sort = "createDate",
                               direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        if(statusAmenagement == null) statusAmenagement = StatusAmenagement.VALIDER_MEDECIN;
        if(!StringUtils.hasText(codComposante)) codComposante = null;
        model.addAttribute("amenagements", amenagementService.getFullTextSearch(statusAmenagement, codComposante, pageable));
        model.addAttribute("statusAmenagement", statusAmenagement);
        model.addAttribute("codComposante", codComposante);
        setModel(model);
        return "administratif/amenagements/list";
    }

    private void setModel(Model model) {
        List<StatusAmenagement> statusAmenagements = new ArrayList<>(List.of(StatusAmenagement.values()));
        statusAmenagements.remove(StatusAmenagement.BROUILLON);
        statusAmenagements.remove(StatusAmenagement.SUPPRIME);
        model.addAttribute("statusAmenagements", statusAmenagements);
        List<ComposanteDto> toto = dossierService.getAllComposantes();
        model.addAttribute("composantes", toto);
        model.addAttribute("typeAmenagements" , TypeAmenagement.values());
        model.addAttribute("tempsMajores" , TempsMajore.values());
        model.addAttribute("typeEpreuves" , TypeEpreuve.values());
        model.addAttribute("classifications", Classification.values());
        model.addAttribute("autorisations", Autorisation.values());
    }

    @GetMapping("/{amenagementId}")
    public String show(@PathVariable Long amenagementId, Dossier dossier, Model model) {
        setModel(model);
        model.addAttribute("dossiers", dossier);
        model.addAttribute("amenagement",amenagementService.getById(amenagementId));
        return "administratif/amenagements/show";
    }

    @GetMapping(value = "/{amenagementId}/get-avis", produces = "application/zip")
    @ResponseBody
    public ResponseEntity<Void> getAvis(@PathVariable("amenagementId") Long amenagementId, HttpServletResponse httpServletResponse) throws IOException, AgapeException {
        httpServletResponse.setContentType("application/pdf");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.setHeader("Content-Disposition", "inline; filename=\"certificat_" + amenagementId + ".pdf\"");
        amenagementService.getAvis(amenagementId, httpServletResponse);
        httpServletResponse.flushBuffer();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/{amenagementId}/get-certificat", produces = "application/zip")
    @ResponseBody
    public ResponseEntity<Void> getCertificat(@PathVariable("amenagementId") Long amenagementId, HttpServletResponse httpServletResponse) throws IOException, AgapeException {
        httpServletResponse.setContentType("application/pdf");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.setHeader("Content-Disposition", "inline; filename=\"certificat_" + amenagementId + ".pdf\"");
        amenagementService.getCertificat(amenagementId, httpServletResponse);
        httpServletResponse.flushBuffer();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
