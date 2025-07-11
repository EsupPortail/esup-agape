package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.service.EnqueteService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ws-secure")
public class WsSecureController {

    private final EnqueteService enqueteService;

    public WsSecureController(EnqueteService enqueteService) {
        this.enqueteService = enqueteService;
    }

}
