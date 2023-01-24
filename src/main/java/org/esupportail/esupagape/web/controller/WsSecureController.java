package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.dtos.SlimSelectDto;
import org.esupportail.esupagape.service.EnqueteService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
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
    public List<SlimSelectDto> getCodFmt(@RequestParam String codFil) {
        if(StringUtils.hasText(codFil)) {
            return enqueteService.getSlimSelectDtosOfCodFmts(codFil);
        }
        return new ArrayList<>();
    }

    @GetMapping("/enquete/cod-sco")
    @ResponseBody
    public List<SlimSelectDto> getCodSco(@RequestParam String codFmt) {
        if(StringUtils.hasText(codFmt)) {
            return enqueteService.getSlimSelectDtosOfCodScos(codFmt);
        }
        return new ArrayList<>();
    }

}
