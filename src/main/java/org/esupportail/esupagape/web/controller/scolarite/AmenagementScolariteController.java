package org.esupportail.esupagape.web.controller.scolarite;

import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.enums.StatusAmenagement;
import org.esupportail.esupagape.service.AmenagementService;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/scolarite/amenagements")
public class AmenagementScolariteController {

    private final AmenagementService amenagementService;
    private final UtilsService utilsService;

    public AmenagementScolariteController(AmenagementService amenagementService, UtilsService utilsService) {
        this.amenagementService = amenagementService;
        this.utilsService = utilsService;
    }

    @GetMapping
    public String list(@PageableDefault(size = 10,
                               sort = "createDate",
                               direction = Sort.Direction.DESC) Pageable pageable, PersonLdap personLdap, Model model) {
        model.addAttribute("amenagements", amenagementService.getFullTextSearch(StatusAmenagement.VISE_ADMINISTRATION, "IUR", utilsService.getCurrentYear(), pageable));
        setModel(model);
        return "scolarite/amenagements/list";
    }

    private void setModel(Model model) {
    }

    @GetMapping("/{amenagementId}")
    public String show(Amenagement amenagement, Dossier dossier, Model model) {
        setModel(model);
        model.addAttribute("dossiers", dossier);
        model.addAttribute("amenagement", amenagement);
        return "scolarite/amenagements/show";
    }

}
