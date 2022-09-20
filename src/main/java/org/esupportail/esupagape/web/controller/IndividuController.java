package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.service.IndividuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping("/individus")
public class IndividuController {

    @Resource
    private IndividuService individuService;

    @GetMapping
    public String index() {
        return "individus/list";
    }

}
