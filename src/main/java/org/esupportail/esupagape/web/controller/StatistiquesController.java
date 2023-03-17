package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.service.StatistiquesService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/statistiques")
public class StatistiquesController {

    private final StatistiquesService statistiquesService;
    private final UtilsService utilsService;

    public StatistiquesController(StatistiquesService statistiquesService, UtilsService utilsService) {
        this.statistiquesService = statistiquesService;
        this.utilsService = utilsService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Integer yearFilter, Model model) {
        if (yearFilter == null) {
            yearFilter = utilsService.getCurrentYear();
        }
        model.addAttribute("yearFilter", yearFilter);
        model.addAttribute("years", utilsService.getYears());
        model.addAttribute("classificationChart", statistiquesService.getClassificationChart(yearFilter));
        model.addAttribute("composanteChart", statistiquesService.getComposanteChart(yearFilter));
        //model.addAttribute("individuChart", statistiquesService.getIndividuChart());
        model.addAttribute("individuLineChart", statistiquesService.getIndividuLineChart());
        return "statistiques/list";
    }


}
