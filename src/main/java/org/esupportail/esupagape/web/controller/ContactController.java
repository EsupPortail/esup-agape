package org.esupportail.esupagape.web.controller;

import org.esupportail.esupagape.entity.Contact;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.service.ContactService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String list(@PathVariable Long id, Model model) {
        List<Contact> contacts = contactService.getContactsByDossier(id);
        model.addAttribute("contacts", contacts);
        return "contacts/list";
    }

    @GetMapping("/show")
    public String showContact(@PathVariable Long id, Model model) {
        List<Contact> contacts = contactService.getContactsByDossier(id);
        model.addAttribute("contacts", contacts);
        return "contacts/show";
    }

    @GetMapping("/update")

    public String updateContact(@PathVariable(value = "id") long id, Model model) throws AgapeException {
        Contact contact = contactService.findById(id);
        model.addAttribute("contact", contact);
        return "contacts/update";
    }
 @PutMapping("/contacts")
    public String update(@Valid Contact contact, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "contacts/update";
        }
        contactService.save(contact);
        return "redirect:/contacts";
 }

    @DeleteMapping(value = "/delete")
    public String deleteDossier(@PathVariable("id") long id) {
        contactService.deleteContact(id);
        return "redirect:/contacts";
    }
}
