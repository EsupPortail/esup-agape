package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.AideHumaine;
import org.esupportail.esupagape.entity.AideMaterielle;
import org.esupportail.esupagape.entity.Document;
import org.esupportail.esupagape.entity.PeriodeAideHumaine;
import org.esupportail.esupagape.entity.enums.FonctionAidant;
import org.esupportail.esupagape.entity.enums.StatusAideHumaine;
import org.esupportail.esupagape.entity.enums.TypeAideMaterielle;
import org.esupportail.esupagape.entity.enums.TypeDocument;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dossiers/{dossierId}/aides")
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
    public String list(@PathVariable Long dossierId, Model model) {
        setModel(model);
        model.addAttribute("aideMaterielle", new AideMaterielle());
        model.addAttribute("aideHumaine", new AideHumaine());
        model.addAttribute("aideMaterielles", aideMaterielleService.findByDossier(dossierId));
        model.addAttribute("aideHumaines", aideHumaineService.findByDossier(dossierId));
        model.addAttribute("total", aideMaterielleService.additionCostAideMaterielle(aideMaterielleService.findByDossier(dossierId)));

        return "aides/list";
    }

    @PostMapping("/create-aide-materiel")
    public String createAideMaterielle(@PathVariable Long dossierId, @Valid AideMaterielle aideMaterielle, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            setModel(model);
            return "aides/list";
        }
        aideMaterielleService.create(aideMaterielle, dossierId);
        return "redirect:/dossiers/" + dossierId + "/aides";
    }


    @PostMapping("/create-aide-humaine")
    public String createAideHumaine(@PathVariable Long dossierId, @Valid AideHumaine aideHumaine, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            setModel(model);
            return "aides/list";
        }
        AideHumaine savedHumaine = aideHumaineService.create(aideHumaine, dossierId);
        return "redirect:/dossiers/" + dossierId + "/aides/aides-humaines/" + savedHumaine.getId() + "/update";
    }

    @PutMapping("/aides-materielles/{aideMaterielleId}/update")
    public String updateAideMaterielle(@PathVariable Long dossierId, @PathVariable Long aideMaterielleId, @Valid AideMaterielle aideMaterielle, RedirectAttributes redirectAttributes) throws AgapeJpaException {
        aideMaterielleService.save(aideMaterielleId, aideMaterielle);
        redirectAttributes.addFlashAttribute("lastEdit", aideMaterielleId);
        return "redirect:/dossiers/" + dossierId + "/aides";
    }

    @DeleteMapping("/aides-materielles/{aideMaterielleId}/delete")
    public String deleteAideMaterielle(@PathVariable Long dossierId, @PathVariable Long aideMaterielleId) {
        aideMaterielleService.delete(aideMaterielleId);
        return "redirect:/dossiers/" + dossierId + "/aides";
    }

    @DeleteMapping("/aides-humaines/{aideHumaineId}/delete")
    public String deleteAideHumaine(@PathVariable Long dossierId, @PathVariable Long aideHumaineId) {
        aideHumaineService.delete(aideHumaineId);
        return "redirect:/dossiers/" + dossierId + "/aides";
    }

    @GetMapping("/aides-humaines/{aideHumaineId}/update")
    public String editAideHumaine(@PathVariable Long aideHumaineId, Model model) {
        setModel(model);
        AideHumaine aideHumaine = aideHumaineService.getById(aideHumaineId);
        model.addAttribute("aideHumaine", aideHumaine);
        model.addAttribute("periodeAideHumaineMap", periodeAideHumaineService.getPeriodeAideHumaineMapByAideHumaine(aideHumaineId));
        model.addAttribute("aideHumainePeriodeSums", periodeAideHumaineService.getAideHumainePeriodeSums(aideHumaineId));
        List<TypeDocument> typeDocumentAideHumaines = aideHumaineService.getPiecesJointesTypes(aideHumaineId);
        model.addAttribute("typeDocumentAideHumaines", typeDocumentAideHumaines);
        List<TypeDocument> typeDocumentAideHumainesEmpty = new java.util.ArrayList<>(List.of(TypeDocument.values()));
        typeDocumentAideHumainesEmpty.removeAll(typeDocumentAideHumaines);
        model.addAttribute("typeDocumentAideHumainesEmpty", typeDocumentAideHumainesEmpty);
        return "aides/update-aide-humaine";
    }

    @PutMapping("/aides-humaines/{aideHumaineId}/update")
    public String updateAideHumaine(@PathVariable Long dossierId, @PathVariable Long aideHumaineId, @Valid AideHumaine aideHumaine, RedirectAttributes redirectAttributes) {
        try {
            aideHumaineService.save(aideHumaineId, aideHumaine);
        } catch (AgapeException e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
        }
        return "redirect:/dossiers/" + dossierId + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @PutMapping("/aides-humaines/{aideHumaineId}/update-periode/{month}")
    public String updatePeriode(@PathVariable Long dossierId, @PathVariable Long aideHumaineId, @PathVariable Integer month, @Valid PeriodeAideHumaine periodeAideHumaine, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", "Le format des données saisies est mauvais"));
        } else {
            periodeAideHumaineService.save(aideHumaineId, month, periodeAideHumaine);
        }
        return "redirect:/dossiers/" + dossierId + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @DeleteMapping("/aides-humaines/{aideHumaineId}/delete-periode/{month}")
    public String deletePeriode(@PathVariable Long dossierId, @PathVariable Long aideHumaineId, @PathVariable Integer month, RedirectAttributes redirectAttributes) {
        try {
            periodeAideHumaineService.delete(aideHumaineId, month);
            redirectAttributes.addFlashAttribute("message", new Message("info", "Période supprimée"));
        } catch (NoSuchElementException e) {
            logger.debug("no periode to delete " + aideHumaineId + " : " + month);
        }
        return "redirect:/dossiers/" + dossierId + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @PostMapping("/aides-humaines/{aideHumaineId}/add-feuille-heures/{month}")
    public String addFeuilleHeures(@PathVariable Long dossierId, @PathVariable Long aideHumaineId, @PathVariable Integer month, @RequestParam("multipartFiles") MultipartFile[] multipartFiles) throws AgapeException {
        periodeAideHumaineService.addFeuilleHeures(aideHumaineId, month, multipartFiles, dossierId);
        return "redirect:/dossiers/" + dossierId + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @DeleteMapping("/aides-humaines/{aideHumaineId}/delete-feuille-heures/{month}")
    public String deleteFeuilleHeures(@PathVariable Long dossierId, @PathVariable Long aideHumaineId, @PathVariable Integer month) {
        periodeAideHumaineService.deleteFeuilleHeures(aideHumaineId, month);
        return "redirect:/dossiers/" + dossierId + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @GetMapping("/aides-humaines/{aideHumaineId}/get-feuille-heures/{month}")
    @ResponseBody
    public ResponseEntity<Void> getFeuilleHeures(@PathVariable Long aideHumaineId, @PathVariable Integer month, HttpServletResponse httpServletResponse) throws AgapeIOException {
        periodeAideHumaineService.getFeuilleHeuresHttpResponse(aideHumaineId, month, httpServletResponse);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/aides-humaines/{aideHumaineId}/add-planning/{month}")
    public String addPlanning(@PathVariable Long dossierId, @PathVariable Long aideHumaineId, @PathVariable Integer month, @RequestParam("multipartFiles") MultipartFile[] multipartFiles) throws AgapeException {
        periodeAideHumaineService.addPlanning(aideHumaineId, month, multipartFiles, dossierId);
        return "redirect:/dossiers/" + dossierId + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @DeleteMapping("/aides-humaines/{aideHumaineId}/delete-planning/{month}")
    public String deletePlanning(@PathVariable Long dossierId, @PathVariable Long aideHumaineId, @PathVariable Integer month) {
        periodeAideHumaineService.deletePlanning(aideHumaineId, month);
        return "redirect:/dossiers/" + dossierId + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @GetMapping("/aides-humaines/{aideHumaineId}/get-planning/{month}")
    @ResponseBody
    public ResponseEntity<Void> getPlanning(@PathVariable Long aideHumaineId, @PathVariable Integer month, HttpServletResponse httpServletResponse) throws AgapeIOException {
        periodeAideHumaineService.getPlanningHttpResponse(aideHumaineId, month, httpServletResponse);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/aides-humaines/{aideHumaineId}/add-document")
    public String addDocument(@PathVariable Long dossierId, @PathVariable Long aideHumaineId, @RequestParam("multipartFiles") MultipartFile[] multipartFiles, @RequestParam TypeDocument typeDocumentAideHumaine, RedirectAttributes redirectAttributes) throws AgapeException {
        aideHumaineService.addDocument(aideHumaineId, multipartFiles, typeDocumentAideHumaine);
        redirectAttributes.addFlashAttribute("returnModPJ", true);
        return "redirect:/dossiers/" + dossierId + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @DeleteMapping("/aides-humaines/{aideHumaineId}/delete-document")
    public String deleteDocument(@PathVariable Long dossierId, @PathVariable Long aideHumaineId, @RequestParam TypeDocument typeDocumentAideHumaine, RedirectAttributes redirectAttributes) {
        aideHumaineService.deleteDocument(aideHumaineId, typeDocumentAideHumaine);
        redirectAttributes.addFlashAttribute("returnModPJ", true);
        return "redirect:/dossiers/" + dossierId + "/aides/aides-humaines/" + aideHumaineId + "/update";
    }

    @GetMapping("aides-humaines/{aideHumaineId}/get-document")
    @ResponseBody
    public ResponseEntity<Void> getDocument(@PathVariable Long aideHumaineId, @RequestParam TypeDocument typeDocumentAideHumaine, HttpServletResponse httpServletResponse) throws AgapeIOException {
        aideHumaineService.getDocumentHttpResponse(aideHumaineId, httpServletResponse, typeDocumentAideHumaine);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void setModel(Model model) {
        model.addAttribute("typeAideMaterielles", TypeAideMaterielle.values());
        model.addAttribute("fonctionAidants", FonctionAidant.values());
        model.addAttribute("statusAideHumaines", StatusAideHumaine.values());
        model.addAttribute("typeDocumentAideHumaines", TypeDocument.values());
    }
}
