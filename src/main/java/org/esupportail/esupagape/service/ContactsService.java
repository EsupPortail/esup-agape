package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.Contact;
import org.esupportail.esupagape.repository.ContactsRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

public class ContactsService {

    @Resource
    private ContactsRepository contactsRepository;

    private final List<ContactsService>  contactsServices;

    public ContactsService(List<ContactsService> contactsServices) {
        this.contactsServices = contactsServices;
    }

    public List<Contact> getAllContacts() {
        return contactsRepository.findAll();
    }

    @Transactional
    public void save(Contact contact) {
        contactsRepository.save(contact);
    }

    @Transactional
    public void deleteContact (long id) {
        this.contactsRepository.deleteById(id);
    }

}
