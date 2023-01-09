package org.esupportail.esupagape.service;

import org.esupportail.esupagape.dtos.ComposanteDto;
import org.esupportail.esupagape.dtos.DocumentDto;
import org.esupportail.esupagape.dtos.DossierIndividuDto;
import org.esupportail.esupagape.dtos.DossierIndividuForm;
import org.esupportail.esupagape.entity.Document;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.TypeIndividu;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.repository.DocumentRepository;
import org.esupportail.esupagape.repository.DossierRepository;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfos;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfosService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class DossierService {

    private static final Logger logger = LoggerFactory.getLogger(DossierService.class);

    private final UtilsService utilsService;

    private final List<DossierInfosService> dossierInfosServices;

    private final DossierRepository dossierRepository;

    private final DocumentRepository documentRepository;

    private final DocumentService documentService;

    public DossierService(UtilsService utilsService, List<DossierInfosService> dossierInfosServices, DossierRepository dossierRepository, DocumentRepository documentRepository, DocumentService documentService) {
        this.utilsService = utilsService;
        this.documentRepository = documentRepository;
        this.documentService = documentService;
        Collections.reverse(dossierInfosServices);
        this.dossierInfosServices = dossierInfosServices;
        this.dossierRepository = dossierRepository;
    }

    public Dossier create(Individu individu, StatusDossier statusDossier) {
        Dossier dossier = new Dossier();
        dossier.setYear(utilsService.getCurrentYear());
        dossier.setIndividu(individu);
        dossier.setStatusDossier(statusDossier);
        if(individu.getNumEtu() != null && !individu.getNumEtu().isEmpty()) {
            dossier.setType(TypeIndividu.ETUDIANT);
        }
        dossierRepository.save(dossier);
        return dossier;
    }

    public void deleteDossier(Long id) {
        Optional<Dossier> dossier = dossierRepository.findById(id);
        dossier.ifPresent(d -> dossierRepository.delete(d));
    }

    public Page<Dossier> getAllByYear(int year, Pageable pageable) {
        return dossierRepository.findAllByYear(year, pageable);
    }

    public Page<Dossier> getAllCurrent(Pageable pageable) {
        return dossierRepository.findAllByYear(utilsService.getCurrentYear(), pageable);
    }

    public Dossier getById(Long id) {
        return dossierRepository.findById(id).orElseThrow();
    }

    public Dossier getByYear(Long individuId, int year) throws AgapeJpaException {
        try {
            return dossierRepository.findByIndividuIdAndYear(individuId, year).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new AgapeJpaException(e.getMessage());
        }
    }

    public Dossier getCurrent(Long individuId) throws AgapeJpaException {
        return getByYear(individuId, utilsService.getCurrentYear());
    }

    public List<Dossier> getAllByIndividu(Long individuId) {
        return dossierRepository.findAllByIndividuId(individuId);
    }

    public void saveAll(List<Dossier> dossiers) {
        dossierRepository.saveAll(dossiers);
    }

    public Page<DossierIndividuDto> getFullTextSearch(String fullTextSearch, TypeIndividu typeIndividu, StatusDossier statusDossier, Integer year,Pageable pageable) {
        return dossierRepository.findByFullTextSearch(fullTextSearch, typeIndividu,statusDossier,year, pageable);
    }

    @Transactional
    public void update(Long id, Dossier dossier) {
        Dossier dossierToUpdate = getById(id);
        dossierToUpdate.setClassification(dossier.getClassification());
        dossierToUpdate.setEtat(dossier.getEtat());
        dossierToUpdate.setMdph(dossier.getMdph());
        dossierToUpdate.setTaux(dossier.getTaux());
        dossierToUpdate.setTypeSuiviHandisup(dossier.getTypeSuiviHandisup());
        dossierToUpdate.setSuiviHandisup(dossier.getSuiviHandisup());
        dossierToUpdate.setEtat(dossier.getEtat());
        dossierToUpdate.setRentreeProchaine(dossier.getRentreeProchaine());
        dossierToUpdate.setCommentaire(dossier.getCommentaire());
        dossierToUpdate.setTypeFormation(dossier.getTypeFormation());
        dossierToUpdate.setModeFormation(dossier.getModeFormation());
        if(StringUtils.hasText(dossier.getSite())) {
            dossierToUpdate.setSite(dossier.getSite());
        }
        if(StringUtils.hasText(dossier.getLibelleFormation())) {
            dossierToUpdate.setLibelleFormation(dossier.getLibelleFormation());
        }
        if(StringUtils.hasText(dossier.getFormAddress())) {
            dossierToUpdate.setFormAddress(dossier.getFormAddress());
        }
    }

    public DossierInfos getInfos(Dossier dossier) {
        DossierInfos infos = new DossierInfos();
        for(DossierInfosService dossierInfosService : dossierInfosServices) {
            dossierInfosService.getDossierProperties(dossier.getIndividu(), utilsService.getCurrentYear(), false, infos);
        }
        return infos;
    }

    @Transactional
    public void syncAllDossiers() {
        List<Dossier> dossiers = dossierRepository.findAll();
        for (Dossier dossier : dossiers) {
            syncDossier(dossier.getId());
        }
        logger.info("Sync dossiers done");
    }

    @Transactional
    public void syncDossier(Long id) {
        Dossier dossier = getById(id);
        for (DossierInfosService dossierInfosService : dossierInfosServices) {
            DossierInfos dossierInfos = dossierInfosService.getDossierProperties(dossier.getIndividu(), utilsService.getCurrentYear(), false, new DossierInfos());
            if(dossierInfos != null) {
                if(dossierInfos.getEtablissement() != null && !dossierInfos.getEtablissement().isEmpty()) {
                    dossier.setSite(dossierInfos.getEtablissement());
                }
                if(dossierInfos.getCodComposante() != null && !dossierInfos.getCodComposante().isEmpty()) {
                    dossier.setCodComposante(dossierInfos.getCodComposante());
                }
                if(dossierInfos.getComposante() != null && !dossierInfos.getComposante().isEmpty()) {
                    dossier.setComposante(dossierInfos.getComposante().trim());
                }
                if(dossierInfos.getLibelleFormation() != null && !dossierInfos.getLibelleFormation().isEmpty()) {
                    dossier.setLibelleFormation(dossierInfos.getLibelleFormation());
                }
                if(dossierInfos.getFormAddress() != null && !dossierInfos.getFormAddress().isEmpty()) {
                    dossier.setFormAddress(dossierInfos.getFormAddress());
                }
            }
        }
    }

    @Transactional
    public void updateDossierIndividu(Long id, DossierIndividuForm dossierIndividuForm) {
        Dossier dossierToUpdate = getById(id);
        dossierToUpdate.setStatusDossier(dossierIndividuForm.getStatusDossier());
        dossierToUpdate.setType(dossierIndividuForm.getType());
        if(StringUtils.hasText(dossierIndividuForm.getNumEtu())) {
            dossierToUpdate.getIndividu().setNumEtu(dossierIndividuForm.getNumEtu());
        }
        dossierToUpdate.getIndividu().setNationalite(dossierIndividuForm.getNationalite());
    }

    public List<ComposanteDto> getAllComposantes() {
        return dossierRepository.findAllComposantes();
    }

    @Transactional
    public void addAttachment(Long id, MultipartFile[] multipartFiles) throws AgapeException {
        Dossier dossier = getById(id);
        try {
            for(MultipartFile multipartFile : multipartFiles) {
                Document attachment = documentService.createDocument(multipartFile.getInputStream(), multipartFile.getOriginalFilename(), multipartFile.getContentType(), dossier.getId(), Dossier.class.getTypeName(), dossier);
                dossier.getAttachments().add(attachment);
            }
        } catch (IOException e) {
            throw new AgapeIOException(e.getMessage());
        }
    }

    @Transactional
    public List<DocumentDto> getAttachements(Long id) {
        return documentRepository.findByDossierId(id);
    }

    @Transactional
    public void deleteAttachment(Long id, Long attachmentId) {
        //if parentType == org.esupportail.esupagape.entity.Dossier
        Dossier dossier = getById(id);
        Document attachment = documentService.getById(attachmentId);
        dossier.getAttachments().remove(attachment);
        documentService.delete(attachment);
    }

}
