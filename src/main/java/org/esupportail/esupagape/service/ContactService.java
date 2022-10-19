package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.Contact;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.repository.ContactRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    @Resource
    private ContactRepository contactRepository;

    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    @Transactional
    public List<Contact> getContactsByDossier(Long dossierId) {
        return contactRepository.findContactByDossierId(dossierId);
    }

    @Transactional
    public void save(Contact contact) {
        contactRepository.save(contact);
    }

    public Contact findById(Long id) throws AgapeException {
        Optional<Contact> optionalContact = contactRepository.findById(id);
        if (optionalContact.isPresent()) {
            return optionalContact.get();
        } else {
            throw  new AgapeException("Je n'ai pas trouvé ce contact");
        }
    }

    @Transactional
    public void deleteContact(long id) {
        this.contactRepository.deleteById(id);
    }

}
