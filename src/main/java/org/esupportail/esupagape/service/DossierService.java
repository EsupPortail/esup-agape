package org.esupportail.esupagape.service;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.BooleanUtils;
import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.dtos.DocumentDto;
import org.esupportail.esupagape.dtos.DossierIndividuClassDto;
import org.esupportail.esupagape.dtos.DossierIndividuDto;
import org.esupportail.esupagape.dtos.forms.DossierFilter;
import org.esupportail.esupagape.dtos.forms.DossierIndividuForm;
import org.esupportail.esupagape.entity.*;
import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.entity.enums.enquete.ModFrmn;
import org.esupportail.esupagape.entity.enums.enquete.TypFrmn;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.exception.AgapeYearException;
import org.esupportail.esupagape.repository.*;
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

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DossierService {

    private static final Logger logger = LoggerFactory.getLogger(DossierService.class);

    private final UtilsService utilsService;
    private final List<DossierInfosService> dossierInfosServices;
    private final DossierRepository dossierRepository;
    private final DocumentRepository documentRepository;
    private final DocumentService documentService;
    private final DossierAmenagementRepository dossierAmenagementRepository;
    private final EntityManager em;
    private final LogService logService;
    private final IndividuRepository individuRepository;
    private final ApplicationProperties applicationProperties;

    Map<String, String> codComposanteLabels = new HashMap<>();


    public DossierService(UtilsService utilsService, List<DossierInfosService> dossierInfosServices, DossierRepository dossierRepository, DocumentRepository documentRepository, DocumentService documentService, DossierAmenagementRepository dossierAmenagementRepository, EntityManager em, LogService logService, IndividuRepository individuRepository, ApplicationProperties applicationProperties) {
        this.utilsService = utilsService;
        this.documentRepository = documentRepository;
        this.documentService = documentService;
        this.dossierAmenagementRepository = dossierAmenagementRepository;
        this.em = em;
        this.logService = logService;
        this.applicationProperties = applicationProperties;
        Collections.reverse(dossierInfosServices);
        this.dossierInfosServices = dossierInfosServices;
        this.dossierRepository = dossierRepository;
        this.individuRepository = individuRepository;
    }

    @Transactional
    public Dossier create(String eppn, Long individuId, TypeIndividu typeIndividu, StatusDossier statusDossier) {
        Optional<Dossier> optDossier = dossierRepository.findByIndividuIdAndYear(individuId, utilsService.getCurrentYear());
        if(optDossier.isPresent()) {
            return optDossier.get();
        }
        Dossier dossier = new Dossier();
        dossier.setYear(utilsService.getCurrentYear());
        Individu individu = individuRepository.findById(individuId).orElseThrow();
        dossier.setIndividu(individu);
        if (StringUtils.hasText(individu.getNumEtu())) {
            dossier.setType(TypeIndividu.ETUDIANT);
        } else if (typeIndividu != null) {
            dossier.setType(typeIndividu);
        } else {
            dossier.setType(TypeIndividu.INCONNU);
        }
        dossierRepository.save(dossier);
        individu.getDossiers().add(dossier);
        changeStatutDossier(dossier.getId(), statusDossier, eppn);
        return dossier;
    }

    @Transactional
    public void changeStatutDossier(Long id, StatusDossier statusDossier, String eppn) {
        Dossier dossier = getById(id);
        String initialStatus = "";
        if (dossier.getStatusDossier() != null) {
            initialStatus = dossier.getStatusDossier().name();
        }
        logService.create(eppn, id, initialStatus, statusDossier.name());
        dossier.setStatusDossier(statusDossier);
    }

    @Transactional
    public void deleteDossier(Long id) {
        Optional<Dossier> dossier = dossierRepository.findById(id);
        if(dossier.isPresent()) {
            for(Document document : dossier.get().getDocuments()) {
                document.setDossier(null);
            }
            dossier.get().getDocuments().clear();
            dossierRepository.save(dossier.get());
            dossierRepository.delete(dossier.get());
        }
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
        if(StringUtils.hasText(fullTextSearch)) {
            yearFilter = null;
        }
        return dossierRepository.findByFullTextSearch(fullTextSearch, typeIndividu, statusDossier, statusDossierAmenagement, yearFilter, pageable);
    }

    @Transactional
    public void update(Long id, Dossier dossier, String eppn) {
        Dossier dossierToUpdate = getById(id);
        if (dossierToUpdate.getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        dossierToUpdate.getClassifications().clear();
        dossierToUpdate.getClassifications().addAll(dossier.getClassifications());
        dossierToUpdate.setEtat(dossier.getEtat());
        dossierToUpdate.setMdphs(dossier.getMdphs());
        dossierToUpdate.setTaux(dossier.getTaux());
        dossierToUpdate.getTypeSuiviHandisup().addAll(dossier.getTypeSuiviHandisup());
        dossierToUpdate.setSuiviHandisup(dossier.getSuiviHandisup());
        dossierToUpdate.setEmployee(dossier.getEmployee());
        dossierToUpdate.setAlternance(dossier.getAlternance());
        dossierToUpdate.setAtypie(dossier.getAtypie());
        dossierToUpdate.setEtat(dossier.getEtat());
        dossierToUpdate.setRentreeProchaine(dossier.getRentreeProchaine());
        dossierToUpdate.setCommentaire(dossier.getCommentaire());
        dossierToUpdate.setTypeFormation(dossier.getTypeFormation());
        dossierToUpdate.setModeFormation(dossier.getModeFormation());
        if(StringUtils.hasText(dossier.getNiveauEtudes())) dossierToUpdate.setNiveauEtudes(dossier.getNiveauEtudes());
        if(StringUtils.hasText(dossier.getSecteurDisciplinaire())) dossierToUpdate.setSecteurDisciplinaire(dossier.getSecteurDisciplinaire());
        if(StringUtils.hasText(dossier.getFormAddress())) dossierToUpdate.setFormAddress(dossier.getFormAddress());
        if (StringUtils.hasText(dossier.getLibelleFormation())) {
            dossierToUpdate.setLibelleFormation(dossier.getLibelleFormation());
        }
        if (StringUtils.hasText(dossier.getFormAddress())) {
            dossierToUpdate.setFormAddress(dossier.getFormAddress());
        }
//        changeStatutDossier(id, StatusDossier.ACCUEILLI, eppn);
    }

    public DossierInfos getInfos(Individu individu, Integer year) {
        DossierInfos infos = new DossierInfos();
        for (DossierInfosService dossierInfosService : dossierInfosServices) {
            dossierInfosService.getDossierProperties(individu, year, false, true, infos);
        }
        return infos;
    }

    @Transactional
    public void updateDossierIndividu(Long id, DossierIndividuForm dossierIndividuForm, String eppn) {
        Dossier dossierToUpdate = getById(id);
        if (dossierToUpdate.getYear() != utilsService.getCurrentYear() && dossierToUpdate.getType().equals(TypeIndividu.ETUDIANT) && StringUtils.hasText(dossierToUpdate.getIndividu().getNumEtu())) {
            throw new AgapeYearException();
        }
        changeStatutDossier(id, dossierIndividuForm.getStatusDossier(), eppn);
        if(!dossierToUpdate.getType().equals(TypeIndividu.ETUDIANT) || !StringUtils.hasText(dossierToUpdate.getIndividu().getNumEtu())) {
            dossierToUpdate.getIndividu().setName(dossierIndividuForm.getName());
            dossierToUpdate.getIndividu().setFirstName(dossierIndividuForm.getFirstName());
            dossierToUpdate.getIndividu().setDateOfBirth(dossierIndividuForm.getDateOfBirth());
            dossierToUpdate.getIndividu().setGender(dossierIndividuForm.getGender());
        }
        if (dossierIndividuForm.getType() != null) {
            dossierToUpdate.setType(dossierIndividuForm.getType());
        }
        if (StringUtils.hasText(dossierIndividuForm.getNumEtu())) {
            dossierToUpdate.getIndividu().setNumEtu(dossierIndividuForm.getNumEtu());
        }
        if (StringUtils.hasText(dossierIndividuForm.getNationalite())) {
            dossierToUpdate.getIndividu().setNationalite(dossierIndividuForm.getNationalite());
        }
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

    public List<String> getAllCampus() {
        return dossierRepository.findAllCampus().stream().filter(StringUtils::hasText).toList();
    }

    @Transactional
    public void addAttachment(Long id, MultipartFile[] multipartFiles) throws AgapeException {
        Dossier dossier = getById(id);
        if (dossier.getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        try {
            for (MultipartFile multipartFile : multipartFiles) {
                Document attachment = documentService.createDocument(multipartFile.getInputStream(), multipartFile.getOriginalFilename(), multipartFile.getContentType(), dossier.getId(), Dossier.class.getSimpleName(), dossier);
                dossier.getAttachments().add(attachment);
            }
        } catch (IOException e) {
            throw new AgapeIOException(e.getMessage());
        }
    }

    @Transactional
    public List<DocumentDto> getAttachments(Long id) {
        return documentRepository.findByDossierId(id).stream().filter(attachment -> "Dossier".equals(attachment.getParentType())).toList();
    }

    @Transactional
    public void deleteAttachment(Long id, Long attachmentId) {
        //if parentType == org.esupportail.esupagape.entity.Dossier
        Dossier dossier = getById(id);
        if (dossier.getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        Document attachment = documentService.getById(attachmentId);
        if ("Dossier".equals(attachment.getParentType())) {
            dossier.getAttachments().remove(attachment);
            documentService.delete(attachment);
        }
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

    @Transactional
    public Page<DossierIndividuClassDto> dossierIndividuClassDtoPage(DossierFilter dossierFilter, Pageable pageable) {
        TypedQuery<DossierIndividuClassDto> query = superFilter(dossierFilter, pageable);
        int totalRows = query.getResultList().size();
        List<DossierIndividuClassDto> resultList = query.setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
        return new PageImpl<>(resultList, pageable, totalRows);
    }

    @Transactional
    public List<String> filteredEmails(DossierFilter dossierFilter) {
        TypedQuery<DossierIndividuClassDto> query = superFilter(dossierFilter, Pageable.unpaged());
        List<DossierIndividuClassDto> dossiers = query.getResultList();
        return dossiers.stream().map(DossierIndividuClassDto::getEmailEtu).filter(StringUtils::hasText).sorted(String::compareTo).collect(Collectors.toList());
    }

    public TypedQuery<DossierIndividuClassDto> superFilter(DossierFilter dossierFilter, Pageable pageable) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DossierIndividuClassDto> cq = cb.createQuery(DossierIndividuClassDto.class);
        Root<Dossier> dossierRoot = cq.from(Dossier.class);
        Join<Dossier, Individu> dossierIndividuJoin = dossierRoot.join("individu", JoinType.INNER);
        Join<Dossier, Enquete> dossierEnqueteJoin = dossierRoot.join("enquete", JoinType.LEFT);
        Join<Dossier, AideMaterielle> dossierAideMaterielleJoin = dossierRoot.join("aidesMaterielles", JoinType.LEFT);
        Join<Dossier, AideHumaine> dossierAideHumaineJoin = dossierRoot.join("aidesHumaines", JoinType.LEFT);

        cq.multiselect(
                dossierRoot.get("id"),
                dossierIndividuJoin.get("numEtu"),
                dossierIndividuJoin.get("codeIne"),
                dossierIndividuJoin.get("firstName"),
                dossierIndividuJoin.get("name"),
                dossierIndividuJoin.get("dateOfBirth"),
                dossierRoot.get("type"),
                dossierRoot.get("statusDossier"),
                dossierRoot.get("statusDossierAmenagement"),
                dossierRoot.get("year"),
                dossierIndividuJoin.get("id"),
                dossierIndividuJoin.get("gender"),
                dossierIndividuJoin.get("emailEtu"),
                dossierIndividuJoin.get("desinscrit")
        ).distinct(true);

        List<Predicate> predicates = new ArrayList<>();

        List<Predicate> yearPredicates = new ArrayList<>();
        for (Integer year : dossierFilter.getYear()) {
            yearPredicates.add(cb.equal(cb.literal(year), dossierRoot.get("year")));
        }
        if (!yearPredicates.isEmpty()) {
            predicates.add(cb.or(yearPredicates.toArray(Predicate[]::new)));
        }

        List<Predicate> statusDossierPredicates = new ArrayList<>();
        for (StatusDossier statusDossier : dossierFilter.getStatusDossier()) {
            statusDossierPredicates.add(cb.equal(cb.literal(statusDossier), dossierRoot.get("statusDossier")));
        }
        if (!statusDossierPredicates.isEmpty()) {
            predicates.add(cb.or(statusDossierPredicates.toArray(Predicate[]::new)));
        }

        List<Predicate> statusDossierAmenagementPredicates = new ArrayList<>();
        for (StatusDossierAmenagement statusDossierAmenagement : dossierFilter.getStatusDossierAmenagement()) {
            statusDossierAmenagementPredicates.add(cb.equal(cb.literal(statusDossierAmenagement), dossierRoot.get("statusDossierAmenagement")));
        }
        if (!statusDossierAmenagementPredicates.isEmpty()) {
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
        if(dossierFilter.getDesinscrit() != null) {
            if(dossierFilter.getDesinscrit()) {
                predicates.add(cb.or(cb.isTrue(dossierIndividuJoin.get("desinscrit"))));
            } else {
                predicates.add(cb.or(cb.isFalse(dossierIndividuJoin.get("desinscrit"))));
            }
        }
        List<Predicate> classificationPredicates = new ArrayList<>();
        for (Classification classification : dossierFilter.getClassifications()) {
            Expression<Collection<Classification>> classifications = dossierRoot.get("classifications");
            classificationPredicates.add(cb.isMember(cb.literal(classification), classifications));
        }
        if (!classificationPredicates.isEmpty()) {
            predicates.add(cb.or(classificationPredicates.toArray(Predicate[]::new)));
        }
        List<Predicate> typePredicates = new ArrayList<>();
        for (TypeIndividu type : dossierFilter.getType()) {
            typePredicates.add(cb.equal(cb.literal(type), dossierRoot.get("type")));
        }
        if (!typePredicates.isEmpty()) {
            predicates.add(cb.or(typePredicates.toArray(Predicate[]::new)));
        }
        List<Predicate> mdphPredicates = new ArrayList<>();
        for (Mdph mdph : dossierFilter.getMdph()) {
            mdphPredicates.add(cb.equal(cb.literal(mdph), dossierRoot.get("mdph")));
        }
        if (!mdphPredicates.isEmpty()) {
            predicates.add(cb.or(mdphPredicates.toArray(Predicate[]::new)));
        }

        if (dossierFilter.getAtypie() != null) {
            List<Predicate> atypiePredicates = new ArrayList<>();
            if (dossierFilter.getAtypie()) {
                atypiePredicates.add(cb.isTrue(dossierRoot.get("atypie")));
            } else {
                atypiePredicates.add(cb.isFalse(dossierRoot.get("atypie")));
                atypiePredicates.add(cb.isNull(dossierRoot.get("atypie")));
            }
            predicates.add(cb.or(atypiePredicates.toArray(Predicate[]::new)));
        }

        if (dossierFilter.getHasScholarship() != null) {
            List<Predicate> suiviHasScholarshipPredicates = new ArrayList<>();
            if (dossierFilter.getHasScholarship()) {
                suiviHasScholarshipPredicates.add(cb.isTrue(dossierRoot.get("hasScholarship")));
            } else {
                suiviHasScholarshipPredicates.add(cb.isFalse(dossierRoot.get("hasScholarship")));
                suiviHasScholarshipPredicates.add(cb.isNull(dossierRoot.get("hasScholarship")));
            }
            predicates.add(cb.or(suiviHasScholarshipPredicates.toArray(Predicate[]::new)));
        }
        List<Predicate> composantePredicates = new ArrayList<>();
        for (String codComposante : dossierFilter.getComposante()) {
            composantePredicates.add(cb.equal(cb.literal(codComposante), dossierRoot.get("codComposante")));
        }
        if (!composantePredicates.isEmpty()) {
            predicates.add(cb.or(composantePredicates.toArray(Predicate[]::new)));
        }

        List<Predicate> secteurDisciplinairePredicates = new ArrayList<>();
        for (String secteurDisciplinaire : dossierFilter.getSecteurDisciplinaire()) {
            secteurDisciplinairePredicates.add(cb.equal(cb.literal(secteurDisciplinaire), dossierRoot.get("secteurDisciplinaire")));
        }
        if (!secteurDisciplinairePredicates.isEmpty()) {
            predicates.add(cb.or(secteurDisciplinairePredicates.toArray(Predicate[]::new)));
        }
        List<Predicate> libelleFormationPredicates = new ArrayList<>();
        for (String libelleFormation : dossierFilter.getLibelleFormation()) {
            libelleFormationPredicates.add(cb.equal(cb.literal(libelleFormation), dossierRoot.get("libelleFormation")));
        }
        if (!libelleFormationPredicates.isEmpty()) {
            predicates.add(cb.or(libelleFormationPredicates.toArray(Predicate[]::new)));
        }
        List<Predicate> niveauEtudesPredicates = new ArrayList<>();
        for (String niveauEtudes : dossierFilter.getNiveauEtudes()) {
            niveauEtudesPredicates.add(cb.equal(cb.literal(niveauEtudes), dossierRoot.get("niveauEtudes")));
        }
        if (!niveauEtudesPredicates.isEmpty()) {
            predicates.add(cb.or(niveauEtudesPredicates.toArray(Predicate[]::new)));
        }
        List<Predicate> typeFormationPredicates = new ArrayList<>();
        for (TypFrmn typFrmn : dossierFilter.getTypFrmn()) {
            typeFormationPredicates.add(cb.equal(cb.literal(typFrmn), dossierRoot.get("typeFormation")));
        }
        if (!typeFormationPredicates.isEmpty()) {
            predicates.add(cb.or(typeFormationPredicates.toArray(Predicate[]::new)));
        }

        List<Predicate> modeFormationPredicates = new ArrayList<>();
        for (ModFrmn modFrmn : dossierFilter.getModFrmn()) {
            modeFormationPredicates.add(cb.equal(cb.literal(modFrmn), dossierRoot.get("modeFormation")));
        }
        if (!modeFormationPredicates.isEmpty()) {
            predicates.add(cb.or(modeFormationPredicates.toArray(Predicate[]::new)));
        }
        if (StringUtils.hasText(dossierFilter.getResultatTotal())) {
            predicates.add(cb.equal(cb.literal(dossierFilter.getResultatTotal()), dossierRoot.get("resultatTotal")));
        }


        List<Predicate> genderPredicates = new ArrayList<>();
        for (Gender gender : dossierFilter.getGender()) {
            genderPredicates.add(cb.equal(cb.literal(gender), dossierIndividuJoin.get("gender")));
        }
        if (!genderPredicates.isEmpty()) {
            predicates.add(cb.or(genderPredicates.toArray(Predicate[]::new)));
        }

        List<Predicate> yearOfBirthPredicates = new ArrayList<>();
        for (Integer yearOfBirth : dossierFilter.getYearOfBirth()) {
            yearOfBirthPredicates.add(cb.between(dossierIndividuJoin.get("dateOfBirth"), LocalDate.of(yearOfBirth, 1, 1), LocalDate.of(yearOfBirth, 12, 31)));
        }
        if (!yearOfBirthPredicates.isEmpty()) {
            predicates.add(cb.or(yearOfBirthPredicates.toArray(Predicate[]::new)));
        }

        List<Predicate> fixCPPredicates = new ArrayList<>();
        for (String fixCP : dossierFilter.getFixCP()) {
            fixCPPredicates.add(cb.equal(cb.literal(fixCP), dossierIndividuJoin.get("fixCP")));
        }
        if (!fixCPPredicates.isEmpty()) {
            predicates.add(cb.or(fixCPPredicates.toArray(Predicate[]::new)));
        }


        List<Predicate> typeAideMateriellePredicates = new ArrayList<>();
        for (TypeAideMaterielle typeAideMaterielle : dossierFilter.getTypeAideMaterielle()) {
            typeAideMateriellePredicates.add(cb.equal(cb.literal(typeAideMaterielle), dossierAideMaterielleJoin.get("typeAideMaterielle")));
        }
        if (!typeAideMateriellePredicates.isEmpty()) {
            predicates.add(cb.or(typeAideMateriellePredicates.toArray(Predicate[]::new)));
        }

        List<Predicate> fonctionAidantPredicates = new ArrayList<>();
        for (FonctionAidant fonctionAidant : dossierFilter.getFonctionAidants()) {
            Expression<Collection<FonctionAidant>> fonctionAidants = dossierAideHumaineJoin.get("fonctionAidants");
            fonctionAidantPredicates.add(cb.isMember(cb.literal(fonctionAidant), fonctionAidants));
        }
        if (!fonctionAidantPredicates.isEmpty()) {
            predicates.add(cb.or(fonctionAidantPredicates.toArray(Predicate[]::new)));
        }
        if (dossierFilter.getHasScholarship() != null) {
            List<Predicate> hasScholarshipPredicates = new ArrayList<>();
            if (dossierFilter.getHasScholarship()) {
                hasScholarshipPredicates.add(cb.isTrue(dossierRoot.get("hasScholarship")));
            } else {
                hasScholarshipPredicates.add(cb.isFalse(dossierRoot.get("hasScholarship")));
                hasScholarshipPredicates.add(cb.isNull(dossierRoot.get("hasScholarship")));
            }
            predicates.add(cb.or(hasScholarshipPredicates.toArray(Predicate[]::new)));
        }

        if (dossierFilter.getNewDossier() != null) {
            if (dossierFilter.getNewDossier()) {
                predicates.add(cb.isTrue(dossierRoot.get("newDossier")));
            } else {
                predicates.add(cb.isFalse(dossierRoot.get("newDossier")));
            }
        }
        Predicate predicate = cb.and(predicates.toArray(Predicate[]::new));
        cq.where(predicate);

        try {
            if(!pageable.getSort().get().toList().isEmpty()) {
                if (
                        pageable.getSort().get().toList().get(0).getProperty().equals("name")
                        ||
                        pageable.getSort().get().toList().get(0).getProperty().equals("numEtu")
                        ||
                        pageable.getSort().get().toList().get(0).getProperty().equals("firstName")
                        ||
                        pageable.getSort().get().toList().get(0).getProperty().equals("dateOfBirth")
                ) {
                    cq.orderBy(QueryUtils.toOrders(pageable.getSort(), dossierIndividuJoin, cb));
                } else if (
                        pageable.getSort().get().toList().get(0).getProperty().equals("statusDossier")
                        ||
                        pageable.getSort().get().toList().get(0).getProperty().equals("type")
                        ||
                        pageable.getSort().get().toList().get(0).getProperty().equals("statusDossierAmenagement")
                        || pageable.getSort().get().toList().get(0).getProperty().equals("year")
                ) {
                    cq.orderBy(QueryUtils.toOrders(pageable.getSort(), dossierRoot, cb));
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return em.createQuery(cq);
    }
    @Transactional
    public void syncStatusDossierAmenagement(Long dossierId) {
        Optional<Dossier> dossier = dossierRepository.findById(dossierId);
        if(dossier.isPresent()) {
            syncStatusDossierAmenagement(dossier.get());
        }
    }

    public void syncStatusDossierAmenagement(Dossier dossier) {
        if(dossier.getDossierAmenagements() == null) return;
        if(dossier.getDossierAmenagements().stream().noneMatch(da -> da.getStatusDossierAmenagement().equals(StatusDossierAmenagement.EN_ATTENTE) && da.getAmenagement().getStatusAmenagement().equals(StatusAmenagement.SUPPRIME))
            && dossier.getDossierAmenagements().stream().noneMatch(da -> da.getStatusDossierAmenagement().equals(StatusDossierAmenagement.VALIDE))
            && dossier.getDossierAmenagements().stream().noneMatch(da -> da.getStatusDossierAmenagement().equals(StatusDossierAmenagement.PORTE))) {
            if(dossier.getDossierAmenagements().stream().anyMatch(da -> da.getStatusDossierAmenagement().equals(StatusDossierAmenagement.EXPIRE))) {
                dossier.setStatusDossierAmenagement(StatusDossierAmenagement.EXPIRE);
            } else {
                dossier.setStatusDossierAmenagement(StatusDossierAmenagement.NON);
                logger.info("dossier " + dossier.getId() + " statusDossierAmenagement set to NON");
            }
        }
        if(dossier.getDossierAmenagements().stream().anyMatch(da -> da.getStatusDossierAmenagement().equals(StatusDossierAmenagement.PORTE))) {
            dossier.setStatusDossierAmenagement(StatusDossierAmenagement.PORTE);
        }
        if(dossier.getDossierAmenagements().stream().anyMatch(da -> da.getStatusDossierAmenagement().equals(StatusDossierAmenagement.VALIDE))) {
            dossier.setStatusDossierAmenagement(StatusDossierAmenagement.VALIDE);
        }
        if(dossier.getDossierAmenagements().stream().anyMatch(da -> da.getStatusDossierAmenagement().equals(StatusDossierAmenagement.EN_ATTENTE) && !da.getAmenagement().getStatusAmenagement().equals(StatusAmenagement.SUPPRIME))) {
            dossier.setStatusDossierAmenagement(StatusDossierAmenagement.EN_ATTENTE);
        }
        if(dossier.getStatusDossier().equals(StatusDossier.NON_RECONDUIT) || dossier.getStatusDossier().equals(StatusDossier.IMPORTE) || dossier.getStatusDossier().equals(StatusDossier.AJOUT_MANUEL)) {
            if(dossier.getStatusDossierAmenagement().equals(StatusDossierAmenagement.PORTE) || dossier.getStatusDossierAmenagement().equals(StatusDossierAmenagement.VALIDE)) {
                dossier.setStatusDossier(StatusDossier.RECONDUIT);
            }
        }
    }

    @Transactional
    public boolean syncDossier(Long id) {
        Dossier dossier = dossierRepository.findById(id).orElseThrow();
        if (BooleanUtils.isTrue(dossier.getIndividu().getDesinscrit())) {
            if(applicationProperties.getCleanDesinscrits() && dossier.getYear().equals(utilsService.getCurrentYear()) && dossier.getStatusDossier().equals(StatusDossier.RECONDUIT)) {
                if(dossier.getDossierAmenagements().stream().noneMatch(da -> da.getAmenagement().getTypeAmenagement().equals(TypeAmenagement.CURSUS))) {
                    logger.info("dossier " + id + " à supprimer, étudiant reconduit non présent " + dossier.getIndividu().getNumEtu());
                    deleteDossier(id);
                }
            }
            return false;
        }
        if (dossier.getIndividu().getDossiers().size() > 1) {
            dossier.setNewDossier(false);
            Dossier lastYearDossier = dossier.getIndividu().getDossiers().stream().sorted(Comparator.comparingInt(Dossier::getYear).reversed()).filter(d -> d.getYear() < dossier.getYear()).findFirst().orElse(null);
            if(lastYearDossier != null) {
                for(DossierAmenagement dossierAmenagement : lastYearDossier.getDossierAmenagements()) {
                    if(dossierAmenagement.getStatusDossierAmenagement().equals(StatusDossierAmenagement.VALIDE)
                       && dossierAmenagement.getAmenagement().getStatusAmenagement().equals(StatusAmenagement.VISE_ADMINISTRATION)
                       && dossierAmenagement.getAmenagement().getEndDate() != null && dossierAmenagement.getAmenagement().getEndDate().isAfter(LocalDateTime.now())
                       && dossier.getDossierAmenagements().stream().noneMatch(da -> da.getAmenagement().equals(dossierAmenagement.getAmenagement()))
                        ) {
                        DossierAmenagement newDossierAmenagement = createDossierAmenagement(dossierAmenagement.getAmenagement(), dossier);
                        newDossierAmenagement.setStatusDossierAmenagement(StatusDossierAmenagement.VALIDE);
                        dossier.setStatusDossierAmenagement(StatusDossierAmenagement.VALIDE);
                        logService.create("SYSTEM", newDossierAmenagement.getId(), StatusDossierAmenagement.VALIDE.name(), StatusDossierAmenagement.VALIDE.name());
                    }
                }
            }
        } else {
            dossier.setNewDossier(true);
        }
        if (dossier.getStatusDossier().equals(StatusDossier.ANONYMOUS)) return false;
        if(StatusDossier.RECONDUIT.equals(dossier.getStatusDossier()) && (dossier.getClassifications().isEmpty() || (dossier.getClassifications().size() == 1 && (dossier.getClassifications().contains(Classification.NON_COMMUNIQUE) || dossier.getClassifications().contains(Classification.AUTRES_TROUBLES))))) {
            try {
                List<Classification> classifications = new ArrayList<>(dossier.getIndividu().getDossiers().stream().sorted(Comparator.comparingInt(Dossier::getYear).reversed()).filter(d -> !d.getClassifications().isEmpty() && !d.getClassifications().contains(Classification.AUTRES_TROUBLES) && !d.getClassifications().contains(Classification.NON_COMMUNIQUE)).findFirst().orElseThrow().getClassifications());
                dossier.getClassifications().clear();
                dossier.getClassifications().addAll(classifications);
                logger.info("dossier " + dossier.getId() + " classifications set to " + classifications);
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
        }
        for (DossierInfosService dossierInfosService : dossierInfosServices) {
            DossierInfos dossierInfos = dossierInfosService.getDossierProperties(dossier.getIndividu(), dossier.getYear(), false, false, new DossierInfos());
            if (dossierInfos != null) {
                if (StringUtils.hasText(dossierInfos.getCodComposante())) {
                    dossier.setCodComposante(dossierInfos.getCodComposante());
                }
                if (!dossierInfos.getCampus().isEmpty()) {
                    dossier.getCampus().addAll(dossierInfos.getCampus());
                }
                if (StringUtils.hasText(dossierInfos.getComposante())) {
                    dossier.setComposante(dossierInfos.getComposante().trim());
                }
                if (StringUtils.hasText(dossierInfos.getLibelleFormation())) {
                    dossier.setLibelleFormation(dossierInfos.getLibelleFormation());
                }
                if (StringUtils.hasText(dossierInfos.getLibelleFormationPrec())) {
                    dossier.setLibelleFormationPrec(dossierInfos.getLibelleFormationPrec());
                } else {
                    if(dossier.getLibelleFormationPrec() == null) {
                        dossier.setLibelleFormationPrec("");
                    }
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
                if (StringUtils.hasText(dossierInfos.getTypeDiplome())) {
                    dossier.setTypeDiplome(dossierInfos.getTypeDiplome());
                }
                if (StringUtils.hasText(dossierInfos.getResultatAnn())) {
                    dossier.setResultatTotal(dossierInfos.getResultatAnn());
                }
                dossier.setHasScholarship(dossierInfos.getHasScholarship());
                if(dossier.getClassifications().isEmpty()) {
                    List<Dossier> lastDossiers = dossierRepository.findAllByIndividuId(dossier.getIndividu().getId()).stream().sorted(Comparator.comparingInt(Dossier::getYear).reversed()).toList();
                    for(Dossier lastDossier : lastDossiers) {
                        if(!lastDossier.getClassifications().isEmpty()) {
                            dossier.getClassifications().addAll(lastDossier.getClassifications());
                            break;
                        }
                    }
                }
            }
        }
        syncStatusDossierAmenagement(dossier.getId());
        return true;
    }

    public DossierAmenagement createDossierAmenagement(Amenagement amenagement, Dossier dossier) {
        DossierAmenagement dossierAmenagement = new DossierAmenagement();
        dossierAmenagement.setLastYear(dossier.getYear());
        dossierAmenagement.setDossier(dossier);
        dossierAmenagement.setAmenagement(amenagement);
        dossierAmenagementRepository.save(dossierAmenagement);
        return dossierAmenagement;
    }

    @PostConstruct
    public void getCodComposanteLabelsFromDossierInfosService() {
        codComposanteLabels.put("ALL_ACCESS", "Toutes les composantes");
        for (DossierInfosService dossierInfosService : dossierInfosServices) {
            try {
                logger.info("Getting codComposanteLabels from " + dossierInfosService.getClass().getSimpleName());
                codComposanteLabels.putAll(dossierInfosService.getCodComposanteLabels());
            } catch (AgapeException | SQLException e) {
                logger.warn(e.getMessage());
            }
        }
    }

    public Map<String, String> getCodComposanteLabels() {
        return codComposanteLabels;
    }

    public void setCodComposanteLabels(Map<String, String> codComposanteLabels) {
        this.codComposanteLabels = codComposanteLabels;
    }

    @Transactional
    public void updateClassification(Long dossierId, List<Classification> classifications, String eduPersonPrincipalName) {
        Dossier dossier = getById(dossierId);
        dossier.getClassifications().clear();
        dossier.getClassifications().addAll(classifications);
        logService.create(eduPersonPrincipalName, dossierId, "update classification", dossier.getStatusDossier().name());
    }

    @Transactional
    public List<Amenagement> getAmenagments(Long dossierId) {
        Dossier dossier = getById(dossierId);
        return dossier.getDossierAmenagements().stream().map(dossierAmenagement -> dossierAmenagement.getAmenagement()).toList();
    }

    //    @Transactional
//    public void anonymiseUnsubscribeDossier(Long id) {
//        Dossier dossier = getById(id);
////        if (dossier.getYear() != utilsService.getCurrentYear()) {
////            throw new AgapeYearException();
////        }
//
//        int nbDossiers = 2;
//        long countDossiers =  dossier.getIndividu().getDossiers().stream().filter(d -> d.getYear() >= utilsService.getCurrentYear() - nbDossiers).count();
//        if(countDossiers == 1 && dossier.getStatusDossier() == StatusDossier.DESINSCRIT) {
//            individuService.anonymiseIndividu(dossier.getIndividu().getId());
//            dossier.setStatusDossier(StatusDossier.ANONYMOUS);
//        }
//    }

}