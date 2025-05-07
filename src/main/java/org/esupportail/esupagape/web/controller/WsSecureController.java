package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.service.EnqueteService;
import org.esupportail.esupagape.service.utils.slimselect.SlimSelectData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/ws-secure")
public class WsSecureController {

    private final EnqueteService enqueteService;

    public WsSecureController(EnqueteService enqueteService) {
        this.enqueteService = enqueteService;
    }

    @GetMapping("/enquete/cod-fmt")
    @ResponseBody
    public List<SlimSelectData> getCodFmt() {
        return enqueteService.getSlimSelectDtosOfCodFmts();
    }

    @GetMapping("/enquete/cod-sco")
    @ResponseBody
    public List<SlimSelectData> getCodSco() {
        return enqueteService.getSlimSelectDtosOfCodScos();
    }

}
