package org.esupportail.esupagape.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.esupportail.esupagape.dtos.ComposanteDto;
import org.esupportail.esupagape.dtos.DocumentDto;
import org.esupportail.esupagape.dtos.DossierIndividuClassDto;
import org.esupportail.esupagape.dtos.DossierIndividuDto;
import org.esupportail.esupagape.dtos.forms.DossierFilter;
import org.esupportail.esupagape.dtos.forms.DossierIndividuForm;
import org.esupportail.esupagape.entity.Document;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.StatusDossierAmenagement;
import org.esupportail.esupagape.entity.enums.TypeIndividu;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.exception.AgapeYearException;
import org.esupportail.esupagape.repository.DocumentRepository;
import org.esupportail.esupagape.repository.DossierRepository;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfos;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfosService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class DossierService {

    private static final Logger logger = LoggerFactory.getLogger(DossierService.class);

    private final UtilsService utilsService;

    private final List<DossierInfosService> dossierInfosServices;

    private final DossierRepository dossierRepository;

    private final DocumentRepository documentRepository;

    private final DocumentService documentService;

    private final ObjectMapper objectMapper;

    public DossierService(UtilsService utilsService, List<DossierInfosService> dossierInfosServices, DossierRepository dossierRepository, DocumentRepository documentRepository, DocumentService documentService, ObjectMapper objectMapper) {
        this.utilsService = utilsService;
        this.documentRepository = documentRepository;
        this.documentService = documentService;
        this.objectMapper = objectMapper;
        Collections.reverse(dossierInfosServices);
        this.dossierInfosServices = dossierInfosServices;
        this.dossierRepository = dossierRepository;
    }

    public Dossier create(Individu individu, StatusDossier statusDossier) {
        Dossier dossier = new Dossier();
        dossier.setYear(utilsService.getCurrentYear());
        dossier.setIndividu(individu);
        dossier.setStatusDossier(statusDossier);
        if (StringUtils.hasText(individu.getNumEtu())) {
            dossier.setType(TypeIndividu.ETUDIANT);
        } else {
            dossier.setType(TypeIndividu.INCONNU);
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

    public Page<DossierIndividuDto> getFullTextSearch(String fullTextSearch, TypeIndividu typeIndividu, StatusDossier statusDossier, StatusDossierAmenagement statusDossierAmenagement, Integer yearFilter, Pageable pageable) {
        return dossierRepository.findByFullTextSearch(fullTextSearch, typeIndividu, statusDossier, statusDossierAmenagement, yearFilter, pageable);
    }

    @Transactional
    public void update(Long id, Dossier dossier) {
        Dossier dossierToUpdate = getById(id);
        if (dossierToUpdate.getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        dossierToUpdate.setClassification(dossier.getClassification());
        dossierToUpdate.setEtat(dossier.getEtat());
        dossierToUpdate.setMdph(dossier.getMdph());
        dossierToUpdate.setTaux(dossier.getTaux());
        dossierToUpdate.setTypeSuiviHandisup(dossier.getTypeSuiviHandisup());
        dossierToUpdate.setSuiviHandisup(dossier.getSuiviHandisup());
        dossierToUpdate.setEmployee(dossier.getEmployee());
        dossierToUpdate.setEtat(dossier.getEtat());
        dossierToUpdate.setRentreeProchaine(dossier.getRentreeProchaine());
        dossierToUpdate.setCommentaire(dossier.getCommentaire());
        dossierToUpdate.setTypeFormation(dossier.getTypeFormation());
        dossierToUpdate.setModeFormation(dossier.getModeFormation());
        if (StringUtils.hasText(dossier.getLibelleFormation())) {
            dossierToUpdate.setLibelleFormation(dossier.getLibelleFormation());
        }
        if (StringUtils.hasText(dossier.getFormAddress())) {
            dossierToUpdate.setFormAddress(dossier.getFormAddress());
        }
    }

    public DossierInfos getInfos(Dossier dossier) {
        DossierInfos infos = new DossierInfos();
        for (DossierInfosService dossierInfosService : dossierInfosServices) {
            dossierInfosService.getDossierProperties(dossier.getIndividu(), dossier.getYear(), false, true, infos);
        }
        return infos;
    }

    @Transactional
    public void syncAllDossiers() {
        logger.info("Sync dossiers started");
        List<Dossier> dossiers = dossierRepository.findAllByYear(utilsService.getCurrentYear(), Pageable.unpaged()).getContent();
        for (Dossier dossier : dossiers) {
            syncDossier(dossier.getId());
        }
        logger.info("Sync dossiers done");
    }

    @Transactional
    public void syncDossier(Long id) {
        Dossier dossier = getById(id);
        if (dossier.getAmenagements().size() == 0) {
            dossier.setStatusDossierAmenagement(StatusDossierAmenagement.NON);
        }
        for (DossierInfosService dossierInfosService : dossierInfosServices) {
            DossierInfos dossierInfos = dossierInfosService.getDossierProperties(dossier.getIndividu(), utilsService.getCurrentYear(), false, false, new DossierInfos());
            if (dossierInfos != null) {
                if (StringUtils.hasText(dossierInfos.getCodComposante())) {
                    dossier.setCodComposante(dossierInfos.getCodComposante());
                }
                if (StringUtils.hasText(dossierInfos.getComposante())) {
                    dossier.setComposante(dossierInfos.getComposante().trim());
                }
                if (StringUtils.hasText(dossierInfos.getLibelleFormation())) {
                    dossier.setLibelleFormation(dossierInfos.getLibelleFormation());
                }
                if (StringUtils.hasText(dossierInfos.getLibelleFormationPrec())) {
                    dossier.setLibelleFormationPrec(dossierInfos.getLibelleFormationPrec());
                }
                if (StringUtils.hasText(dossierInfos.getFormAddress())) {
                    dossier.setFormAddress(dossierInfos.getFormAddress());
                }
            }
        }
    }

    @Transactional
    public void updateDossierIndividu(Long id, DossierIndividuForm dossierIndividuForm) {
        Dossier dossierToUpdate = getById(id);
        if (dossierToUpdate.getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        dossierToUpdate.setStatusDossier(dossierIndividuForm.getStatusDossier());
        dossierToUpdate.setStatusDossierAmenagement(dossierToUpdate.getStatusDossierAmenagement());
        if (!StringUtils.hasText(dossierToUpdate.getIndividu().getNumEtu())) {
            dossierToUpdate.setType(dossierIndividuForm.getType());
        }
        if (StringUtils.hasText(dossierIndividuForm.getNumEtu())) {
            dossierToUpdate.getIndividu().setNumEtu(dossierIndividuForm.getNumEtu());
        }
        if (StringUtils.hasText(dossierIndividuForm.getNationalite())) {
            dossierToUpdate.getIndividu().setNationalite(dossierIndividuForm.getNationalite());
        }
    }

    public List<ComposanteDto> getAllComposantes() {
        return dossierRepository.findAllComposantes();
    }

    @Transactional
    public void addAttachment(Long id, MultipartFile[] multipartFiles) throws AgapeException {
        Dossier dossier = getById(id);
        if (dossier.getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        try {
            for (MultipartFile multipartFile : multipartFiles) {
                Document attachment = documentService.createDocument(multipartFile.getInputStream(), multipartFile.getOriginalFilename(), multipartFile.getContentType(), dossier.getId(), Dossier.class.getTypeName(), dossier);
                dossier.getAttachments().add(attachment);
            }
        } catch (IOException e) {
            throw new AgapeIOException(e.getMessage());
        }
    }

    @Transactional
    public List<DocumentDto> getAttachments(Long id) {
        return documentRepository.findByDossierId(id).stream().filter(attachment -> "org.esupportail.esupagape.entity.Dossier".equals(attachment.getParentType())).toList();
    }

    @Transactional
    public void deleteAttachment(Long id, Long attachmentId) {
        //if parentType == org.esupportail.esupagape.entity.Dossier
        Dossier dossier = getById(id);
        if (dossier.getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        Document attachment = documentService.getById(attachmentId);
        if ("org.esupportail.esupagape.entity.Dossier".equals(attachment.getParentType())) {
            dossier.getAttachments().remove(attachment);
            documentService.delete(attachment);
        }
    }

    @Transactional
    public Page<DossierIndividuClassDto> findDossierByDossierFilter(DossierFilter dossierFilter, Pageable pageable) {
        Dossier dossierEx = new Dossier();
        dossierEx.setYear(dossierFilter.getYearFilter());
        dossierEx.setStatusDossier(dossierFilter.getStatusDossier());
        dossierEx.setStatusDossierAmenagement(dossierFilter.getStatusDossierAmenagement());
        Individu individuEx = new Individu();
        individuEx.setGender(dossierFilter.getGender());
        dossierEx.setIndividu(individuEx);
        Example<Dossier> dossierExample = Example.of(dossierEx);
        Page<Dossier> dossiers = dossierRepository.findAll(dossierExample, pageable);
        List<DossierIndividuClassDto> dossierIndividuDtos = new ArrayList<>();
        for(Dossier dossier : dossiers) {
            DossierIndividuClassDto dossierIndividuDto = new DossierIndividuClassDto();
            dossierIndividuDto.setId(dossier.getId());
            dossierIndividuDto.setNumEtu(dossier.getIndividu().getNumEtu());
            dossierIndividuDto.setCodeIne(dossier.getIndividu().getCodeIne());
            dossierIndividuDto.setFirstName(dossier.getIndividu().getFirstName());
            dossierIndividuDto.setName(dossier.getIndividu().getName());
            dossierIndividuDto.setDateOfBirth(dossier.getIndividu().getDateOfBirth());
            dossierIndividuDto.setType(dossier.getType());
            dossierIndividuDto.setStatusDossier(dossier.getStatusDossier());
            dossierIndividuDto.setStatusDossierAmenagement(dossier.getStatusDossierAmenagement());
            dossierIndividuDto.setIndividuId(dossier.getIndividu().getId());
            dossierIndividuDto.setGender(dossier.getIndividu().getGender());
            dossierIndividuDtos.add(dossierIndividuDto);
        }
        return new PageImpl<>(dossierIndividuDtos, pageable, dossierIndividuDtos.size());
    }

    public boolean isDossierOfThisYear(Dossier dossier) {
        return isDossierOfThisYear(dossier.getId());
    }

    public boolean isDossierOfThisYear(Long id) {
        Dossier dossier = getById(id);
        try {
            Dossier thisYearDossier = getCurrent(dossier.getIndividu().getId());
            if (thisYearDossier.getId().equals(dossier.getId())) {
                return true;
            }
        } catch (AgapeJpaException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }
}


