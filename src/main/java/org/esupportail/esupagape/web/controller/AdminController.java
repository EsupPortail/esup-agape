package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.service.IndividuService;
import org.esupportail.esupagape.web.viewentity.Message;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private IndividuService individuService;

    @GetMapping
    public String index() {
        return "admin/index";
    }

    @GetMapping("/force-sync")
    public String forceSync(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", new Message("success", "L'import est terminé"));
        individuService.importIndividus();
        return "redirect:/admin";
    }

    @GetMapping("/sync-individus")
    public String syncIndividus(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", new Message("success", "La synchro est terminée"));
        individuService.syncAllIndividus();
        return "redirect:/admin";
    }

}
