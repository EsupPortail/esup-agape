package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.dtos.DossierCompletCSVDto;
import org.esupportail.esupagape.dtos.DossierIndividuForm;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.entity.enums.enquete.ModFrmn;
import org.esupportail.esupagape.entity.enums.enquete.TypeFrmn;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.CsvExportService;
import org.esupportail.esupagape.service.DocumentService;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.IndividuService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.esupportail.esupagape.web.viewentity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/dossiers")
public class DossierController {

    private final DossierService dossierService;

    private final IndividuService individuService;

    private final UtilsService utilsService;

    private final DocumentService documentService;

    private final CsvExportService csvService;

    public DossierController(DossierService dossierService, IndividuService individuService, UtilsService utilsService, DocumentService documentService, CsvExportService csvService) {
        this.dossierService = dossierService;
        this.individuService = individuService;
        this.utilsService = utilsService;
        this.documentService = documentService;
        this.csvService = csvService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String fullTextSearch,
            @RequestParam(required = false) TypeIndividu typeIndividu,
            @RequestParam(required = false) StatusDossier statusDossier,
            @RequestParam(required = false) StatusDossierAmenagement statusDossierAmenagement,
            @RequestParam(required = false) Integer yearFilter,
            @PageableDefault(sort = "name") Pageable pageable, Model model) {
        if (yearFilter == null) {
            yearFilter = utilsService.getCurrentYear();
        }
        model.addAttribute("fullTextSearch", fullTextSearch);
        model.addAttribute("typeIndividu", typeIndividu);
        model.addAttribute("statusDossier", statusDossier);
        model.addAttribute("statusDossierAmenagement", statusDossierAmenagement);
        model.addAttribute("dossiers", dossierService.getFullTextSearch(fullTextSearch, typeIndividu, statusDossier, statusDossierAmenagement, yearFilter, pageable));
        model.addAttribute("yearFilter", yearFilter);
        model.addAttribute("years", utilsService.getYears());
        model.addAttribute("statusDossierList", StatusDossier.values());
        model.addAttribute("statusDossierAmenagements", StatusDossierAmenagement.values());
        model.addAttribute("typeIndividuList", TypeIndividu.values());
        return "dossiers/list";
    }

    @GetMapping("/{dossierId}")
    public String update(@PathVariable Long dossierId, Model model) {
        Dossier dossier = dossierService.getById(dossierId);
        model.addAttribute("extendedInfos", dossierService.getInfos(dossier));
        model.addAttribute("classifications", Classification.values());
        model.addAttribute("typeSuiviHandisups", TypeSuiviHandisup.values());
        model.addAttribute("rentreeProchaines", RentreeProchaine.values());
        model.addAttribute("tauxs", Taux.values());
        model.addAttribute("mdphs", Mdph.values());
        model.addAttribute("etats", Etat.values());
        model.addAttribute("statusDossierAmenagements", StatusDossierAmenagement.values());
        model.addAttribute("typeFormations", TypeFrmn.values());
        model.addAttribute("modeFormations", ModFrmn.values());
//        model.addAttribute("currentDossier", dossierService.getById(id));
        model.addAttribute("age", individuService.computeAge(dossier.getIndividu()));
        model.addAttribute("dossierIndividuFrom", new DossierIndividuForm());
        model.addAttribute("attachments", dossierService.getAttachements(dossier.getId()));
        return "dossiers/update";
    }

    @GetMapping("/{dossierId}/sync")
    public String sync(@PathVariable Long dossierId, RedirectAttributes redirectAttributes) {
        dossierService.syncDossier(dossierId);
        try {
            individuService.syncIndividu(dossierService.getById(dossierId).getIndividu().getId());
        } catch (AgapeJpaException e) {
            throw new RuntimeException(e);
        }
        redirectAttributes.addFlashAttribute("message", new Message("success", "Synchonisation effectuée"));
        return "redirect:/dossiers/" + dossierId;
    }

    @PutMapping("/{dossierId}")
    public String update(@PathVariable Long dossierId, @Valid Dossier dossier) {
        dossierService.update(dossierId, dossier);
        return "redirect:/dossiers/" + dossierId;
    }

    @PutMapping("/{dossierId}/update-dossier-individu")
    public String update(@PathVariable Long dossierId, @Valid DossierIndividuForm dossierIndividuForm) {
        dossierService.updateDossierIndividu(dossierId, dossierIndividuForm);
        return "redirect:/dossiers/" + dossierId;
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/delete-dossier/{dossierId}")
    public String deleteDossier(@PathVariable Long dossierId) {
        dossierService.deleteDossier(dossierId);
        return "redirect:/dossiers";
    }

    @PostMapping("/{dossierId}/add-attachments")
    public String addAttachments(
            @PathVariable Long dossierId,
            @RequestParam("multipartFiles") MultipartFile[] multipartFiles,
            RedirectAttributes redirectAttributes) throws AgapeException {
        dossierService.addAttachment(dossierId, multipartFiles);
        redirectAttributes.addFlashAttribute("returnModPJ", true);
        return "redirect:/dossiers/" + dossierId;
    }

    @GetMapping(value = "/{dossierId}/get-attachment/{attachmentId}")
    @ResponseBody
    public ResponseEntity<Void> getLastFileFromSignRequest(
            @PathVariable("attachmentId") Long attachmentId,
            HttpServletResponse httpServletResponse) throws AgapeIOException {
        documentService.getDocumentHttpResponse(attachmentId, httpServletResponse);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{dossierId}/delete-attachment/{attachmentId}")
    public String getLastFileFromSignRequest(
            @PathVariable Long dossierId,
            @PathVariable("attachmentId") Long attachmentId,
            RedirectAttributes redirectAttributes) throws AgapeException {
        dossierService.deleteAttachment(dossierId, attachmentId);
        redirectAttributes.addFlashAttribute("returnModPJ", true);
        return "redirect:/dossiers/" + dossierId;
    }

    @GetMapping("/export-to-csv")
    public void exportToCsv(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) TypeIndividu typeIndividu,
            @RequestParam(required = false) StatusDossier statusDossier,
            @RequestParam(required = false) StatusDossierAmenagement statusDossierAmenagement,
            @RequestParam(required = false) String formAddress,
            HttpServletResponse response) {
        List<DossierCompletCSVDto> dossierCompletCSVDtos = dossierService.getCsvDossier(year, typeIndividu, statusDossier, statusDossierAmenagement, formAddress);
        String fileName = "dossier-complet.csv";
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
//        csvService.writeExcelHackToCsv(response);
        try (Writer writer = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8)) {
            csvService.writeDossierCompletToCsv(dossierCompletCSVDtos, writer);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/export-all-email-to-csv")
    public void exportAllEmailToCsv(
            @RequestParam(required = false) Integer year,
            HttpServletResponse response) {
        List<DossierCompletCSVDto> dossierCompletCSVDtos = dossierService.findEmailEtuByYearForCSV(year);
        String fileName = "emails.csv";
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        try (Writer writer = response.getWriter()) {
            csvService.writeEmailsToCsv(dossierCompletCSVDtos, writer);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
