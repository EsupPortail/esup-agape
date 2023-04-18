package org.esupportail.esupagape.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.esupportail.esupagape.dtos.ComposanteDto;
import org.esupportail.esupagape.dtos.DocumentDto;
import org.esupportail.esupagape.dtos.DossierIndividuClassDto;
import org.esupportail.esupagape.dtos.DossierIndividuDto;
import org.esupportail.esupagape.dtos.forms.DossierFilter;
import org.esupportail.esupagape.dtos.forms.DossierIndividuForm;
import org.esupportail.esupagape.entity.AideHumaine;
import org.esupportail.esupagape.entity.AideMaterielle;
import org.esupportail.esupagape.entity.Document;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Enquete;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.Classification;
import org.esupportail.esupagape.entity.enums.FonctionAidant;
import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.entity.enums.Mdph;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.entity.enums.StatusDossierAmenagement;
import org.esupportail.esupagape.entity.enums.TypeAideMaterielle;
import org.esupportail.esupagape.entity.enums.TypeIndividu;
import org.esupportail.esupagape.entity.enums.enquete.ModFrmn;
import org.esupportail.esupagape.entity.enums.enquete.TypFrmn;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.exception.AgapeYearException;
import org.esupportail.esupagape.repository.DocumentRepository;
import org.esupportail.esupagape.repository.DossierRepository;
import org.esupportail.esupagape.repository.IndividuRepository;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfos;
import org.esupportail.esupagape.service.interfaces.dossierinfos.DossierInfosService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DossierService {

    private static final Logger logger = LoggerFactory.getLogger(DossierService.class);

    private final UtilsService utilsService;

    private final List<DossierInfosService> dossierInfosServices;

    private final DossierRepository dossierRepository;

    private final IndividuRepository individuRepository;

    private final DocumentRepository documentRepository;

    private final DocumentService documentService;

    private final EntityManager em;

    public DossierService(UtilsService utilsService, List<DossierInfosService> dossierInfosServices, DossierRepository dossierRepository, IndividuRepository individuRepository, DocumentRepository documentRepository, DocumentService documentService, ObjectMapper objectMapper, EntityManager em) {
        this.utilsService = utilsService;
        this.individuRepository = individuRepository;
        this.documentRepository = documentRepository;
        this.documentService = documentService;
        this.em = em;
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
        dossierToUpdate.setClassifications(dossier.getClassifications());
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
                if (StringUtils.hasText(dossierInfos.getNiveauEtudes())) {
                    dossier.setNiveauEtudes(dossierInfos.getNiveauEtudes());
                }
                if (StringUtils.hasText(dossierInfos.getSecteurDisciplinaire())) {
                    dossier.setSecteurDisciplinaire(dossierInfos.getSecteurDisciplinaire());
                }
                if (StringUtils.hasText(dossierInfos.getResultatAnn())) {
                    dossier.setResultatTotal(dossierInfos.getResultatAnn());
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

    public List<String> getAllNiveauEtudes() {
        return dossierRepository.findAllNiveaux();
    }

    public List<String> getAllSecteurDisciplinaire() {
        return dossierRepository.findAllSecteurDisciplinaire();
    }

    public List<String> getAllLibelleFormation() {
        return dossierRepository.findAllLibelleFormation();
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

//    @Transactional
//    public Page<DossierIndividuClassDto> findDossierByDossierFilter(DossierFilter dossierFilter, Pageable pageable) {
//        Dossier dossierEx = new Dossier();
//        dossierEx.setYear(dossierFilter.getYearFilter());
//        dossierEx.setStatusDossier(dossierFilter.getStatusDossier());
//        dossierEx.setStatusDossierAmenagement(dossierFilter.getStatusDossierAmenagement());
//        Individu individuEx = new Individu();
//        individuEx.setGender(dossierFilter.getGender());
//        dossierEx.setIndividu(individuEx);
//        Example<Dossier> dossierExample = Example.of(dossierEx);
//        Page<Dossier> dossiers = dossierRepository.findAll(dossierExample, pageable);
//        List<DossierIndividuClassDto> dossierIndividuDtos = new ArrayList<>();
//        for(Dossier dossier : dossiers) {
//            DossierIndividuClassDto dossierIndividuDto = new DossierIndividuClassDto();
//            dossierIndividuDto.setId(dossier.getId());
//            dossierIndividuDto.setNumEtu(dossier.getIndividu().getNumEtu());
//            dossierIndividuDto.setCodeIne(dossier.getIndividu().getCodeIne());
//            dossierIndividuDto.setFirstName(dossier.getIndividu().getFirstName());
//            dossierIndividuDto.setName(dossier.getIndividu().getName());
//            dossierIndividuDto.setDateOfBirth(dossier.getIndividu().getDateOfBirth());
//            dossierIndividuDto.setType(dossier.getType());
//            dossierIndividuDto.setStatusDossier(dossier.getStatusDossier());
//            dossierIndividuDto.setStatusDossierAmenagement(dossier.getStatusDossierAmenagement());
//            dossierIndividuDto.setIndividuId(dossier.getIndividu().getId());
//            dossierIndividuDto.setGender(dossier.getIndividu().getGender());
//            dossierIndividuDtos.add(dossierIndividuDto);
//        }
//        return new PageImpl<>(dossierIndividuDtos, pageable, dossierIndividuDtos.size());
//    }

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

    @Transactional
    public Page<DossierIndividuClassDto> dossierIndividuClassDtoPage(DossierFilter dossierFilter, Pageable pageable) {
        TypedQuery<Dossier> query = superFilter(dossierFilter, pageable);
        int totalRows = query.getResultList().size();
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());
        List<Dossier> resultList = query.getResultList();
        List<DossierIndividuClassDto> dossierIndividuDtos = new ArrayList<>();
        for (Dossier dossier : resultList) {
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
        return new PageImpl<>(dossierIndividuDtos, pageable, totalRows);
    }

    @Transactional
    public List<String> filteredEmails(DossierFilter dossierFilter) {
        TypedQuery<Dossier> query = superFilter(dossierFilter, Pageable.unpaged());
        List<Dossier> dossiers = query.getResultList();
        return dossiers.stream().map(dossier -> dossier.getIndividu().getEmailEtu()).collect(Collectors.toList());
    }

    public TypedQuery<Dossier> superFilter(DossierFilter dossierFilter, Pageable pageable) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Dossier> cq = cb.createQuery(Dossier.class);
        Root<Dossier> dossierRoot = cq.from(Dossier.class);

        Join<Dossier, Individu> dossierIndividuJoin = dossierRoot.join("individu", JoinType.INNER);
        Join<Dossier, Enquete> dossierEnqueteJoin = dossierRoot.join("enquete", JoinType.LEFT);
        Join<Dossier, AideMaterielle> dossierAideMaterielleJoin = dossierRoot.join("aidesMaterielles", JoinType.LEFT);
        Join<Dossier, AideHumaine> dossierAideHumaineJoin = dossierRoot.join("aidesHumaines", JoinType.LEFT);
        ;//        Join<Individu, List<Dossier>> dossierIndividuDossiersJoin = dossierIndividuJoin.join("dossiers", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        List<Predicate> yearPredicates = new ArrayList<>();
        for (Integer year : dossierFilter.getYear()) {
            yearPredicates.add(cb.equal(cb.literal(year), dossierRoot.get("year")));
        }
        if (yearPredicates.size() > 0) {
            predicates.add(cb.or(yearPredicates.toArray(Predicate[]::new)));
        }

        List<Predicate> statusDossierPredicates = new ArrayList<>();
        for (StatusDossier statusDossier : dossierFilter.getStatusDossier()) {
            statusDossierPredicates.add(cb.equal(cb.literal(statusDossier), dossierRoot.get("statusDossier")));
        }
        if (statusDossierPredicates.size() > 0) {
            predicates.add(cb.or(statusDossierPredicates.toArray(Predicate[]::new)));
        }

        List<Predicate> statusDossierAmenagementPredicates = new ArrayList<>();
        for (StatusDossierAmenagement statusDossierAmenagement : dossierFilter.getStatusDossierAmenagement()) {
            statusDossierAmenagementPredicates.add(cb.equal(cb.literal(statusDossierAmenagement), dossierRoot.get("statusDossierAmenagement")));
        }
        if (statusDossierAmenagementPredicates.size() > 0) {
            predicates.add(cb.or(statusDossierAmenagementPredicates.toArray(Predicate[]::new)));
        }
        if (dossierFilter.getFinished() != null) {
            List<Predicate> finishedPredicates = new ArrayList<>();
            if (dossierFilter.getFinished()) {
                finishedPredicates.add(cb.or(cb.isTrue(dossierEnqueteJoin.get("finished"))));
            } else {
                finishedPredicates.add(cb.or(cb.isFalse(dossierEnqueteJoin.get("finished"))));
                finishedPredicates.add(cb.or(cb.isNull(dossierEnqueteJoin.get("finished"))));
            }
            predicates.add(cb.or(finishedPredicates.toArray(Predicate[]::new)));
        }
        List<Predicate> classificationPredicates = new ArrayList<>();
        for (Classification classification : dossierFilter.getClassifications()) {
            Expression<Collection<Classification>> classifications = dossierRoot.get("classifications");
            classificationPredicates.add(cb.isMember(cb.literal(classification), classifications));
        }
        if (classificationPredicates.size() > 0) {
            predicates.add(cb.or(classificationPredicates.toArray(Predicate[]::new)));
        }
        List<Predicate> typePredicates = new ArrayList<>();
        for (TypeIndividu type : dossierFilter.getType()) {
            typePredicates.add(cb.equal(cb.literal(type), dossierRoot.get("type")));
        }
        if (typePredicates.size() > 0) {
            predicates.add(cb.or(typePredicates.toArray(Predicate[]::new)));
        }
        List<Predicate> mdphPredicates = new ArrayList<>();
        for (Mdph mdph : dossierFilter.getMdph()) {
            mdphPredicates.add(cb.equal(cb.literal(mdph), dossierRoot.get("mdph")));
        }
        if (mdphPredicates.size() > 0) {
            predicates.add(cb.or(mdphPredicates.toArray(Predicate[]::new)));
        }

        if (dossierFilter.getSuiviHandisup() != null) {
            List<Predicate> suiviHandisupPredicates = new ArrayList<>();
            if (dossierFilter.getSuiviHandisup()) {
                suiviHandisupPredicates.add(cb.isTrue(dossierRoot.get("suiviHandisup")));
            } else {
                suiviHandisupPredicates.add(cb.isFalse(dossierRoot.get("suiviHandisup")));
                suiviHandisupPredicates.add(cb.isNull(dossierRoot.get("suiviHandisup")));
            }
            predicates.add(cb.or(suiviHandisupPredicates.toArray(Predicate[]::new)));
        }
        List<Predicate> composantePredicates = new ArrayList<>();
        for (String codComposante : dossierFilter.getComposante()) {
            composantePredicates.add(cb.equal(cb.literal(codComposante), dossierRoot.get("codComposante")));
        }
        if (composantePredicates.size() > 0) {
            predicates.add(cb.or(composantePredicates.toArray(Predicate[]::new)));
        }

        List<Predicate> secteurDisciplinairePredicates = new ArrayList<>();
        for (String secteurDisciplinaire : dossierFilter.getSecteurDisciplinaire()) {
            secteurDisciplinairePredicates.add(cb.equal(cb.literal(secteurDisciplinaire), dossierRoot.get("secteurDisciplinaire")));
        }
        if (secteurDisciplinairePredicates.size() > 0) {
            predicates.add(cb.or(secteurDisciplinairePredicates.toArray(Predicate[]::new)));
        }
        List<Predicate> libelleFormationPredicates = new ArrayList<>();
        for (String libelleFormation : dossierFilter.getLibelleFormation()) {
            libelleFormationPredicates.add(cb.equal(cb.literal(libelleFormation), dossierRoot.get("libelleFormation")));
        }
        if (libelleFormationPredicates.size() > 0) {
            predicates.add(cb.or(libelleFormationPredicates.toArray(Predicate[]::new)));
        }
        List<Predicate> niveauEtudesPredicates = new ArrayList<>();
        for (String niveauEtudes : dossierFilter.getNiveauEtudes()) {
            niveauEtudesPredicates.add(cb.equal(cb.literal(niveauEtudes), dossierRoot.get("niveauEtudes")));
        }
        if (niveauEtudesPredicates.size() > 0) {
            predicates.add(cb.or(niveauEtudesPredicates.toArray(Predicate[]::new)));
        }
        List<Predicate> typeFormationPredicates = new ArrayList<>();
        for (TypFrmn typFrmn : dossierFilter.getTypFrmn()) {
            typeFormationPredicates.add(cb.equal(cb.literal(typFrmn), dossierRoot.get("typeFormation")));
        }
        if (typeFormationPredicates.size() > 0) {
            predicates.add(cb.or(typeFormationPredicates.toArray(Predicate[]::new)));
        }

        List<Predicate> modeFormationPredicates = new ArrayList<>();
        for (ModFrmn modFrmn : dossierFilter.getModFrmn()) {
            modeFormationPredicates.add(cb.equal(cb.literal(modFrmn), dossierRoot.get("modeFormation")));
        }
        if (modeFormationPredicates.size() > 0) {
            predicates.add(cb.or(modeFormationPredicates.toArray(Predicate[]::new)));
        }
        if (StringUtils.hasText(dossierFilter.getResultatTotal())) {
            predicates.add(cb.equal(cb.literal(dossierFilter.getResultatTotal()), dossierRoot.get("resultatTotal")));
        }


        List<Predicate> genderPredicates = new ArrayList<>();
        for (Gender gender : dossierFilter.getGender()) {
            genderPredicates.add(cb.equal(cb.literal(gender), dossierIndividuJoin.get("gender")));
        }
        if (genderPredicates.size() > 0) {
            predicates.add(cb.or(genderPredicates.toArray(Predicate[]::new)));
        }

        List<Predicate> yearOfBirthPredicates = new ArrayList<>();
        for (Integer yearOfBirth : dossierFilter.getYearOfBirth()) {
            yearOfBirthPredicates.add(cb.between(dossierIndividuJoin.get("dateOfBirth"), LocalDate.of(yearOfBirth, 1, 1), LocalDate.of(yearOfBirth, 12, 31)));
        }
        if (yearOfBirthPredicates.size() > 0) {
            predicates.add(cb.or(yearOfBirthPredicates.toArray(Predicate[]::new)));
        }

        List<Predicate> fixCPPredicates = new ArrayList<>();
        for (String fixCP : dossierFilter.getFixCP()) {
            fixCPPredicates.add(cb.equal(cb.literal(fixCP), dossierIndividuJoin.get("fixCP")));
        }
        if (fixCPPredicates.size() > 0) {
            predicates.add(cb.or(fixCPPredicates.toArray(Predicate[]::new)));
        }


        List<Predicate> typeAideMateriellePredicates = new ArrayList<>();
        for (TypeAideMaterielle typeAideMaterielle : dossierFilter.getTypeAideMaterielle()) {
            typeAideMateriellePredicates.add(cb.equal(cb.literal(typeAideMaterielle), dossierAideMaterielleJoin.get("typeAideMaterielle")));
        }
        if (typeAideMateriellePredicates.size() > 0) {
            predicates.add(cb.or(typeAideMateriellePredicates.toArray(Predicate[]::new)));
        }

        List<Predicate> fonctionAidantPredicates = new ArrayList<>();
        for (FonctionAidant fonctionAidant : dossierFilter.getFonctionAidants()) {
            Expression<Collection<FonctionAidant>> fonctionAidants = dossierAideHumaineJoin.get("fonctionAidants");
            fonctionAidantPredicates.add(cb.isMember(cb.literal(fonctionAidant), fonctionAidants));
        }
        if (fonctionAidantPredicates.size() > 0) {
            predicates.add(cb.or(fonctionAidantPredicates.toArray(Predicate[]::new)));
        }
        //        if(dossierFilter.getNewDossier() != null && dossierFilter.getNewDossier()) {
//            predicates.add(cb.lessThan(cb.count(dossierIndividuJoin.get("dossiers")), 2L));
//        }
        Predicate predicate = cb.and(predicates.toArray(Predicate[]::new));
        cq.where(predicate);

        try {
            cq.orderBy(QueryUtils.toOrders(pageable.getSort(), dossierRoot, cb));
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
        try {
            cq.orderBy(QueryUtils.toOrders(pageable.getSort(), dossierIndividuJoin, cb));

        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
        return em.createQuery(cq);
    }

}


