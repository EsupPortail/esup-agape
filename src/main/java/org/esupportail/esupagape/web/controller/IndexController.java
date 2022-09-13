package org.esupportail.esupagape.web.controller;

import org.springframework.stereotype.Controller;

@Controller("/")
public class IndexController {

    public String index() {
        return "index";
    }

}
