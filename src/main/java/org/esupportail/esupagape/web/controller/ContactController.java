package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Contact;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.service.ContactService;
import org.esupportail.esupagape.service.DossierService;
import org.esupportail.esupagape.service.IndividuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dossiers/{id}/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public String list(@PathVariable Long id, Model model) {
        List<Contact> contacts = contactService.getContactsByDossier(id);
        model.addAttribute("contacts", contacts);
        return "contacts/list";
    }

}
