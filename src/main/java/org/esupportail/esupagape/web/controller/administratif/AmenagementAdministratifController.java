package org.esupportail.esupagape.web.controller.administratif;

import org.esupportail.esupagape.service.AmenagementService;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/administratif/amenagements")
public class AmenagementAdministratifController {

    private final AmenagementService amenagementService;

    public AmenagementAdministratifController(AmenagementService amenagementService) {
        this.amenagementService = amenagementService;
    }

    @GetMapping
    public String list(@PageableDefault(size = 10,
            sort = "createDate",
            direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        model.addAttribute("amenagements", amenagementService.findAllPaged(pageable));
        return "administratif/amenagements/list";
    }

}