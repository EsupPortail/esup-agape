package org.esupportail.esupagape.web.controller.referent;

import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.StatusAmenagement;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.AmenagementService;
import org.esupportail.esupagape.service.AmenagementWorkflowService;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.esupportail.esupagape.web.viewentity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/referent/amenagements")
public class AmenagementReferentController {

    private final AmenagementService amenagementService;
    private final AmenagementWorkflowService amenagementWorkflowService;
    private final DossierService dossierService;
    private final UtilsService utilsService;

    public AmenagementReferentController(AmenagementService amenagementService, AmenagementWorkflowService amenagementWorkflowService, DossierService dossierService, UtilsService utilsService) {
        this.amenagementService = amenagementService;
        this.amenagementWorkflowService = amenagementWorkflowService;
        this.dossierService = dossierService;
        this.utilsService = utilsService;
    }

    @GetMapping
    public String list(@PageableDefault(size = 10,
            sort = "createDate",
            direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        Page<Amenagement> amenagements;
        if (amenagementWorkflowService.isReferentValidationEnabled()) {
            amenagements = amenagementService.getFullTextSearch(StatusAmenagement.VALIDE_MEDECIN, null, utilsService.getCurrentYear(), pageable);
        } else {
            amenagements = new PageImpl<>(List.of(), pageable, 0);
        }
        model.addAttribute("amenagements", amenagements);
        model.addAttribute("nbAmenagementsToValidateReferent", amenagementService.countToValidateReferent());
        return "referent/amenagements/list";
    }

    @GetMapping("/{amenagementId}")
    public String show(@PathVariable Long amenagementId, Model model) throws AgapeJpaException {
        Amenagement amenagement = amenagementService.getById(amenagementId);
        Individu individu = amenagement.getDossierAmenagements().stream().toList().get(0).getDossier().getIndividu();
        List<Dossier> dossiers = dossierService.getAllByIndividu(individu.getId()).stream().sorted(Comparator.comparing(Dossier::getYear).reversed()).collect(Collectors.toList());
        model.addAttribute("amenagement", amenagement);
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("lastDossier", dossiers.get(0));
        model.addAttribute("currentYear", utilsService.getCurrentYear());
        return "referent/amenagements/show";
    }

    @PostMapping("/{amenagementId}/validation")
    public String validation(@PathVariable Long amenagementId, RedirectAttributes redirectAttributes) {
        try {
            amenagementService.validationReferent(amenagementId);
            redirectAttributes.addFlashAttribute("message", new Message("success", "L'aménagement a bien été transmis à l'administration"));
        } catch (AgapeException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
        }
        return "redirect:/referent/amenagements/" + amenagementId;
    }
}
