package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.AideHumaine;
import org.esupportail.esupagape.entity.AideMaterielle;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.PeriodeAideHumaine;
import org.esupportail.esupagape.entity.enums.FonctionAidant;
import org.esupportail.esupagape.entity.enums.StatusAideHumaine;
import org.esupportail.esupagape.entity.enums.TypeAideMaterielle;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.AideHumaineService;
import org.esupportail.esupagape.service.AideMaterielleService;
import org.esupportail.esupagape.service.PeriodeAideHumaineService;
import org.esupportail.esupagape.web.viewentity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/dossiers/{id}/aides")
public class AideController {

    private static final Logger logger = LoggerFactory.getLogger(AideController.class);

    private final AideMaterielleService aideMaterielleService;

    private final AideHumaineService aideHumaineService;

    private final PeriodeAideHumaineService periodeAideHumaineService;

    public AideController(AideMaterielleService aideMaterielleService, AideHumaineService aideHumaineService, PeriodeAideHumaineService periodeAideHumaineService) {
        this.aideMaterielleService = aideMaterielleService;
        this.aideHumaineService = aideHumaineService;
        this.periodeAideHumaineService = periodeAideHumaineService;
    }

    @GetMapping
    public String list(Dossier dossier, Model model) {
        setModel(model);
        model.addAttribute("aideMaterielle", new AideMaterielle());
        model.addAttribute("aideHumaine", new AideHumaine());
        model.addAttribute("aideMaterielles", aideMaterielleService.findByDossier(dossier));
        model.addAttribute("aideHumaines", aideHumaineService.findByDossier(dossier));
        model.addAttribute("total", aideMaterielleService.additionCostAideMaterielle(aideMaterielleService.findByDossier(dossier)));

        return "aides/list";
    }

    @PostMapping("/create-aide-materiel")
    public String createAideMaterielle(@Valid AideMaterielle aideMaterielle, BindingResult bindingResult, Model model, Dossier dossier) {
        if (bindingResult.hasErrors()) {
            setModel(model);
            return "aides/list";
        }
        aideMaterielle.setDossier(dossier);
        aideMaterielleService.create(aideMaterielle);
        return "redirect:/dossiers/" + dossier.getId() + "/aides";
    }


    @PostMapping("/create-aide-humaine")
    public String createAideHumaine(@Valid AideHumaine aideHumaine, BindingResult bindingResult, Model model, Dossier dossier) {
        if (bindingResult.hasErrors()) {
            setModel(model);
            return "aides/list";
        }
        aideHumaine.setDossier(dossier);
        AideHumaine savedHumaine = aideHumaineService.create(aideHumaine);
        return "redirect:/dossiers/" + dossier.getId() + "/aides/aides-humaines/" + savedHumaine.getId() + "/update";
    }

    @PutMapping("/aides-materielles/{aideMaterielleId}/update")
    public String updateAideMaterielle(@PathVariable Long aideMaterielleId, @Valid AideMaterielle aideMaterielle, Dossier dossier, RedirectAttributes redirectAttributes) throws AgapeJpaException {
        aideMaterielleService.save(aideMaterielleId, aideMaterielle);
        redirectAttributes.addFlashAttribute("lastEdit", aideMaterielleId);
        return "redirect:/dossiers/" + dossier.getId() + "/aides";
    }

    @DeleteMapping("/aides-materielles/{aideMaterielleId}/delete")
    public String deleteAideMaterielle(@PathVariable Long aideMaterielleId, Dossier dossier) {
        aideMaterielleService.delete(aideMaterielleId);
        return "redirect:/dossiers/" + dossier.getId() + "/aides";
    }

    @DeleteMapping("/aides-humaines/{aideHumaineId}/delete")
    public String deleteAideHumaine(@PathVariable Long aideHumaineId, Dossier dossier) {
        aideHumaineService.delete(aideHumaineId);
        return "redirect:/dossiers/" + dossier.getId() + "/aides";
    }
    @GetMapping("/aides-humaines/{aideHumaineId}/update")
    public String editAideHumaine(@PathVariable Long aideHumaineId, Dossier dossier, Model model) {
        setModel(model);
        model.addAttribute("aideHumaine", aideHumaineService.getById(aideHumaineId));
        model.addAttribute("periodeAideHumaineMap", periodeAideHumaineService.getPeriodeAideHumaineMapByAideHumaine(aideHumaineId));
        model.addAttribute("aideHumainePeriodeSums", periodeAideHumaineService.getAideHumainePeriodeSums(aideHumaineId));
        return "aides/update-aide-humaine";
    }

    @PutMapping("/aides-humaines/{aideHumaineId}/update")
    public String updateAideHumaine(@PathVariable Long aideHumaineId, @Valid AideHumaine aideHumaine, BindingResult bindingResult, Dossier dossier, Model model) {
        aideHumaineService.save(aideHumaineId, aideHumaine);
        return "redirect:/dossiers/" + dossier.getId() + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @PutMapping("/aides-humaines/{aideHumaineId}/update-periode/{month}")
    public String updatePeriode(@PathVariable Long aideHumaineId, @PathVariable Integer month, @Valid PeriodeAideHumaine periodeAideHumaine, BindingResult bindingResult, Dossier dossier, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", "Le format des données saisies est mauvais"));
        } else {
            periodeAideHumaineService.save(aideHumaineId, month, periodeAideHumaine);
        }
        return "redirect:/dossiers/" + dossier.getId() + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @DeleteMapping("/aides-humaines/{aideHumaineId}/delete-periode/{month}")
    public String deletePeriode(@PathVariable Long aideHumaineId, @PathVariable Integer month, Dossier dossier, RedirectAttributes redirectAttributes) {
        try {
            periodeAideHumaineService.delete(aideHumaineId, month);
            redirectAttributes.addFlashAttribute("message", new Message("info", "Période supprimée"));
        } catch (NoSuchElementException e ) {
            logger.debug("no periode to delete " + aideHumaineId + " : " + month);
        }
        return "redirect:/dossiers/" + dossier.getId() + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @PostMapping("/aides-humaines/{aideHumaineId}/add-feuille-heures/{month}")
    public String addFeuilleHeures(@PathVariable Long aideHumaineId, @PathVariable Integer month, @RequestParam("multipartFiles") MultipartFile[] multipartFiles, Dossier dossier) throws AgapeException {
        periodeAideHumaineService.addFeuilleHeures(aideHumaineId, month, multipartFiles, dossier);
        return "redirect:/dossiers/" + dossier.getId() + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @DeleteMapping("/aides-humaines/{aideHumaineId}/delete-feuille-heures/{month}")
    public String deleteFeuilleHeures(@PathVariable Long aideHumaineId, @PathVariable Integer month, Dossier dossier) {
        periodeAideHumaineService.deleteFeuilleHeures(aideHumaineId, month);
        return "redirect:/dossiers/" + dossier.getId() + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @GetMapping("/aides-humaines/{aideHumaineId}/get-feuille-heures/{month}")
    @ResponseBody
    public ResponseEntity<Void> getFeuilleHeures(@PathVariable Long aideHumaineId, @PathVariable Integer month, HttpServletResponse httpServletResponse) throws AgapeIOException {
        periodeAideHumaineService.getFeuilleHeuresHttpResponse(aideHumaineId, month, httpServletResponse);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/aides-humaines/{aideHumaineId}/add-planning/{month}")
    public String addPlanning(@PathVariable Long aideHumaineId, @PathVariable Integer month, @RequestParam("multipartFiles") MultipartFile[] multipartFiles, Dossier dossier) throws AgapeException {
        periodeAideHumaineService.addPlanning(aideHumaineId, month, multipartFiles, dossier);
        return "redirect:/dossiers/" + dossier.getId() + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @DeleteMapping("/aides-humaines/{aideHumaineId}/delete-planning/{month}")
    public String deletePlanning(@PathVariable Long aideHumaineId, @PathVariable Integer month, Dossier dossier) {
        periodeAideHumaineService.deletePlanning(aideHumaineId, month);
        return "redirect:/dossiers/" + dossier.getId() + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @GetMapping("/aides-humaines/{aideHumaineId}/get-planning/{month}")
    @ResponseBody
    public ResponseEntity<Void> getPlanning(@PathVariable Long aideHumaineId, @PathVariable Integer month, HttpServletResponse httpServletResponse) throws AgapeIOException {
        periodeAideHumaineService.getPlanningHttpResponse(aideHumaineId, month, httpServletResponse);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/aides-humaines/{aideHumaineId}/add-fiche")
    public String addFiche(@PathVariable Long aideHumaineId, @RequestParam("multipartFiles") MultipartFile[] multipartFiles, Dossier dossier, RedirectAttributes redirectAttributes) throws AgapeException {
        aideHumaineService.addFiche(aideHumaineId, multipartFiles, dossier);
        redirectAttributes.addFlashAttribute("returnModPJ", true);
        return "redirect:/dossiers/" + dossier.getId() + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @DeleteMapping("/aides-humaines/{aideHumaineId}/delete-fiche")
    public String deleteFiche(@PathVariable Long aideHumaineId, Dossier dossier, RedirectAttributes redirectAttributes) {
        aideHumaineService.deleteFiche(aideHumaineId);
        redirectAttributes.addFlashAttribute("returnModPJ", true);
        return "redirect:/dossiers/" + dossier.getId() + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @GetMapping("/aides-humaines/{aideHumaineId}/get-fiche")
    @ResponseBody
    public ResponseEntity<Void> getFiche(@PathVariable Long aideHumaineId, HttpServletResponse httpServletResponse) throws AgapeIOException {
        aideHumaineService.getFicheHttpResponse(aideHumaineId, httpServletResponse);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/aides-humaines/{aideHumaineId}/add-annexe")
    public String addAnnexe(@PathVariable Long aideHumaineId, @RequestParam("multipartFiles") MultipartFile[] multipartFiles, Dossier dossier, RedirectAttributes redirectAttributes) throws AgapeException {
        aideHumaineService.addAnnexe(aideHumaineId, multipartFiles, dossier);
        redirectAttributes.addFlashAttribute("returnModPJ", true);
        return "redirect:/dossiers/" + dossier.getId() + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @DeleteMapping("/aides-humaines/{aideHumaineId}/delete-annexe")
    public String deleteAnnexe(@PathVariable Long aideHumaineId, Dossier dossier, RedirectAttributes redirectAttributes) {
        aideHumaineService.deleteAnnexe(aideHumaineId);
        redirectAttributes.addFlashAttribute("returnModPJ", true);
        return "redirect:/dossiers/" + dossier.getId() + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @GetMapping("/aides-humaines/{aideHumaineId}/get-annexe")
    @ResponseBody
    public ResponseEntity<Void> getAnnexe(@PathVariable Long aideHumaineId, HttpServletResponse httpServletResponse) throws AgapeIOException {
       aideHumaineService.getAnnexeHttpResponse(aideHumaineId, httpServletResponse);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/aides-humaines/{aideHumaineId}/add-contrat")
    public String addContrat(@PathVariable Long aideHumaineId, @RequestParam("multipartFiles") MultipartFile[] multipartFiles, Dossier dossier, RedirectAttributes redirectAttributes) throws AgapeException {
        aideHumaineService.addContrat(aideHumaineId, multipartFiles, dossier);
        redirectAttributes.addFlashAttribute("returnModPJ", true);
        return "redirect:/dossiers/" + dossier.getId() + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @DeleteMapping("/aides-humaines/{aideHumaineId}/delete-contrat")
    public String deleteContrat(@PathVariable Long aideHumaineId, Dossier dossier, RedirectAttributes redirectAttributes) {
        aideHumaineService.deleteContrat(aideHumaineId);
        redirectAttributes.addFlashAttribute("returnModPJ", true);
        return "redirect:/dossiers/" + dossier.getId() + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @GetMapping("/aides-humaines/{aideHumaineId}/get-contrat")
    @ResponseBody
    public ResponseEntity<Void> getContrat(@PathVariable Long aideHumaineId, HttpServletResponse httpServletResponse) throws AgapeIOException {
        aideHumaineService.getContratHttpResponse(aideHumaineId, httpServletResponse);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void setModel(Model model) {
        model.addAttribute("typeAideMaterielles", TypeAideMaterielle.values());
        model.addAttribute("fonctionAidants", FonctionAidant.values());
        model.addAttribute("statusAideHumaines", StatusAideHumaine.values());
    }

}
