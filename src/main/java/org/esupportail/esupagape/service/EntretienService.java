package org.esupportail.esupagape.service;

import org.esupportail.esupagape.dtos.DocumentDto;
import org.esupportail.esupagape.dtos.EntretienAttachementDto;
import org.esupportail.esupagape.entity.Document;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Entretien;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.exception.AgapeYearException;
import org.esupportail.esupagape.repository.DocumentRepository;
import org.esupportail.esupagape.repository.EntretienRepository;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class EntretienService {

    private final EntretienRepository entretienRepository;

    private final DossierService dossierService;

    private final DocumentRepository documentRepository;

    private final DocumentService documentService;

    private final UtilsService utilsService;

    public EntretienService(EntretienRepository entretienRepository, DossierService dossierService, DocumentRepository documentRepository, DocumentService documentService, UtilsService utilsService) {
        this.entretienRepository = entretienRepository;
        this.dossierService = dossierService;
        this.documentRepository = documentRepository;
        this.documentService = documentService;
        this.utilsService = utilsService;
    }

    @Transactional
    public void save(Entretien entretien){
        Dossier dossier = dossierService.getById(entretien.getDossier().getId());
        if(dossier.getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        //TODO passage automatique en suivi si status importé, a confirmer avec celine martin
        if(dossier.getStatusDossier().equals(StatusDossier.IMPORTE)) {
            dossier.setStatusDossier(StatusDossier.SUIVI);
        }
        entretienRepository.save(entretien);
    }

    @Transactional
    public void create (Entretien entretien, Long idDossier, PersonLdap personLdap) {
       Dossier dossier = dossierService.getById(idDossier);
        if(dossier.getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
       entretien.setDossier(dossier);
       entretien.setInterlocuteur(personLdap.getDisplayName());
        entretienRepository.save(entretien);
    }

    public Entretien getById(Long id) throws AgapeJpaException {
        Optional<Entretien> optionalEntretien = entretienRepository.findById(id);
        if (optionalEntretien.isPresent()) {
            return optionalEntretien.get();
        } else {
            throw new AgapeJpaException("Je n'ai pas trouvé cet entretien");
        }
    }

    @Transactional
    public void deleteEntretien(Long id) {
        Entretien entretien = getById(id);
        if(entretien.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        entretienRepository.deleteById(id);
    }

    @Transactional
    public void addAttachment(Long id, MultipartFile[] multipartFiles) throws AgapeException {
        Entretien entretien = getById(id);
        if(entretien.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        try {
            for(MultipartFile multipartFile : multipartFiles) {
                Document attachment = documentService.createDocument(multipartFile.getInputStream(), multipartFile.getOriginalFilename(), multipartFile.getContentType(), id, Entretien.class.getTypeName(), entretien.getDossier());
                entretien.getAttachments().add(attachment);
            }
        } catch (IOException e) {
            throw new AgapeIOException(e.getMessage());
        }

    }

    @Transactional
    public List<DocumentDto> getAttachements(Long id) {
        return documentRepository.findByParentId(id);
    }

    @Transactional
    public void deleteAttachment(Long id, Long attachmentId) throws AgapeException {
        Entretien entretien = getById(id);
        if(entretien.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        Document attachment = documentService.getById(attachmentId);
        entretien.getAttachments().remove(attachment);
        documentService.delete(attachment);
    }

    @Transactional
    public void update(Long entretienId, Entretien entretien, PersonLdap personLdap) throws AgapeJpaException {
        Entretien entretienToUpdate = getById(entretienId);
        if(entretienToUpdate.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        entretienToUpdate.setDate(entretien.getDate());
        entretienToUpdate.setTypeContact(entretien.getTypeContact());
        entretienToUpdate.setInterlocuteur(personLdap.getDisplayName());
        entretienToUpdate.setCompteRendu(entretien.getCompteRendu());
        entretienRepository.save(entretienToUpdate);
    }

    public Page<Entretien> findByDossier(Long dossierId, Pageable pageable) {
        return entretienRepository.findEntretiensByDossierId(dossierId, pageable);
    }

    public Page<EntretienAttachementDto> findEntretiensWithAttachementsByDossierId(Long dossierId, Pageable pageable) {
        return entretienRepository.findEntretiensWithAttachementsByDossierId(dossierId, pageable);
    }
}
