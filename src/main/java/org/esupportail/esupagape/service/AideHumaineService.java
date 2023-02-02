package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.AideHumaine;
import org.esupportail.esupagape.entity.Document;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.enums.TypeDocumentAideHumaine;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.esupportail.esupagape.exception.AgapeYearException;
import org.esupportail.esupagape.repository.AideHumaineRepository;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuInfos;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class AideHumaineService {

    private final AideHumaineRepository aideHumaineRepository;

    private final IndividuService individuService;

    private final DocumentService documentService;

    private final UtilsService utilsService;

    private final DossierService dossierService;

    public AideHumaineService(AideHumaineRepository aideHumaineRepository, IndividuService individuService, DocumentService documentService, UtilsService utilsService, DossierService dossierService) {
        this.aideHumaineRepository = aideHumaineRepository;
        this.individuService = individuService;
        this.documentService = documentService;
        this.utilsService = utilsService;
        this.dossierService = dossierService;
    }

    @Transactional
    public AideHumaine create(AideHumaine aideHumaine, Long dossierId) {
        Dossier dossier = dossierService.getById(dossierId);
        if(dossier.getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        aideHumaine.setDossier(dossier);
        recupAidantWithNumEtu(aideHumaine.getNumEtuAidant(), aideHumaine);
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
            throw new AgapeException("Impossible de modifier un dossier d'une année précédente");
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
        aideHumaineToUpdate.setRib(aideHumaine.getRib());
        aideHumaineToUpdate.setCarteVitale(aideHumaine.getCarteVitale());
        aideHumaineToUpdate.setCarteEtu(aideHumaine.getCarteEtu());
        if (StringUtils.hasText(aideHumaine.getNumEtuAidant())) {
            if (!aideHumaine.getNumEtuAidant().equals(aideHumaineToUpdate.getNumEtuAidant())) {
                recupAidantWithNumEtu(aideHumaine.getNumEtuAidant(), aideHumaineToUpdate);
            }
        } else {
            aideHumaineToUpdate.setNumEtuAidant("");
            if (StringUtils.hasText(aideHumaine.getNameAidant())) {
                aideHumaineToUpdate.setNameAidant(aideHumaine.getNameAidant());
            }
            if (StringUtils.hasText(aideHumaine.getFirstNameAidant())) {
                aideHumaineToUpdate.setFirstNameAidant(aideHumaine.getFirstNameAidant());
            }
            if (StringUtils.hasText(aideHumaine.getPhoneAidant())) {
                aideHumaineToUpdate.setPhoneAidant(aideHumaine.getPhoneAidant());
            }
            if (StringUtils.hasText(aideHumaine.getEmailAidant())) {
                aideHumaineToUpdate.setEmailAidant(aideHumaine.getEmailAidant());
            }
        }
    }

    private void recupAidantWithNumEtu(String numEtu, AideHumaine aideHumaineToUpdate) {
        IndividuInfos individuInfos = individuService.getIndividuInfosByNumEtu(numEtu);
        if (StringUtils.hasText(individuInfos.getName())) {
            aideHumaineToUpdate.setNameAidant(individuInfos.getName());
        }
        if (StringUtils.hasText(individuInfos.getFirstName())) {
            aideHumaineToUpdate.setFirstNameAidant(individuInfos.getFirstName());
        }
        if (StringUtils.hasText(individuInfos.getEmailEtu())) {
            aideHumaineToUpdate.setEmailAidant(individuInfos.getEmailEtu());
        }
        if (StringUtils.hasText(individuInfos.getEmailPerso())) {
            aideHumaineToUpdate.setEmailAidant(individuInfos.getEmailPerso());
        }
        if (individuInfos.getDateOfBirth() != null) {
            aideHumaineToUpdate.setDateOfBirthAidant(individuInfos.getDateOfBirth());
        }
        if (StringUtils.hasText(individuInfos.getFixPhone())) {
            aideHumaineToUpdate.setPhoneAidant(individuInfos.getFixPhone());
        }
        if (StringUtils.hasText(individuInfos.getContactPhone())) {
            aideHumaineToUpdate.setPhoneAidant(individuInfos.getContactPhone());
        }
        aideHumaineToUpdate.setNumEtuAidant(numEtu);
    }

    @Transactional
    public void addDocument(Long aideHumaineId, MultipartFile[] multipartFiles, TypeDocumentAideHumaine type) throws AgapeIOException {
        AideHumaine aideHumaine = getById(aideHumaineId);
        if(aideHumaine.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        try {
            for (MultipartFile multipartFile : multipartFiles) {
                Document document = documentService.createDocument(multipartFile.getInputStream(), multipartFile.getOriginalFilename(), multipartFile.getContentType(), aideHumaine.getId(), AideHumaine.class.getTypeName(), aideHumaine.getDossier());
                switch (type) {
                    case FICHE -> aideHumaine.setFicheRenseignement(document);
                    case ANNEXE -> aideHumaine.setAnnexe(document);
                    case CONTRAT -> aideHumaine.setContrat(document);
                    case RIB -> aideHumaine.setRib(document);
                    case CARTE_VITALE -> aideHumaine.setCarteVitale(document);
                    case CARTE_ETU -> aideHumaine.setCarteEtu(document);
                }
            }
        } catch (IOException e) {
            throw new AgapeIOException(e.getMessage());
        }
    }

    @Transactional
    public void deleteDocument(Long aideHumaineId, TypeDocumentAideHumaine type) {
        AideHumaine aideHumaine = getById(aideHumaineId);
        if(aideHumaine.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        Document document = getDocumentByType(type, aideHumaine);
        switch (type) {
            case FICHE -> aideHumaine.setFicheRenseignement(null);
            case ANNEXE -> aideHumaine.setAnnexe(null);
            case CONTRAT -> aideHumaine.setContrat(null);
            case RIB -> aideHumaine.setRib(null);
            case CARTE_VITALE -> aideHumaine.setCarteVitale(null);
            case CARTE_ETU -> aideHumaine.setCarteEtu(null);
        }
        documentService.delete(document);
    }

    @Transactional
    public void getDocumentHttpResponse(Long aideHumaineId, HttpServletResponse httpServletResponse, TypeDocumentAideHumaine type) throws AgapeIOException {
        AideHumaine aideHumaine = getById(aideHumaineId);
        if(aideHumaine.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        try {
            Document document = getDocumentByType(type, aideHumaine);
            utilsService.copyFileStreamToHttpResponse(document.getFileName(), document.getContentType(), document.getInputStream(), httpServletResponse);
        } catch (IOException e) {
            throw new AgapeIOException(e.getMessage());
        }
    }

    private static Document getDocumentByType(TypeDocumentAideHumaine type, AideHumaine aideHumaine) {
        Document document = null;
        switch (type) {
            case FICHE -> document = aideHumaine.getFicheRenseignement();
            case ANNEXE -> document = aideHumaine.getAnnexe();
            case CONTRAT -> document = aideHumaine.getContrat();
            case RIB -> document = aideHumaine.getRib();
            case CARTE_VITALE -> document = aideHumaine.getCarteVitale();
            case CARTE_ETU -> document = aideHumaine.getCarteEtu();
        }
        return document;
    }

}
