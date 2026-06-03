package org.esupportail.esupagape.web.controller.referent;

import jakarta.servlet.http.HttpServletResponse;
import org.esupportail.esupagape.dtos.forms.AmenagementUpdateDto;
import org.esupportail.esupagape.entity.*;
import org.esupportail.esupagape.entity.enums.StatusAmenagement;
import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.AmenagementService;
import org.esupportail.esupagape.service.AmenagementWorkflowService;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.TypeLigneAmenagementService;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.esupportail.esupagape.service.utils.UserService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.esupportail.esupagape.repository.UserOthersAffectationsRepository;
import org.esupportail.esupagape.web.viewentity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/referent/amenagements")
public class AmenagementReferentController {

    private final UserService userService;
    private final AmenagementService amenagementService;
    private final AmenagementWorkflowService amenagementWorkflowService;
    private final DossierService dossierService;
    private final TypeLigneAmenagementService typeLigneAmenagementService;
    private final UtilsService utilsService;
    private final UserOthersAffectationsRepository userOthersAffectationsRepository;

    public AmenagementReferentController(UserService userService, AmenagementService amenagementService, AmenagementWorkflowService amenagementWorkflowService, DossierService dossierService, TypeLigneAmenagementService typeLigneAmenagementService, UtilsService utilsService, UserOthersAffectationsRepository userOthersAffectationsRepository) {
        this.userService = userService;
        this.amenagementService = amenagementService;
        this.amenagementWorkflowService = amenagementWorkflowService;
        this.dossierService = dossierService;
        this.typeLigneAmenagementService = typeLigneAmenagementService;
        this.utilsService = utilsService;
        this.userOthersAffectationsRepository = userOthersAffectationsRepository;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Integer yearFilter,
                       @RequestParam(required = false) String fullTextSearch,
                       @RequestParam(required = false) String composanteFilter,
                       @RequestParam(required = false) String campusFilter,
                       @RequestParam(required = false) Boolean viewedFilter,
                       @PageableDefault(size = 10,
            sort = "createDate",
            direction = Sort.Direction.DESC) Pageable pageable,
                       PersonLdap personLdap,
                       Model model) throws AgapeException {
        if (yearFilter == null) {
            yearFilter = utilsService.getCurrentYear();
        }
        Map<String, String> codComposantes = dossierService.getCodComposanteLabels();
        Page<Amenagement> amenagements;
        if (!amenagementWorkflowService.isReferentValidationEnabled()) {
            amenagements = new PageImpl<>(List.of(), pageable, 0);
        } else {
            String codComposante = userService.getComposante(personLdap);
            List<String> codComposanteToDisplay = new ArrayList<>();
            List<String> userCodComposantes = new ArrayList<>(userOthersAffectationsRepository.findByUid(personLdap.getUid()).stream().map(UserOthersAffectations::getCodComposante).toList());
            if(userCodComposantes.contains("ALL_ACCESS")) {
                userCodComposantes.clear();
                userCodComposantes.addAll(codComposantes.keySet());
            }
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
                } else {
                    notViewedByUid = personLdap.getUid();
                }
            }
            StatusAmenagement statusAmenagement = amenagementWorkflowService.getPendingReferentStatus();
            if (StringUtils.hasText(fullTextSearch)) {
                amenagements = amenagementService.getByIndividuNameScol(fullTextSearch, statusAmenagement, codComposanteToDisplay, campusFilter, viewedByUid, notViewedByUid, pageable);
            } else {
                amenagements = amenagementService.getFullTextSearchScol(statusAmenagement, codComposanteToDisplay, campusFilter, viewedByUid, notViewedByUid, yearFilter, pageable);
            }
        }
        model.addAttribute("amenagements", amenagements);
        model.addAttribute("nbAmenagementsToValidateReferent", amenagementService.countToValidateReferent());
        model.addAttribute("codComposantes", codComposantes);
        model.addAttribute("yearFilter", yearFilter);
        model.addAttribute("campuses", dossierService.getAllCampus());
        model.addAttribute("composanteFilter", composanteFilter);
        model.addAttribute("campusFilter", campusFilter);
        model.addAttribute("viewedFilter", viewedFilter);
        model.addAttribute("fullTextSearch", fullTextSearch);
        setModel(model);
        return "referent/amenagements/list";
    }

    @GetMapping("/{amenagementId}")
    public String show(@PathVariable Long amenagementId) {
        return "redirect:/referent/amenagements/" + amenagementId + "/update";
    }

    @GetMapping("/{amenagementId}/update")
    public String update(@PathVariable Long amenagementId, Model model) throws AgapeJpaException, AgapeException {
        setModel(model);
        Amenagement amenagement = amenagementService.getByIdWithLignesAndTypes(amenagementId);
        DossierAmenagement dossierAmenagement = amenagementService.getDossierAmenagementOfCurrentYear(amenagement);
        if (dossierAmenagement == null || dossierAmenagement.getDossier() == null) {
            throw new AgapeJpaException("Aucun dossier courant trouvé pour cet aménagement");
        }

        List<TypeLigneAmenagement> types = typeLigneAmenagementService.getActifsByYear(utilsService.getCurrentYear());
        Map<Long, LigneAmenagement> lignesExistantes = amenagement.getLignesAmenagement().stream()
                .collect(Collectors.toMap(l -> l.getTypeLigneAmenagement().getId(), l -> l, (first, second) -> first));

        AmenagementUpdateDto dto = new AmenagementUpdateDto();
        dto.setTypeAmenagement(amenagement.getTypeAmenagement());
        dto.setEndDate(amenagement.getEndDate());
        dto.setTypeEpreuves(amenagement.getTypeEpreuves());
        dto.setAutresTypeEpreuve(amenagement.getAutresTypeEpreuve());
        dto.setTempsMajore(amenagement.getTempsMajore());
        dto.setAutresTempsMajores(amenagement.getAutresTempsMajores());
        dto.setAutorisation(amenagement.getAutorisation());
        dto.setClassification(amenagement.getClassification());
        types.forEach(type -> {
            org.esupportail.esupagape.dtos.forms.LigneAmenagementDto ligneDto = new org.esupportail.esupagape.dtos.forms.LigneAmenagementDto();
            ligneDto.setTypeLigneAmenagementId(type.getId());
            ligneDto.setLibelle(type.getLibelle());
            ligneDto.setChampLibre(type.isChampLibre());
            LigneAmenagement existante = lignesExistantes.get(type.getId());
            if (existante != null) {
                ligneDto.setId(existante.getId());
                ligneDto.setSelected(true);
                ligneDto.setLibelleLibre(existante.getLibelleLibre());
                ligneDto.setStatut(existante.getStatut());
                ligneDto.setCommentairePrecision(existante.getCommentairePrecision());
                ligneDto.setCommentaireValidation(existante.getCommentaireValidation());
            }
            dto.getLignesAmenagement().add(ligneDto);
        });

        Individu individu = dossierAmenagement.getDossier().getIndividu();
        List<Dossier> dossiers = dossierService.getAllByIndividu(individu.getId()).stream().sorted(Comparator.comparing(Dossier::getYear).reversed()).collect(Collectors.toList());
        model.addAttribute("amenagement", amenagement);
        model.addAttribute("amenagementDto", dto);
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("lastDossier", dossiers.get(0));
        model.addAttribute("currentForm", dossierService.getInfos(individu, utilsService.getCurrentYear()).getLibelleFormation());
        model.addAttribute("currentYear", utilsService.getCurrentYear());
        model.addAttribute("currentDossier", dossierAmenagement.getDossier());
        return "referent/amenagements/update";
    }

    @PutMapping("/{amenagementId}/update")
    public String update(@PathVariable Long amenagementId, @ModelAttribute("amenagementDto") AmenagementUpdateDto dto, RedirectAttributes redirectAttributes) {
        try {
            amenagementService.updateReferentValidation(amenagementId, dto);
            amenagementService.validationReferent(amenagementId);
            redirectAttributes.addFlashAttribute("message", new Message("success", "L'aménagement a bien été transmis à l'administration"));
        } catch (AgapeException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
        }
        return "redirect:/referent/amenagements/" + amenagementId + "/update";
    }

    @PostMapping("/{amenagementId}/viewed")
    public String viewed(@PathVariable Long amenagementId, PersonLdap personLdap, RedirectAttributes redirectAttributes) throws AgapeJpaException {
        amenagementService.viewedByUid(amenagementId, personLdap.getUid());
        redirectAttributes.addFlashAttribute("message", new Message("success", "Aménagement marqué comme lu"));
        return "redirect:/referent/amenagements/" + amenagementId + "/update";
    }

    @PostMapping("/{amenagementId}/not-viewed")
    public String notViewed(@PathVariable Long amenagementId, PersonLdap personLdap, RedirectAttributes redirectAttributes) throws AgapeJpaException {
        amenagementService.notViewedByUid(amenagementId, personLdap.getUid());
        redirectAttributes.addFlashAttribute("message", new Message("success", "Aménagement marqué comme non lu"));
        return "redirect:/referent/amenagements/" + amenagementId + "/update";
    }

    @GetMapping(value = "/{amenagementId}/get-avis", produces = "application/zip")
    @ResponseBody
    public ResponseEntity<Void> getAvis(@PathVariable("amenagementId") Long amenagementId, HttpServletResponse httpServletResponse) throws IOException, AgapeException {
        httpServletResponse.setContentType("application/pdf");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.setHeader("Content-Disposition", "inline; filename=\"avis_" + amenagementId + ".pdf\"");
        amenagementService.getAvis(amenagementId, httpServletResponse);
        httpServletResponse.flushBuffer();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void setModel(Model model) {
        model.addAttribute("typeAmenagements", TypeAmenagement.values());
        model.addAttribute("tempsMajores", TempsMajore.values());
        model.addAttribute("typeEpreuves", TypeEpreuve.values());
        model.addAttribute("classifications", Classification.values());
        model.addAttribute("autorisations", Autorisation.values());
        model.addAttribute("years", utilsService.getYears());
        model.addAttribute("statutsLigneAmenagement", StatutLigneAmenagement.values());
    }
}
