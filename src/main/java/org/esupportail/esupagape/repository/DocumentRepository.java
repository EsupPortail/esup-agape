package org.esupportail.esupagape.repository;


import org.esupportail.esupagape.dtos.DocumentDto;
import org.esupportail.esupagape.entity.Document;
import org.esupportail.esupagape.entity.Dossier;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DocumentRepository extends CrudRepository<Document, Long>  {
    List<DocumentDto> findByParentId(Long parentId);
    List<DocumentDto> findByDossier(Dossier dossier);
    List<DocumentDto> findByDossierId(Long id);
}
