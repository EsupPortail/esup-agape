package org.esupportail.esupagape.service;

import jakarta.servlet.http.HttpServletResponse;
import org.esupportail.esupagape.entity.Aidant;
import org.esupportail.esupagape.entity.AideHumaine;
import org.esupportail.esupagape.entity.Document;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.TypeDocument;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.esupportail.esupagape.exception.AgapeRuntimeException;
import org.esupportail.esupagape.exception.AgapeYearException;
import org.esupportail.esupagape.repository.AidantRepository;
import org.esupportail.esupagape.repository.AideHumaineRepository;
import org.esupportail.esupagape.repository.DocumentRepository;
import org.esupportail.esupagape.service.ldap.LdapPersonService;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AideHumaineService {

    private final DocumentRepository documentRepository;

    private final AideHumaineRepository aideHumaineRepository;

    private final AidantRepository aidantRepository;

    private final DocumentService documentService;

    private final UtilsService utilsService;

    private final DossierService dossierService;

    private final SyncService syncService;
    private final LdapPersonService ldapPersonService;

    public AideHumaineService(DocumentRepository documentRepository, AideHumaineRepository aideHumaineRepository, AidantRepository aidantRepository, DocumentService documentService, UtilsService utilsService, DossierService dossierService, SyncService syncService, LdapPersonService ldapPersonService) {
        this.documentRepository = documentRepository;
        this.aideHumaineRepository = aideHumaineRepository;
        this.aidantRepository = aidantRepository;
        this.documentService = documentService;
        this.utilsService = utilsService;
        this.dossierService = dossierService;
        this.syncService = syncService;
        this.ldapPersonService = ldapPersonService;
    }

    @Transactional
    public AideHumaine create(AideHumaine aideHumaine, Long dossierId, String eppn) {
        Dossier dossier = dossierService.getById(dossierId);
        if(dossier.getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        aideHumaine.setDossier(dossier);
        if (dossier.getStatusDossier().equals(StatusDossier.IMPORTE)
             || dossier.getStatusDossier().equals(StatusDossier.AJOUT_MANUEL)
             || dossier.getStatusDossier().equals(StatusDossier.ACCUEILLI)
             || dossier.getStatusDossier().equals(StatusDossier.RECONDUIT)) {
            dossierService.changeStatutDossier(dossierId, StatusDossier.SUIVI, eppn);
        }
        aideHumaine.setAidant(recupAidantWithNumEtu(aideHumaine.getNumAidant()));
        return aideHumaineRepository.save(aideHumaine);
    }

    public Page<AideHumaine> findByDossier(Long dossierId) {
        return aideHumaineRepository.findByDossierId(dossierId, Pageable.unpaged());
    }

    public AideHumaine getById(Long aideHumaineId) {
        return aideHumaineRepository.findById(aideHumaineId).orElseThrow();
    }

    @Transactional
    public void delete(Long aideHumaineId) {
        AideHumaine aideHumaineToUpdate = getById(aideHumaineId);
        if(aideHumaineToUpdate.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        if(aideHumaineToUpdate.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeRuntimeException("Impossible de modifier un dossier d'une année précédente");
        }
        aideHumaineRepository.deleteById(aideHumaineId);
    }

    @Transactional
    public void save(Long aideHumaineId, AideHumaine aideHumaine) throws AgapeException {
        AideHumaine aideHumaineToUpdate = getById(aideHumaineId);
        if(aideHumaineToUpdate.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        aideHumaineToUpdate.setStatusAideHumaine(aideHumaine.getStatusAideHumaine());
        aideHumaineToUpdate.setFonctionAidants(aideHumaine.getFonctionAidants());
        if (StringUtils.hasText(aideHumaine.getNumAidant())) {
            if (!aideHumaine.getNumAidant().equals(aideHumaineToUpdate.getNumAidant())) {
                aideHumaine.setAidant(recupAidantWithNumEtu(aideHumaine.getNumAidant()));
            }
        } else {
            throw new AgapeRuntimeException("numéro d'aidant non trouvé");
        }
    }

    @Transactional
    public Aidant recupAidantWithNumEtu(String numEtuAidant) {
        List<PersonLdap> personLdaps = ldapPersonService.searchBySupannEtuId(numEtuAidant);
        if(!personLdaps.isEmpty()) {
            PersonLdap personLdap = personLdaps.get(0);
            Aidant aidant = aidantRepository.findByNumEtuAidant(numEtuAidant);
            if (aidant == null) {
                aidant = new Aidant();
                aidant.setNumEtuAidant(numEtuAidant);
            }
            if (StringUtils.hasText(personLdap.getSn())) {
                aidant.setNameAidant(personLdap.getSn());
            }
            if (StringUtils.hasText(personLdap.getGivenName())) {
                aidant.setFirstNameAidant(personLdap.getGivenName());
            }
            if (StringUtils.hasText(personLdap.getMail())) {
                aidant.setEmailAidant(personLdap.getMail());
            }
            if (personLdap.getSchacDateOfBirth() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                aidant.setDateOfBirthAidant(LocalDate.parse(personLdap.getSchacDateOfBirth(), formatter));
            }
            if (StringUtils.hasText(personLdap.getTelephoneNumber())) {
                aidant.setPhoneAidant(personLdap.getTelephoneNumber());
            }
            aidantRepository.save(aidant);
            return aidant;
        }
        return null;
    }

    @Transactional
    public void addDocument(Long aideHumaineId, MultipartFile[] multipartFiles, TypeDocument type) throws AgapeIOException {
        AideHumaine aideHumaine = getById(aideHumaineId);
        if(aideHumaine.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        try {
            for (MultipartFile multipartFile : multipartFiles) {
                Document document = documentService.createDocument(multipartFile.getInputStream(), multipartFile.getOriginalFilename(), multipartFile.getContentType(), aideHumaine.getId(), AideHumaine.class.getSimpleName(), aideHumaine.getDossier());
                document.setTypeDocument(type);
                aideHumaine.getPiecesJointes().add(document);
            }
        } catch (IOException e) {
            throw new AgapeIOException(e.getMessage());
        }
    }

    @Transactional
    public void deleteDocument(Long aideHumaineId, Long documentId) {
        AideHumaine aideHumaine = getById(aideHumaineId);
        if(aideHumaine.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        if(aideHumaine.getPiecesJointes().stream().anyMatch(document -> Objects.equals(document.getId(), documentId))) {
            Document document = documentService.getById(documentId);
            aideHumaine.getPiecesJointes().remove(document);
            documentService.delete(document);
        }
    }

    @Transactional
    public void getDocumentHttpResponse(Long aideHumaineId, HttpServletResponse httpServletResponse, TypeDocument type) throws AgapeIOException {
        try {
            Document document = getDocumentByType(aideHumaineId, type);
            utilsService.copyFileStreamToHttpResponse(document.getFileName(), document.getContentType(), document.getInputStream(), httpServletResponse);
        } catch (IOException e) {
            throw new AgapeIOException(e.getMessage());
        }
    }

    private Document getDocumentByType(Long aideHumaineId, TypeDocument type) {
        return documentRepository.findByParentIdAndTypeDocument(aideHumaineId, type);
    }

    @Transactional
    public List<TypeDocument> getPiecesJointesTypes(Long aideHumaineId) {
        AideHumaine aideHumaine = getById(aideHumaineId);
        return aideHumaine.getPiecesJointes().stream().map(Document::getTypeDocument).collect(Collectors.toList());
    }

    @Transactional
    public List<Document> getPiecesJointes(Long aideHumaineId) {
        AideHumaine aideHumaine = getById(aideHumaineId);
        return new ArrayList<>(aideHumaine.getPiecesJointes());
    }
}
