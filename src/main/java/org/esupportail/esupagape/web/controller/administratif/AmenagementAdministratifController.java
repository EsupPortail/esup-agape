package org.esupportail.esupagape.web.controller.administratif;

import org.esupportail.esupagape.dtos.ComposanteDto;
import org.esupportail.esupagape.entity.enums.StatusAmenagement;
import org.esupportail.esupagape.service.AmenagementService;
import org.esupportail.esupagape.service.DossierService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
                       @RequestParam(required = false) String composanteFilter,
                       @RequestParam(required = false) String codComposante,
                       @PageableDefault(size = 10,
            sort = "createDate",
            direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        model.addAttribute("amenagements", amenagementService.getFullTextSearch(statusAmenagement, codComposante, pageable));
        model.addAttribute("statusAmenagement", statusAmenagement);
        model.addAttribute("composanteFilter", composanteFilter);
        model.addAttribute("codComposante", codComposante);
        setModel(model);
        return "administratif/amenagements/list";
    }

    private void setModel(Model model) {
        model.addAttribute("statusAmenagements", StatusAmenagement.values());
        List<ComposanteDto> toto = dossierService.getAllComposantes();
        model.addAttribute("composantes", toto);
    }

}