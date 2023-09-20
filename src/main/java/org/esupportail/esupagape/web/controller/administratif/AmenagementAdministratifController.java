package org.esupportail.esupagape.web.controller.administratif;

import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.AmenagementService;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.esupportail.esupagape.service.utils.UtilsService;
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

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/administratif/amenagements")
public class AmenagementAdministratifController {

    private final AmenagementService amenagementService;

    private final DossierService dossierService;

    private final UtilsService utilsService;

    private final ApplicationProperties applicationProperties;

    public AmenagementAdministratifController(AmenagementService amenagementService, DossierService dossierService, UtilsService utilsService, ApplicationProperties applicationProperties) {
        this.amenagementService = amenagementService;
        this.dossierService = dossierService;
        this.utilsService = utilsService;
        this.applicationProperties = applicationProperties;
    }

    @GetMapping
    public String list(@RequestParam(required = false) StatusAmenagement statusAmenagement,
                       @RequestParam(required = false) String codComposante,
                       @RequestParam(required = false) Integer yearFilter,
                       @RequestParam(required = false) Boolean porte,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) String fullTextSearch,
                       @PageableDefault(size = 10,
                               sort = "createDate",
                               direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        if(porte == null) porte = false;
        if (yearFilter == null) {
            yearFilter = utilsService.getCurrentYear();
        }
        if(statusAmenagement == null) statusAmenagement = StatusAmenagement.VALIDE_MEDECIN;
        if(!StringUtils.hasText(codComposante)) codComposante = null;
        List<StatusAmenagement> statusAmenagements = new ArrayList<>(List.of(StatusAmenagement.values()));
        statusAmenagements.remove(StatusAmenagement.BROUILLON);
        statusAmenagements.remove(StatusAmenagement.SUPPRIME);
        if(!StringUtils.hasText(applicationProperties.getEsupSignatureUrl())) {
            statusAmenagements.remove(StatusAmenagement.ENVOYE);
        }
        Page<Amenagement> amenagements;
         if (porte) {
             amenagements = amenagementService.getFullTextSearchPorte(codComposante, yearFilter, pageable);
             if (StringUtils.hasText(fullTextSearch)) {
                amenagements = new PageImpl<>(amenagements.stream().filter(amenagement -> amenagement.getDossier().getIndividu().getName().equals(fullTextSearch) || amenagement.getDossier().getIndividu().getFirstName().equals(fullTextSearch) || amenagement.getDossier().getIndividu().getNumEtu().equals(fullTextSearch)).toList());
             }
             statusAmenagements.clear();
             statusAmenagements.add(StatusAmenagement.VISE_ADMINISTRATION);
        } else {
            if (StringUtils.hasText(fullTextSearch)) {
                amenagements = amenagementService.getByIndividuNamePortable(fullTextSearch, pageable);
            } else {
                amenagements = amenagementService.getFullTextSearch(statusAmenagement, codComposante, yearFilter, pageable);
            }
        }
        model.addAttribute("statusAmenagements", statusAmenagements);
        model.addAttribute("amenagements", amenagements);
        model.addAttribute("nbAmenagementsToValidate", amenagementService.countToValidate());
        model.addAttribute("nbAmenagementsToPorte", amenagementService.countToPorte());
        model.addAttribute("statusAmenagement", statusAmenagement);
        model.addAttribute("codComposante", codComposante);
        model.addAttribute("porte", porte);
        model.addAttribute("yearFilter", yearFilter);
        model.addAttribute("fullTextSearch", fullTextSearch);
        setModel(model);
        return "administratif/amenagements/list";
    }

    private void setModel(Model model) {
        model.addAttribute("composantes", dossierService.getAllComposantes());
        model.addAttribute("typeAmenagements" , TypeAmenagement.values());
        model.addAttribute("tempsMajores" , TempsMajore.values());
        model.addAttribute("typeEpreuves" , TypeEpreuve.values());
        model.addAttribute("classifications", Classification.values());
        model.addAttribute("autorisations", Autorisation.values());
        model.addAttribute("years", utilsService.getYears());
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
        return "administratif/amenagements/show";
    }

    @PostMapping("/{amenagementId}/porte")
    public String porte(@PathVariable Long amenagementId, PersonLdap personLdap, RedirectAttributes redirectAttributes) {
        try {
            amenagementService.porteAdministration(amenagementId, personLdap);
            redirectAttributes.addFlashAttribute("message", new Message("success", "L'aménagement a été porté pour l'année courante"));
        } catch (AgapeJpaException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", "Portabilité impossible"));

        }
        return "redirect:/administratif/amenagements/" + amenagementId;
    }

    @PostMapping("/{amenagementId}/reject")
    public String reject(@PathVariable Long amenagementId, PersonLdap personLdap, RedirectAttributes redirectAttributes) {
        try {
            amenagementService.rejectAdministration(amenagementId, personLdap);
            redirectAttributes.addFlashAttribute("message", new Message("danger", "L'aménagement a été annulé pour l'année courante"));
        } catch (AgapeJpaException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", "Annulation impossible"));

        }
        return "redirect:/administratif/amenagements/" + amenagementId;
    }

    @PostMapping("/{amenagementId}/validation")
    public String validation(@PathVariable Long amenagementId, PersonLdap personLdap, RedirectAttributes redirectAttributes) {
        try {
            if(StringUtils.hasText(applicationProperties.getEsupSignatureUrl())) {
                redirectAttributes.addFlashAttribute("message", new Message("danger", "La validation se fait par Esup-Signature"));
                return "redirect:/administratif/amenagements/" + amenagementId;
            }
            amenagementService.validationAdministration(amenagementId, personLdap);
            redirectAttributes.addFlashAttribute("message", new Message("success", "L'aménagement a bien été validé"));
        } catch (AgapeException | IOException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
        }
        return "redirect:/administratif/amenagements/" + amenagementId;
    }

    @PostMapping("/{amenagementId}/refus")
    public String refus(@PathVariable Long amenagementId, @RequestParam String motif, PersonLdap personLdap, RedirectAttributes redirectAttributes) {
        try {
            if(StringUtils.hasText(applicationProperties.getEsupSignatureUrl())) {
                redirectAttributes.addFlashAttribute("message", new Message("danger", "Le refus se fait par Esup-Signature"));
                return "redirect:/administratif/amenagements/" + amenagementId;
            }
            amenagementService.refusAdministration(amenagementId, personLdap, motif);
            redirectAttributes.addFlashAttribute("message", new Message("warning", "Le refus a bien été pris en compte"));
        } catch (AgapeException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
        }
        return "redirect:/administratif/amenagements/" + amenagementId;
    }

    @PostMapping("/{amenagementId}/send")
    public String send(@PathVariable Long amenagementId) {
        amenagementService.sendAmenagementToIndividu(amenagementId, true);
        return "redirect:/administratif/amenagements/" + amenagementId;
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
