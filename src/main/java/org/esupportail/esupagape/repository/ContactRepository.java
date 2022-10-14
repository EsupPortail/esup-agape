package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository <Contact, Long> {

    List<Contact> findContactByDossierId(Long dossierId);

}
