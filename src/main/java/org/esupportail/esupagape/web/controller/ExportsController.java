package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.service.ExportService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.esupportail.esupagape.web.viewentity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/exports")
public class ExportsController {

    private static final Logger logger = LoggerFactory.getLogger(ExportsController.class);

    private final ExportService exportService;

    private final UtilsService utilsService;

    public ExportsController(ExportService exportService, UtilsService utilsService) {
        this.exportService = exportService;
        this.utilsService = utilsService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) Integer yearFilter, Model model) {
        if (yearFilter == null) {
            yearFilter = utilsService.getCurrentYear();
        }
        model.addAttribute("yearFilter", yearFilter);
        model.addAttribute("years", utilsService.getYears());
        return "exports/list";
    }

    @GetMapping("/export-to-csv")
    public void exportToCsv(
            @RequestParam(required = false) Integer year,
            HttpServletResponse response) {
        String fileName = "dossier-complet.csv";
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
        try (Writer writer = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8)) {
            exportService.getCsvDossier(year, writer);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/export-enquete-to-csv")
    public ResponseEntity<Void> exportEnqueteToCsv(
            @RequestParam(required = false) Integer year,
            HttpServletResponse response, RedirectAttributes redirectAttributes) {
        String fileName = "enquetes.csv";
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        try {
            Writer writer = response.getWriter();
            exportService.findEnqueteByYearForCSV(year, writer);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", new Message("danger", e.getMessage()));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/exports");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }

}
