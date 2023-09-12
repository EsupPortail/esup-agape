package org.esupportail.esupagape.web.controller.scolarite;

import jakarta.servlet.http.HttpServletRequest;
import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.repository.AmenagementRepository;
import org.esupportail.esupagape.service.AmenagementService;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.ScolariteService;
import org.esupportail.esupagape.service.ldap.LdapPersonService;
import org.esupportail.esupagape.service.ldap.OrganizationalUnitLdap;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.esupportail.esupagape.service.utils.UserService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/scolarite/amenagements")
public class AmenagementScolariteController {

    private final UserService userService;

    private final UtilsService utilsService;

    private final DossierService dossierService;

    private final ScolariteService scolariteService;

    public AmenagementScolariteController(UserService userService, UtilsService utilsService, ScolariteService scolariteService, DossierService dossierService) {
        this.userService = userService;
        this.utilsService = utilsService;
        this.dossierService = dossierService;
        this.scolariteService = scolariteService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Integer yearFilter,
                       @RequestParam(required = false) String fullTextSearch,
                       @RequestParam(required = false) StatusAmenagement statusAmenagement,
                       @PageableDefault(size = 10,
                               sort = "createDate",
                               direction = Sort.Direction.DESC) Pageable pageable, HttpServletRequest httpServletRequest, PersonLdap personLdap, Model model) throws AgapeException {
        if (yearFilter == null) {
            yearFilter = utilsService.getCurrentYear();
        }
        String codComposante = userService.getComposante(personLdap);
        if (codComposante != null) {
            Page<Amenagement> amenagements = scolariteService.getFullTextSearchScol(statusAmenagement, codComposante, utilsService.getCurrentYear(), pageable);
            if (StringUtils.hasText(fullTextSearch)) {
                amenagements = scolariteService.getByIndividuNameScol(fullTextSearch, codComposante, pageable);
            }
            model.addAttribute("amenagements", amenagements);
            model.addAttribute("codComposante", codComposante);
        } else {
            if (httpServletRequest.isUserInRole("ROLE_ADMIN")) {
                model.addAttribute("amenagements", scolariteService.getFullTextSearchScol(statusAmenagement, null, utilsService.getCurrentYear(), pageable));
            } else {
                model.addAttribute("amenagements", new PageImpl<>(new ArrayList<>()));
            }
        }
        model.addAttribute("yearFilter", yearFilter);
        model.addAttribute("composantes", dossierService.getAllComposantes());
        model.addAttribute("statusAmenagement", StatusAmenagement.values());
        setModel(model);
        return "scolarite/amenagements/list";
    }

    private void setModel(Model model) {
        model.addAttribute("typeAmenagements", TypeAmenagement.values());
        model.addAttribute("tempsMajores", TempsMajore.values());
        model.addAttribute("typeEpreuves", TypeEpreuve.values());
        model.addAttribute("classifications", Classification.values());
        model.addAttribute("autorisations", Autorisation.values());
        model.addAttribute("years", utilsService.getYears());
    }

    @GetMapping("/{amenagementId}")
    public String show(Amenagement amenagement, Dossier dossier, Model model) {
        setModel(model);
        model.addAttribute("dossiers", dossier);
        model.addAttribute("amenagement", amenagement);
        return "scolarite/amenagements/show";
    }

}
