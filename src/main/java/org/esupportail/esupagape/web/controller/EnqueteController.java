package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.dtos.EnqueteForm;
import org.esupportail.esupagape.entity.Enquete;
import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.entity.enums.enquete.CodAmL;
import org.esupportail.esupagape.entity.enums.enquete.CodHd;
import org.esupportail.esupagape.entity.enums.enquete.CodMeaa;
import org.esupportail.esupagape.entity.enums.enquete.CodMeae;
import org.esupportail.esupagape.entity.enums.enquete.CodMeahF;
import org.esupportail.esupagape.entity.enums.enquete.CodPfas;
import org.esupportail.esupagape.entity.enums.enquete.CodPfpp;
import org.esupportail.esupagape.entity.enums.enquete.LibelleCodAmL;
import org.esupportail.esupagape.entity.enums.enquete.LibelleCodMeahF;
import org.esupportail.esupagape.entity.enums.enquete.ModFrmn;
import org.esupportail.esupagape.entity.enums.enquete.TypeFrmn;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.service.CsvImportService;
import org.esupportail.esupagape.service.EnqueteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dossiers/{dossierId}/enquete")
public class EnqueteController {

    public static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EnqueteService enqueteService;

    private final CsvImportService csvImportService;

    public EnqueteController(EnqueteService enqueteService, CsvImportService csvImportService) {
        this.enqueteService = enqueteService;

        this.csvImportService = csvImportService;
    }

    @GetMapping
    public String show(@PathVariable Long dossierId, Model model) {
        setModel(model);
        Enquete enquete = enqueteService.getAndUpdateByDossierId(dossierId);
        model.addAttribute("enquete", enquete);
        return "enquete/update";
    }

    @PutMapping("/{enqueteId}/update")
    public String update(@PathVariable Long dossierId, @PathVariable Long enqueteId, @Valid EnqueteForm enqueteForm) throws AgapeJpaException {
        enqueteService.update(enqueteId, enqueteForm, dossierId);
        return "redirect:/dossiers/" + dossierId + "/enquete";
    }

    private void setModel(Model model) {
        model.addAttribute("typeFrmns", TypeFrmn.values());
        model.addAttribute("modFrmns", ModFrmn.values());
        model.addAttribute("codFils", enqueteService.getCodFils());
        model.addAttribute("codHds", CodHd.values());
        model.addAttribute("codPfpps", CodPfpp.values());
        model.addAttribute("codPfass", CodPfas.values());
        model.addAttribute("codMeahFs", CodMeahF.values());
        model.addAttribute("libelleCodMeahFs", LibelleCodMeahF.values());
        model.addAttribute("codMeaes", CodMeae.values());
        model.addAttribute("codMeaaStructures", Arrays.stream(CodMeaa.values()).filter(codMeaa -> codMeaa.equals(CodMeaa.AA1) || codMeaa.equals(CodMeaa.AA2)).collect(Collectors.toList()));
        model.addAttribute("codMeaas", Arrays.stream(CodMeaa.values()).filter(codMeaa -> !codMeaa.equals(CodMeaa.AA1) && !codMeaa.equals(CodMeaa.AA2)).collect(Collectors.toList()));
        model.addAttribute("codamls", CodAmL.values());
        model.addAttribute("libelleCodAmLs", LibelleCodAmL.values());
        model.addAttribute("genders", Gender.values());
    }

}

