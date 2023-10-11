package org.esupportail.esupagape.web.controller.scolarite;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.UserOthersAffectations;
import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.repository.UserOthersAffectationsRepository;
import org.esupportail.esupagape.service.AmenagementService;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.ldap.LdapOrganizationalUnitService;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.esupportail.esupagape.service.utils.UserService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.esupportail.esupagape.web.viewentity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/scolarite/amenagements")
public class AmenagementScolariteController {

    private final UserService userService;

    private final UtilsService utilsService;

    private final DossierService dossierService;

    private final AmenagementService amenagementService;

    private final LdapOrganizationalUnitService ldapOrganizationalUnitService;

    private final UserOthersAffectationsRepository userOthersAffectationsRepository;

    public AmenagementScolariteController(UserService userService, UtilsService utilsService, DossierService dossierService, AmenagementService amenagementService, LdapOrganizationalUnitService ldapOrganizationalUnitService, UserOthersAffectationsRepository userOthersAffectationsRepository) {
        this.userService = userService;
        this.utilsService = utilsService;
        this.dossierService = dossierService;
        this.amenagementService = amenagementService;
        this.ldapOrganizationalUnitService = ldapOrganizationalUnitService;
        this.userOthersAffectationsRepository = userOthersAffectationsRepository;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Integer yearFilter,
                       @RequestParam(required = false) String fullTextSearch,
                       @RequestParam(required = false) String composanteFilter,
                       @RequestParam(required = false) String campusFilter,
                       @RequestParam(required = false) Boolean viewedFilter,
                       @RequestParam(required = false) StatusAmenagement statusAmenagement,
                       @PageableDefault(size = 10,
                               sort = "createDate",
                               direction = Sort.Direction.DESC) Pageable pageable, HttpServletRequest httpServletRequest, PersonLdap personLdap, Model model) throws AgapeException {
        if (yearFilter == null) {
            yearFilter = utilsService.getCurrentYear();
        }
        String codComposante = userService.getComposante(personLdap);
        Page<Amenagement> amenagements;
        List<String> codComposanteToDisplay = new ArrayList<>();
        List<String> userCodComposantes = new ArrayList<>(userOthersAffectationsRepository.findByUid(personLdap.getUid()).stream().map(UserOthersAffectations::getCodComposante).toList());
        if (codComposante != null) {
            userCodComposantes.add(codComposante);
        }
        if (StringUtils.hasText(composanteFilter)) {
            codComposanteToDisplay.add(composanteFilter);
        } else {
            codComposanteToDisplay.addAll(userCodComposantes);
        }
        String viewedByUid = null;
        String notViewedByUid = null;
        if (viewedFilter != null) {
            if (viewedFilter) {
                viewedByUid = personLdap.getUid();
            } else  {
                notViewedByUid = personLdap.getUid();
            }
        }
        if (StringUtils.hasText(fullTextSearch)) {
            amenagements = amenagementService.getByIndividuNameScol(fullTextSearch, StatusAmenagement.VISE_ADMINISTRATION, codComposanteToDisplay, campusFilter, viewedByUid, notViewedByUid, pageable);
        } else {
            amenagements = amenagementService.getFullTextSearchScol(statusAmenagement, codComposanteToDisplay, campusFilter, viewedByUid, notViewedByUid, utilsService.getCurrentYear(), pageable);
        }
        model.addAttribute("amenagements", amenagements);
        Map<String, String> composantes = new HashMap<>();
        for(String userCodComposante : userCodComposantes) {
            composantes.put(userCodComposante, ldapOrganizationalUnitService.getOrganizationalUnitLdap(userCodComposante).getDescription());
        }
        model.addAttribute("composantes", composantes);
        model.addAttribute("yearFilter", yearFilter);
        model.addAttribute("statusAmenagement", StatusAmenagement.values());
        model.addAttribute("campuses", dossierService.getAllCampus());
        model.addAttribute("composanteFilter", composanteFilter);
        model.addAttribute("campusFilter", campusFilter);
        model.addAttribute("viewedFilter", viewedFilter);
        model.addAttribute("fullTextSearch", fullTextSearch);
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

    @PostMapping("/{amenagementId}/viewed")
    public String viewed(@PathVariable Long amenagementId, PersonLdap personLdap, RedirectAttributes redirectAttributes) throws AgapeJpaException {
        amenagementService.viewedByUid(amenagementId, personLdap.getUid());
        redirectAttributes.addFlashAttribute("message", new Message("success", "Aménagement marqué comme lu"));
        return "redirect:/scolarite/amenagements/" + amenagementId;
    }

    @PostMapping("/{amenagementId}/not-viewed")
    public String notViewed(@PathVariable Long amenagementId, PersonLdap personLdap, RedirectAttributes redirectAttributes) throws AgapeJpaException {
        amenagementService.notViewedByUid(amenagementId, personLdap.getUid());
        redirectAttributes.addFlashAttribute("message", new Message("success", "Aménagement marqué comme non lu"));
        return "redirect:/scolarite/amenagements/" + amenagementId;
    }

    @GetMapping("/{amenagementId}")
    public String show(@PathVariable Long amenagementId, Model model) throws AgapeJpaException, AgapeException {
        setModel(model);
        Amenagement amenagement = amenagementService.getById(amenagementId);
        model.addAttribute("amenagement", amenagement);
        List<Dossier> dossiers = dossierService.getAllByIndividu(amenagement.getDossier().getIndividu().getId()).stream().sorted(Comparator.comparing(Dossier::getYear).reversed()).collect(Collectors.toList());
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("currentForm", dossierService.getInfos(amenagement.getDossier().getIndividu(), utilsService.getCurrentYear()).getLibelleFormation());
        model.addAttribute("lastDossier", dossiers.get(0));
        Dossier dossier;
        try {
            dossier = dossierService.getCurrent(amenagement.getDossier().getIndividu().getId());
        } catch (AgapeJpaException e) {
            dossier = null;
        }
        model.addAttribute("currentDossier", dossier);
        return "scolarite/amenagements/show";
    }

    @GetMapping(value = "/{amenagementId}/get-certificat", produces = "application/zip")
    @ResponseBody
    public ResponseEntity<Void> getCertificat(@PathVariable("amenagementId") Long amenagementId, @RequestParam(required = false) String type, HttpServletResponse httpServletResponse) throws IOException, AgapeException {
        httpServletResponse.setContentType("application/pdf");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        if(type != null && type.equals("download")) {
            httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"certificat_" + amenagementId + ".pdf\"");
        } else {
            httpServletResponse.setHeader("Content-Disposition", "inline; filename=\"certificat_" + amenagementId + ".pdf\"");
        }
        amenagementService.getCertificat(amenagementId, httpServletResponse);
        httpServletResponse.flushBuffer();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
