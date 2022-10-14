package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactsRepository extends JpaRepository <Contact, Long>{
}
