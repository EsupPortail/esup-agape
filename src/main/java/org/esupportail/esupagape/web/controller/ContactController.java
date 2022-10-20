package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Contact;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.service.ContactService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/dossiers/{id}/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public String list(@PathVariable Long id,  Model model) {
        List<Contact> contacts = contactService.getContactsByDossier(id);
        model.addAttribute("contacts", contacts);
        return "contacts/list";
    }

    @GetMapping("/{contactId}/show")
    public String showContact(@PathVariable Long contactId, Model model) throws AgapeException {
        Contact contact = contactService.findById(contactId);
        model.addAttribute("contact", contact);
        return "contacts/show";
    }

    @GetMapping("/{contactId}/update")
    public String updateContact(@PathVariable Long contactId, Model model) throws AgapeException {
        Contact contact = contactService.findById(contactId);
        model.addAttribute("contact", contact);
        return "contacts/update";
    }

    @PutMapping("/contacts")
    public String update(@Valid Contact contact, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "contacts/update";
        }
        contactService.save(contact);
        return "redirect:/dossiers/{id}/contacts";
    }

    @DeleteMapping(value = "/{contactId}/delete")
    public String deleteDossier(@PathVariable Long contactId) {
        contactService.deleteContact(contactId);
        return "redirect:/dossiers/{id}/contacts";
    }
}
