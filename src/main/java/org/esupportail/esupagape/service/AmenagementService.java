package org.esupportail.esupagape.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.config.ldap.LdapProperties;
import org.esupportail.esupagape.dtos.forms.AmenagementCreateDto;
import org.esupportail.esupagape.dtos.forms.AmenagementUpdateDto;
import org.esupportail.esupagape.dtos.forms.LigneAmenagementDto;
import org.esupportail.esupagape.dtos.pdfs.CertificatPdf;
import org.esupportail.esupagape.entity.*;
import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.entity.enums.enquete.CodMeae;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.exception.AgapeRuntimeException;
import org.esupportail.esupagape.exception.AgapeYearException;
import org.esupportail.esupagape.repository.*;
import org.esupportail.esupagape.repository.ldap.OrganizationalUnitLdapRepository;
import org.esupportail.esupagape.repository.ldap.PersonLdapRepository;
import org.esupportail.esupagape.service.ldap.OrganizationalUnitLdap;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.esupportail.esupagape.service.mail.MailService;
import org.esupportail.esupagape.service.utils.EsupSignatureService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AmenagementService {

    private static final Logger logger = LoggerFactory.getLogger(AmenagementService.class);

    private final ApplicationProperties applicationProperties;
    private final LdapProperties ldapProperties;
    private final AmenagementRepository amenagementRepository;
    private final DossierAmenagementRepository dossierAmenagementRepository;
    private final DossierService dossierService;
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;
    private final UtilsService utilsService;
    private final EsupSignatureService esupSignatureService;
    private final MailService mailService;
    private final LogService logService;
    private final DocumentService documentService;
    private final LibelleAmenagementRepository libelleAmenagementRepository;
    private final UserOthersAffectationsRepository userOthersAffectationsRepository;
    private final PersonLdapRepository personLdapRepository;
    private final OrganizationalUnitLdapRepository organizationalUnitLdapRepository;
    private final DossierRepository dossierRepository;
    private final DataMappingService dataMappingService;
    private final TypeLigneAmenagementRepository typeLigneAmenagementRepository;
    private final AmenagementWorkflowService amenagementWorkflowService;

    public AmenagementService(ApplicationProperties applicationProperties, LdapProperties ldapProperties, AmenagementRepository amenagementRepository, DossierAmenagementRepository dossierAmenagementRepository, DossierService dossierService, ObjectMapper objectMapper, MessageSource messageSource, UtilsService utilsService, EsupSignatureService esupSignatureService, MailService mailService, LogService logService, DocumentService documentService, LibelleAmenagementRepository libelleAmenagementRepository, UserOthersAffectationsRepository userOthersAffectationsRepository, PersonLdapRepository personLdapRepository, OrganizationalUnitLdapRepository organizationalUnitLdapRepository, DossierRepository dossierRepository, DataMappingService dataMappingService, TypeLigneAmenagementRepository typeLigneAmenagementRepository, AmenagementWorkflowService amenagementWorkflowService) {
        this.applicationProperties = applicationProperties;
        this.ldapProperties = ldapProperties;
        this.amenagementRepository = amenagementRepository;
        this.dossierAmenagementRepository = dossierAmenagementRepository;
        this.dossierService = dossierService;
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
        this.utilsService = utilsService;
        this.esupSignatureService = esupSignatureService;
        this.mailService = mailService;
        this.logService = logService;
        this.documentService = documentService;
        this.libelleAmenagementRepository = libelleAmenagementRepository;
        this.userOthersAffectationsRepository = userOthersAffectationsRepository;
        this.personLdapRepository = personLdapRepository;
        this.organizationalUnitLdapRepository = organizationalUnitLdapRepository;
        this.dossierRepository = dossierRepository;
        this.dataMappingService = dataMappingService;
        this.typeLigneAmenagementRepository = typeLigneAmenagementRepository;
        this.amenagementWorkflowService = amenagementWorkflowService;
    }

    public Amenagement getById(Long id) {
        return amenagementRepository.findById(id).orElseThrow();
    }

    @Transactional(readOnly = true)
    public Amenagement getByIdWithLignesAndTypes(Long id) {
        return amenagementRepository.findByIdWithLignesAndTypes(id).orElseThrow();
    }

    @Transactional
    public Page<Amenagement> findByDossier(Long dossierId) {
        Dossier dossier = dossierService.getById(dossierId);
        List<Amenagement> amenagements = dossierAmenagementRepository.findDossierAmenagementByDossier(dossier).stream().map(DossierAmenagement::getAmenagement).collect(Collectors.toList());
        return new PageImpl<>(amenagements, Pageable.unpaged(), amenagements.size());
    }

    public Amenagement getCurrentAmenagement(Long dossierId) {
        List<Amenagement> amenagements =  amenagementRepository.findByDossierIdAndStatusAmenagement(dossierId, StatusAmenagement.VISE_ADMINISTRATION);
        if(!amenagements.isEmpty() && (amenagements.get(0).getTypeAmenagement().equals(TypeAmenagement.CURSUS) || amenagements.get(0).getEndDate().isAfter(LocalDateTime.now()))) {
            return amenagements.get(0);
        }
        return null;
    }

    public Boolean isAmenagementTempsMajore(Long dossierId) {
        Dossier dossier = dossierService.getById(dossierId);
        List<DossierAmenagement> dossierAmenagements = dossierAmenagementRepository.findDossierAmenagementByDossier(dossier);
        if(!dossierAmenagements.isEmpty()) {
            return dossierAmenagements.stream().anyMatch(dossierAmenagement -> (dossierAmenagement.getAmenagement().getTempsMajore() != null && !dossierAmenagement.getAmenagement().getTempsMajore().equals(TempsMajore.AUCUN) && !dossierAmenagement.getAmenagement().getTempsMajore().equals(TempsMajore.TEMPSCOMP)) || StringUtils.hasText(dossierAmenagement.getAmenagement().getAutresTempsMajores()));
        }
        List<Amenagement> amenagements =  amenagementRepository.findByDossierIdAndStatusAmenagement(dossierId, StatusAmenagement.VISE_ADMINISTRATION);
        if(!amenagements.isEmpty() && (amenagements.get(0).getTypeAmenagement().equals(TypeAmenagement.CURSUS) || amenagements.get(0).getEndDate().isAfter(LocalDateTime.now()))) {
            return amenagements.stream().anyMatch(amenagement -> amenagement.getTempsMajore() != null || !amenagement.getTempsMajore().equals(TempsMajore.TEMPSCOMP) || !amenagement.getTempsMajore().equals(TempsMajore.AUCUN) || StringUtils.hasText(amenagement.getAutresTempsMajores()));
        }
        return null;
    }

    @Transactional
    public void softDeleteAmenagement(Long amenagementId) throws AgapeException {
        Amenagement amenagement = getById(amenagementId);
        List<DossierAmenagement> dossierAmenagements = dossierAmenagementRepository.findDossierAmenagementByAmenagement(amenagement);
        if(dossierAmenagements.stream().noneMatch(dossierAmenagement -> dossierAmenagement.getLastYear() == utilsService.getCurrentYear())) {
            throw new AgapeYearException();
        }
        if(amenagement.getStatusAmenagement().equals(StatusAmenagement.BROUILLON) || amenagement.getStatusAmenagement().equals(StatusAmenagement.ENVOYE) || amenagement.getStatusAmenagement().equals(StatusAmenagement.VALIDE_MEDECIN)) {
            amenagement.setStatusAmenagement(StatusAmenagement.SUPPRIME);
        } else {
            throw new AgapeException("Impossible de supprimer un aménagement qui n'est pas au statut brouillon, envoyé à la signature du médecin ou validé par le médecin");
        }
    }

    @Transactional
    public Amenagement create(AmenagementCreateDto dto, Long idDossier, PersonLdap personLdap) throws AgapeException {
        Dossier dossier = dossierService.getById(idDossier);
        if (dossier.getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        if (dto.getTypeAmenagement().equals(TypeAmenagement.DATE) && dto.getEndDate() == null) {
            throw new AgapeException("Impossible de créer l'aménagement sans date de fin");
        }
        if (dossier.getStatusDossier().equals(StatusDossier.IMPORTE) || dossier.getStatusDossier().equals(StatusDossier.AJOUT_MANUEL)) {
            dossierService.changeStatutDossier(idDossier, StatusDossier.RECU_PAR_LA_MEDECINE_PREVENTIVE, personLdap.getEduPersonPrincipalName());
        }
        Amenagement amenagement = new Amenagement();
        amenagement.setTypeAmenagement(dto.getTypeAmenagement());
        amenagement.setEndDate(dto.getEndDate());
        amenagement.setAutresTempsMajores(dto.getAutresTempsMajores());
        amenagement.setTempsMajore(dto.getTempsMajore());
        amenagement.setAutresTypeEpreuve(dto.getAutresTypeEpreuve());
        amenagement.setAutorisation(dto.getAutorisation());
        amenagement.setNomMedecin(personLdap.getDisplayName());
        amenagement.setMailMedecin(personLdap.getMail());
        if (!dto.getTypeEpreuves().contains(TypeEpreuve.AUCUN)) {
            amenagement.setTypeEpreuves(dto.getTypeEpreuves());
        } else {
            amenagement.getTypeEpreuves().add(TypeEpreuve.AUCUN);
        }
        if (Autorisation.OUI.equals(dto.getAutorisation()) && dto.getClassification() != null) {
            amenagement.getClassification().addAll(dto.getClassification());
        }
        updateDossierClassification(dossier, dto.getClassification(), dto.getAutorisation());
        dto.getLignesAmenagement().stream()
                .filter(LigneAmenagementDto::isSelected)
                .forEach(ligneDto -> {
                    TypeLigneAmenagement type = typeLigneAmenagementRepository.getReferenceById(ligneDto.getTypeLigneAmenagementId());
                    LigneAmenagement ligne = new LigneAmenagement();
                    ligne.setTypeLigneAmenagement(type);
                    ligne.setAmenagement(amenagement);
                    ligne.setCommentairePrecision(ligneDto.getCommentairePrecision());
                    if (type.isChampLibre()) {
                        ligne.setLibelleLibre(ligneDto.getLibelleLibre());
                    }
                    amenagement.getLignesAmenagement().add(ligne);
                });
        amenagementRepository.save(amenagement);
        dossierService.createDossierAmenagement(amenagement, dossier);
        return amenagement;
    }

    @Transactional
    public void update(Long amenagementId, AmenagementUpdateDto amenagement) throws AgapeJpaException {
        Amenagement amenagementToUpdate = getById(amenagementId);
        DossierAmenagement dossierAmenagement = getDossierAmenagementOfCurrentYear(amenagementToUpdate);
        if(dossierAmenagement.getDossier() == null) {
            throw new AgapeYearException();
        }
        if (amenagementToUpdate.getStatusAmenagement().equals(StatusAmenagement.BROUILLON)) {
            amenagementToUpdate.setTypeAmenagement(amenagement.getTypeAmenagement());
            amenagementToUpdate.setAutorisation(amenagement.getAutorisation());
            if (!amenagement.getTypeEpreuves().contains(TypeEpreuve.AUCUN)) {
                amenagementToUpdate.setTypeEpreuves(amenagement.getTypeEpreuves());
            } else {
                amenagement.getTypeEpreuves().clear();
                amenagement.getTypeEpreuves().add(TypeEpreuve.AUCUN);
            }
            amenagementToUpdate.setTypeEpreuves(amenagement.getTypeEpreuves());
            amenagementToUpdate.setAutresTypeEpreuve(amenagement.getAutresTypeEpreuve());
            amenagementToUpdate.setEndDate(amenagement.getEndDate());
            amenagementToUpdate.setTempsMajore(amenagement.getTempsMajore());
            amenagementToUpdate.setAutresTempsMajores(amenagement.getAutresTempsMajores());

            Set<Classification> selectedClassifications = amenagement.getClassification() != null
                    ? amenagement.getClassification()
                    : new HashSet<>();
            amenagementToUpdate.getClassification().clear();
            if(Autorisation.OUI.equals(amenagement.getAutorisation())) {
                amenagementToUpdate.getClassification().addAll(selectedClassifications);
            } else {
                selectedClassifications = new HashSet<>();
            }
            updateDossierClassification(dossierAmenagement.getDossier(), selectedClassifications, amenagement.getAutorisation());
            syncLignesAmenagement(amenagementToUpdate, amenagement.getLignesAmenagement());
            amenagementRepository.save(amenagementToUpdate);
        }
    }

    private void syncLignesAmenagement(Amenagement amenagement, List<LigneAmenagementDto> lignesAmenagementDto) {
        if (lignesAmenagementDto == null) {
            lignesAmenagementDto = List.of();
        }
        Map<Long, LigneAmenagementDto> dtoById = lignesAmenagementDto.stream()
                .filter(LigneAmenagementDto::isSelected)
                .filter(ligne -> ligne.getId() != null)
                .collect(Collectors.toMap(LigneAmenagementDto::getId, ligne -> ligne));

        amenagement.getLignesAmenagement().removeIf(ligne -> ligne.getId() != null && !dtoById.containsKey(ligne.getId()));

        amenagement.getLignesAmenagement().forEach(ligne -> {
            LigneAmenagementDto ligneDto = dtoById.get(ligne.getId());
            if (ligneDto != null) {
                ligne.setCommentairePrecision(ligneDto.getCommentairePrecision());
                if (ligne.getTypeLigneAmenagement().isChampLibre()) {
                    ligne.setLibelleLibre(ligneDto.getLibelleLibre());
                } else {
                    ligne.setLibelleLibre(null);
                }
            }
        });

        lignesAmenagementDto.stream()
                .filter(LigneAmenagementDto::isSelected)
                .filter(ligne -> ligne.getId() == null)
                .forEach(ligneDto -> {
                    TypeLigneAmenagement type = typeLigneAmenagementRepository.getReferenceById(ligneDto.getTypeLigneAmenagementId());
                    LigneAmenagement ligne = new LigneAmenagement();
                    ligne.setAmenagement(amenagement);
                    ligne.setTypeLigneAmenagement(type);
                    ligne.setCommentairePrecision(ligneDto.getCommentairePrecision());
                    if (type.isChampLibre()) {
                        ligne.setLibelleLibre(ligneDto.getLibelleLibre());
                    }
                    amenagement.getLignesAmenagement().add(ligne);
                });
    }

    @Transactional
    public void updateReferentValidation(Long amenagementId, AmenagementUpdateDto dto) throws AgapeException {
        Amenagement amenagement = getById(amenagementId);
        DossierAmenagement dossierAmenagement = getDossierAmenagementOfCurrentYear(amenagement);
        if (dossierAmenagement.getDossier() == null) {
            throw new AgapeYearException();
        }
        if (!amenagementWorkflowService.isPendingReferentValidation(amenagement)) {
            throw new AgapeException("Impossible de modifier un aménagement qui n'est pas en attente de validation référent");
        }

        Map<Long, LigneAmenagementDto> dtoById = dto.getLignesAmenagement().stream()
                .filter(ligne -> ligne.getId() != null)
                .collect(Collectors.toMap(LigneAmenagementDto::getId, ligne -> ligne));
        java.util.Set<Long> existingTypeIds = new java.util.HashSet<>();

        for (LigneAmenagement ligneAmenagement : amenagement.getLignesAmenagement()) {
            existingTypeIds.add(ligneAmenagement.getTypeLigneAmenagement().getId());
            LigneAmenagementDto ligneDto = dtoById.get(ligneAmenagement.getId());
            if (ligneDto == null || ligneDto.getStatut() == null) {
                throw new AgapeException("Le statut de chaque ligne d'aménagement doit être renseigné");
            }
            if ((StatutLigneAmenagement.REFUSE.equals(ligneDto.getStatut()) || StatutLigneAmenagement.MODIFIE.equals(ligneDto.getStatut()))
                    && !StringUtils.hasText(ligneDto.getCommentaireValidation())) {
                throw new AgapeException("Le commentaire de validation est obligatoire pour les lignes refusées ou modifiées");
            }
            ligneAmenagement.setStatut(ligneDto.getStatut());
            ligneAmenagement.setCommentaireValidation(StringUtils.hasText(ligneDto.getCommentaireValidation()) ? ligneDto.getCommentaireValidation().trim() : null);
        }

        dto.getLignesAmenagement().stream()
                .filter(LigneAmenagementDto::isSelected)
                .filter(ligneDto -> ligneDto.getId() == null)
                .filter(ligneDto -> !existingTypeIds.contains(ligneDto.getTypeLigneAmenagementId()))
                .forEach(ligneDto -> {
                    TypeLigneAmenagement type = typeLigneAmenagementRepository.getReferenceById(ligneDto.getTypeLigneAmenagementId());
                    LigneAmenagement ligne = new LigneAmenagement();
                    ligne.setAmenagement(amenagement);
                    ligne.setTypeLigneAmenagement(type);
                    ligne.setCommentairePrecision(ligneDto.getCommentairePrecision());
                    ligne.setStatut(StatutLigneAmenagement.ACCEPTE);
                    if (type.isChampLibre()) {
                        ligne.setLibelleLibre(ligneDto.getLibelleLibre());
                    }
                    amenagement.getLignesAmenagement().add(ligne);
                });

        amenagementRepository.save(amenagement);
    }

    public DossierAmenagement getDossierAmenagementOfCurrentYear(Amenagement amenagement) {
        List<DossierAmenagement> dossierAmenagements = dossierAmenagementRepository.findDossierAmenagementByAmenagement(amenagement);
        return dossierAmenagements.stream().filter(da -> da.getLastYear() == utilsService.getCurrentYear()).findFirst().orElse(null);
    }

    private void updateDossierClassification(Dossier dossier, Set<Classification> selectedClassifications, Autorisation autorisation) {
        if (dossier.getStatusDossier().equals(StatusDossier.RECU_PAR_LA_MEDECINE_PREVENTIVE)) {
            if(Autorisation.OUI.equals(autorisation)) {
                if (selectedClassifications != null && !selectedClassifications.isEmpty()) {
                    if((dossier.getClassifications().contains(Classification.NON_COMMUNIQUE) || dossier.getClassifications().contains(Classification.REFUS)) && dossier.getClassifications().stream().anyMatch(c -> c != null && !c.equals(Classification.NON_COMMUNIQUE) && !c.equals(Classification.REFUS) && !c.equals(Classification.TEMPORAIRE))) {
                        throw new AgapeRuntimeException("NON_COMMUNIQUE ou REFUS impossible avec une autre classification");
                    }
                    dossier.getClassifications().addAll(selectedClassifications);
                }
            } else if (Autorisation.NON.equals(autorisation)) {
                dossier.getClassifications().clear();
                dossier.getClassifications().add(Classification.REFUS);
            } else {
                dossier.getClassifications().clear();
                dossier.getClassifications().add(Classification.NON_COMMUNIQUE);
            }
        }
    }

    public Page<Amenagement> getFullTextSearchScol(StatusAmenagement statusAmenagement, List<String> codComposantes, String campus, String viewedByUid, String notViewedByUid, Integer yearFilter, Pageable pageable) {
        return amenagementRepository.findByFullTextSearchScol(statusAmenagement, codComposantes, campus, viewedByUid, notViewedByUid, yearFilter, pageable);
    }

    public Page<Amenagement> getByIndividuNameScol(String fullTextSearch, StatusAmenagement statusAmenagement, List<String> codComposantes, String campus, String viewedByUid, String notViewedByUid, Pageable pageable) {
        return amenagementRepository.findByIndividuNameScol(fullTextSearch, statusAmenagement, utilsService.getCurrentYear(), codComposantes, campus, viewedByUid, notViewedByUid, pageable);
    }

    public Page<Amenagement> findAllPaged(Pageable pageable) {
        return amenagementRepository.findAll(pageable);
    }

    public Page<Amenagement> getFullTextSearch(StatusAmenagement statusAmenagement, String codComposante, Integer yearFilter, Pageable pageable) {
        return amenagementRepository.findByFullTextSearch(statusAmenagement, codComposante, yearFilter, pageable);
    }

    public Page<Amenagement> getByIndividuNamePortable(String fullTextSearch, Pageable pageable) {
        return amenagementRepository.findByIndividuNamePortable(fullTextSearch, utilsService.getCurrentYear(), pageable);
    }

    public Page<Amenagement> getPortable(String codComposante, Integer yearFilter, Pageable pageable) {
        return amenagementRepository.findByPortable(codComposante, yearFilter, pageable);
    }

    public Page<Amenagement> getFullTextSearchPorte(String fullTextSearch, String codComposante, Integer yearFilter, Pageable pageable) {
        return amenagementRepository.findByFullTextSearchPortable(fullTextSearch, codComposante, yearFilter, pageable);
    }

    public Long countToValidate() {
        return amenagementRepository.countByStatusAmenagement(amenagementWorkflowService.getPendingAdministrationStatus(), utilsService.getCurrentYear());
    }

    public Long countToValidateReferent() {
        if (!amenagementWorkflowService.isReferentValidationEnabled()) {
            return 0L;
        }
        return amenagementRepository.countByStatusAmenagement(StatusAmenagement.VALIDE_MEDECIN, utilsService.getCurrentYear());
    }

    public boolean canAccessAmenagement(Long amenagementId, List<String> codComposantes) {
        if (codComposantes == null || codComposantes.isEmpty()) {
            return false;
        }
        Amenagement amenagement = getById(amenagementId);
        DossierAmenagement dossierAmenagement = getDossierAmenagementOfCurrentYear(amenagement);
        return dossierAmenagement != null
                && dossierAmenagement.getDossier() != null
                && codComposantes.contains(dossierAmenagement.getDossier().getCodComposante());
    }

    public void assertBelongsToDossier(Long amenagementId, Long dossierId) {
        Amenagement amenagement = getById(amenagementId);
        Dossier dossier = dossierService.getById(dossierId);
        if(dossierAmenagementRepository.findDossierAmenagementByDossierAndAmenagement(dossier, amenagement).isEmpty()) {
            throw new AccessDeniedException("Aménagement hors périmètre du dossier");
        }
    }

    public Long countToPorte() {
        return amenagementRepository.countToPorte(utilsService.getCurrentYear());
    }

    @Transactional
    public void validationMedecin(Long id, PersonLdap personLdap) throws AgapeException {
        Amenagement amenagement = getById(id);
        DossierAmenagement dossierAmenagement = getDossierAmenagementOfCurrentYear(amenagement);
        if(dossierAmenagement.getDossier() == null) {
            throw new AgapeYearException();
        }
        if(amenagement.getStatusAmenagement().equals(StatusAmenagement.BROUILLON)) {
            StatusAmenagement initialStatus = amenagement.getStatusAmenagement();
            amenagement.setValideMedecinDate(LocalDateTime.now());
            dossierAmenagement.setStatusDossierAmenagement(StatusDossierAmenagement.EN_ATTENTE);
            amenagement.setMailMedecin(personLdap.getMail());
            StatusAmenagement validatedStatus = amenagementWorkflowService.isReferentValidationEnabled()
                    ? StatusAmenagement.VALIDE_MEDECIN
                    : StatusAmenagement.VALIDE_REFERENT;
            if(!StringUtils.hasText(applicationProperties.getEsupSignatureAvisWorkflowId()) && StringUtils.hasText(applicationProperties.getEsupSignatureCertificatsWorkflowId())) {
                if (!amenagementWorkflowService.isReferentValidationEnabled()) {
                    sendToCertificatWorkflow(id);
                }
                amenagement.setStatusAmenagement(validatedStatus);
            } else if(StringUtils.hasText(applicationProperties.getEsupSignatureAvisWorkflowId())) {
                sendToAvisWorkflow(id);
                amenagement.setStatusAmenagement(StatusAmenagement.ENVOYE);
                //TODO lors de la suppression, supprimer dans esup-signature
            } else {
                try {
                    byte[] modelBytes = new ClassPathResource("models/avis.pdf").getInputStream().readAllBytes();
                    Document avis = documentService.createDocument(
                            new ByteArrayInputStream(generateDocument(amenagement, modelBytes, TypeWorkflow.AVIS, true)),
                            "Avis-" + dossierAmenagement.getDossier().getIndividu().getNumEtu() + "-" + amenagement.getId() + ".pdf",
                            "application/pdf", amenagement.getId(), Amenagement.class.getSimpleName(),
                            dossierAmenagement.getDossier());
                    amenagement.setAvis(avis);
                } catch (IOException e) {
                    throw new AgapeException("Impossible de générer l'avis");
                }
                amenagement.setStatusAmenagement(validatedStatus);
                logger.info("aménagement : " + amenagement.getId() + " validé par " + personLdap.getMail());
            }
            logService.create(personLdap, dossierAmenagement.getDossier().getId(), "AMENAGEMENT", initialStatus.name(), amenagement.getStatusAmenagement().name());
            dossierService.syncStatusDossierAmenagement(dossierAmenagement.getDossier().getId());
            sendReferentAlertIfNeeded(amenagement);
        } else {
            throw new AgapeException("Impossible de valider un aménagement qui n'est pas au statut brouillon");
        }
    }

    @Transactional
    public void validationReferent(Long amenagementId, PersonLdap personLdap) throws AgapeException {
        if (!amenagementWorkflowService.isReferentValidationEnabled()) {
            throw new AgapeException("La validation par les référents n'est pas activée");
        }
        Amenagement amenagement = getById(amenagementId);
        DossierAmenagement dossierAmenagement = getDossierAmenagementOfCurrentYear(amenagement);
        if(dossierAmenagement.getDossier() == null) {
            throw new AgapeYearException();
        }
        if(amenagement.getStatusAmenagement().equals(StatusAmenagement.VALIDE_MEDECIN)) {
            if (amenagement.getLignesAmenagement().stream().anyMatch(ligne -> ligne.getStatut() == null)) {
                throw new AgapeException("Toutes les lignes d'aménagement doivent être évaluées avant transmission à l'administration");
            }
            StatusAmenagement initialStatus = amenagement.getStatusAmenagement();
            amenagement.setStatusAmenagement(StatusAmenagement.VALIDE_REFERENT);
            logService.create(personLdap, dossierAmenagement.getDossier().getId(), "AMENAGEMENT", initialStatus.name(), amenagement.getStatusAmenagement().name());
            amenagementRepository.saveAndFlush(amenagement);
            if(StringUtils.hasText(applicationProperties.getEsupSignatureCertificatsWorkflowId())) {
                sendToCertificatWorkflow(amenagementId);
            }
            logger.info("aménagement : " + amenagementId + " validé par le référent");
        } else {
            throw new AgapeException("Impossible de valider un aménagement qui n'est pas au statut Validé par le médecin");
        }
    }

    @Transactional
    public void sendToCertificatWorkflow(Long id) throws AgapeException {
        Amenagement amenagement = getById(id);
        try {
            byte[] modelBytes;
            if(StringUtils.hasText(applicationProperties.getModelsPath())) {
                modelBytes = Files.readAllBytes(new File(applicationProperties.getModelsPath() + "/certificat.pdf").toPath());
            } else {
                modelBytes = new ClassPathResource("models/certificat.pdf").getInputStream().readAllBytes();
            }
            esupSignatureService.send(amenagement, generateDocument(amenagement, modelBytes, TypeWorkflow.CERTIFICAT, false), TypeWorkflow.CERTIFICAT);
            logger.info("send amenagement " + id + " to esup-signature");
        } catch (IOException e) {
            logger.warn(e.getMessage());
            throw new AgapeException("Envoi vers esup-signature impossible", e);
        }
    }

    @Transactional
    public void sendToAvisWorkflow(Long id) throws AgapeException {
        Amenagement amenagement = getById(id);
        try {
            byte[] modelBytes;
            if(StringUtils.hasText(applicationProperties.getModelsPath())) {
                modelBytes = Files.readAllBytes(new File(applicationProperties.getModelsPath() + "/avis.pdf").toPath());
            } else {
                modelBytes = new ClassPathResource("models/avis.pdf").getInputStream().readAllBytes();
            }
            if (!amenagementWorkflowService.isReferentValidationEnabled()) {
                esupSignatureService.send(amenagement, generateDocument(amenagement, modelBytes, TypeWorkflow.CERTIFICAT, false), TypeWorkflow.CERTIFICAT);
            }
            esupSignatureService.send(amenagement, generateDocument(amenagement, modelBytes, TypeWorkflow.AVIS, false), TypeWorkflow.AVIS);
        } catch (IOException e) {
            throw new AgapeException("Envoi vers esup-signature impossible", e);
        }
    }

    @Transactional
    public void validationAdministration(Long amenagementId, PersonLdap personLdap) throws Exception {
        Amenagement amenagement = getById(amenagementId);
        DossierAmenagement dossierAmenagement = getDossierAmenagementOfCurrentYear(amenagement);
        if(dossierAmenagement.getDossier() == null) {
            throw new AgapeYearException();
        }
        if(amenagementWorkflowService.isPendingAdministrationValidation(amenagement)) {
            if(!StringUtils.hasText(applicationProperties.getEsupSignatureUrl())) {
                StatusAmenagement initialStatus = amenagement.getStatusAmenagement();
                amenagement.setAdministrationDate(LocalDateTime.now());
                amenagement.setStatusAmenagement(StatusAmenagement.VISE_ADMINISTRATION);
                amenagement.setNomValideur(personLdap.getDisplayName());
                amenagement.setUidValideur(personLdap.getUid());
                dossierAmenagement.setStatusDossierAmenagement(StatusDossierAmenagement.VALIDE);
                byte[] modelBytes = new ClassPathResource("models/certificat.pdf").getInputStream().readAllBytes();
                Document certificat = documentService.createDocument(
                        new ByteArrayInputStream(generateDocument(amenagement, modelBytes, TypeWorkflow.CERTIFICAT, true)),
                        "Certificat-" + dossierAmenagement.getDossier().getIndividu().getNumEtu() + "-" + amenagement.getId() + ".pdf",
                        "application/pdf", amenagement.getId(), Amenagement.class.getSimpleName(),
                        dossierAmenagement.getDossier());
                amenagement.setCertificat(certificat);
                logService.create(personLdap, dossierAmenagement.getDossier().getId(), "AMENAGEMENT", initialStatus.name(), amenagement.getStatusAmenagement().name());
                dossierService.syncStatusDossierAmenagement(dossierAmenagement.getDossier().getId());
                amenagementRepository.save(amenagement);
                sendAlert(amenagement);
                sendAmenagementToIndividu(amenagement.getId(), false);
            }
        } else {
            throw new AgapeException("Impossible de valider un aménagement qui n'est pas en attente de validation administrative");
        }
    }

    @Transactional
    public void refusAdministration(Long id, PersonLdap personLdap, String motif) throws AgapeException {
        Amenagement amenagement = getById(id);
        DossierAmenagement dossierAmenagement = getDossierAmenagementOfCurrentYear(amenagement);
        if(dossierAmenagement.getDossier() == null) {
            throw new AgapeYearException();
        }
        if(amenagementWorkflowService.isPendingAdministrationValidation(amenagement)) {
            StatusAmenagement initialStatus = amenagement.getStatusAmenagement();
            amenagement.setAdministrationDate(LocalDateTime.now());
            // Débloquer la saisie par le référent en repassant au statut précédent
            amenagement.setStatusAmenagement(amenagementWorkflowService.isReferentValidationEnabled() ? StatusAmenagement.VALIDE_MEDECIN : StatusAmenagement.BROUILLON);
            amenagement.setNomValideur(personLdap.getDisplayName());
            amenagement.setUidValideur(personLdap.getUid());
            amenagement.setMotifRefus(motif);

            // Historique des refus
            try {
                List<Map<String, String>> history;
                if (StringUtils.hasText(amenagement.getRefusHistory())) {
                    history = objectMapper.readValue(amenagement.getRefusHistory(), new TypeReference<>() {});
                } else {
                    history = new ArrayList<>();
                }
                Map<String, String> entry = new HashMap<>();
                entry.put("date", LocalDateTime.now().toString());
                entry.put("author", personLdap.getDisplayName());
                entry.put("motif", motif);
                history.add(entry);
                amenagement.setRefusHistory(objectMapper.writeValueAsString(history));
            } catch (JsonProcessingException e) {
                logger.error("Error writing refusal history", e);
            }

            dossierAmenagement.setStatusDossierAmenagement(StatusDossierAmenagement.REFUSE);
            logService.create(personLdap, dossierAmenagement.getDossier().getId(), "AMENAGEMENT", initialStatus.name(), StatusAmenagement.REFUSE_ADMINISTRATION.name());
            dossierService.syncStatusDossierAmenagement(dossierAmenagement.getDossier().getId());
            logger.info("amenagement " + id + " refused");

        } else {
            throw new AgapeException("Impossible de refuser un aménagement qui n'est pas en attente de validation administrative");
        }
    }

    @Transactional
    public void getCertificat(Long id, HttpServletResponse httpServletResponse) throws IOException, AgapeException {
        Amenagement amenagement = getById(id);
        if(!amenagement.getStatusAmenagement().equals(StatusAmenagement.VISE_ADMINISTRATION)) {
            throw new AgapeException("Le certificat ne peut pas être émis");
        }
        byte[] certificat;
        if(amenagement.getCertificat() != null ) {
            certificat = amenagement.getCertificat().getInputStream().readAllBytes();
        } else {
            certificat = generateDocument(amenagement, loadCertificatModelBytes(), TypeWorkflow.CERTIFICAT, true);
        }
        httpServletResponse.getOutputStream().write(certificat);
    }

    @Transactional
    public void getCertificatPreview(Long id, HttpServletResponse httpServletResponse) throws IOException, AgapeException {
        Amenagement amenagement = getById(id);
        if (!amenagementWorkflowService.isPendingAdministrationValidation(amenagement)
                && !amenagement.getStatusAmenagement().equals(StatusAmenagement.REFUSE_ADMINISTRATION)) {
            throw new AgapeException("L'aperçu du certificat ne peut pas être émis");
        }
        byte[] certificat = generateDocument(amenagement, loadCertificatModelBytes(), TypeWorkflow.CERTIFICAT, false);
        httpServletResponse.getOutputStream().write(certificat);
    }

    @Transactional
    public void getAvis(Long id, HttpServletResponse httpServletResponse) throws IOException, AgapeException {
        Amenagement amenagement = getById(id);
        if(!(amenagement.getStatusAmenagement().equals(StatusAmenagement.BROUILLON)
                || amenagement.getStatusAmenagement().equals(StatusAmenagement.VALIDE_MEDECIN)
                || amenagement.getStatusAmenagement().equals(StatusAmenagement.VALIDE_REFERENT)
                || amenagement.getStatusAmenagement().equals(StatusAmenagement.VISE_ADMINISTRATION)
                || amenagement.getStatusAmenagement().equals(StatusAmenagement.REFUSE_ADMINISTRATION))) {
            throw new AgapeException("L'avis ne peut pas être émis");
        }
        byte[] avis;
        if(amenagement.getAvis() != null ) {
            avis = amenagement.getAvis().getInputStream().readAllBytes();
        } else {
            byte[] modelBytes;
            if(StringUtils.hasText(applicationProperties.getModelsPath())) {
                modelBytes = Files.readAllBytes(new File(applicationProperties.getModelsPath() + "/avis.pdf").toPath());
            } else {
                modelBytes = new ClassPathResource("models/avis.pdf").getInputStream().readAllBytes();
            }
            avis = generateDocument(amenagement, modelBytes, TypeWorkflow.AVIS, true);
        }
        httpServletResponse.getOutputStream().write(avis);
    }

    private byte[] loadCertificatModelBytes() throws IOException {
        if(StringUtils.hasText(applicationProperties.getModelsPath())) {
            return Files.readAllBytes(new File(applicationProperties.getModelsPath() + "/certificat.pdf").toPath());
        }
        return new ClassPathResource("models/certificat.pdf").getInputStream().readAllBytes();
    }

    private byte[] generateDocument(Amenagement amenagement, byte[] modelBytes, TypeWorkflow typeWorkflow, boolean withSign) throws IOException {
        CertificatPdf certificatPdf = new CertificatPdf();
        DossierAmenagement dossierAmenagement = amenagement.getDossierAmenagements().stream().toList().get(0);
        if(dossierAmenagement.getDossier() == null) {
            throw new AgapeYearException();
        }
        Dossier dossier = dossierAmenagement.getDossier();
        certificatPdf.setName(dossier.getIndividu().getName());
        certificatPdf.setFirstname(dossier.getIndividu().getFirstName());
        certificatPdf.setDateOfBirth(dossier.getIndividu().getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        certificatPdf.setLibelleFormation(dossier.getLibelleFormation());
        certificatPdf.setSite(dossier.getComposante());
        certificatPdf.setAddress(dossier.getIndividu().getFixAddress() + " " + dossier.getIndividu().getFixCP() + " " + dossier.getIndividu().getFixCity());
        certificatPdf.setNumEtu(dossier.getIndividu().getNumEtu());
        if(amenagement.getTypeAmenagement().equals(TypeAmenagement.CURSUS)) {
            certificatPdf.setEndDate(messageSource.getMessage("amenagement.typeAmenagement.CURSUS", null, Locale.getDefault()));
        } else {
            certificatPdf.setEndDate("Jusqu’à la date de fin : " + amenagement.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        certificatPdf.setTypeEpreuves(amenagement.getTypeEpreuves().stream().map(typeEpreuve -> messageSource.getMessage("amenagement.typeEpreuve." + typeEpreuve.name(), null, Locale.getDefault())).collect(Collectors.joining(", ")));
        certificatPdf.setTempsMajore(messageSource.getMessage("amenagement.tempsMajore." + amenagement.getTempsMajore().name(), null, Locale.getDefault()));
        StringBuilder amenagementsWithNumbers = new StringBuilder();
        int i = 1;
        if (amenagement.getLignesAmenagement() != null && !amenagement.getLignesAmenagement().isEmpty()) {
            java.util.stream.Stream<LigneAmenagement> lignesStream = amenagement.getLignesAmenagement().stream();
            List<LigneAmenagement> lignes = lignesStream.toList();
            for (LigneAmenagement ligne : lignes) {
                String libelle = ligne.getTypeLigneAmenagement().isChampLibre()
                        ? ligne.getLibelleLibre()
                        : ligne.getTypeLigneAmenagement().getLibelle();
                if (libelle != null && !libelle.isBlank()) {
                    String status = ligne.getStatut() != null ? messageSource.getMessage("amenagement.statutLigneAmenagement." + ligne.getStatut().name(), null, Locale.getDefault()) : "";
                    String comment = ligne.getCommentaireValidation() != null ? ligne.getCommentaireValidation() : "";
                    Integer displayNumber = ligne.getTypeLigneAmenagement().getOrdre() != null ? ligne.getTypeLigneAmenagement().getOrdre() : i;
                    amenagementsWithNumbers.append(displayNumber).append(" - ").append(libelle);
                    if (StringUtils.hasText(status) || StringUtils.hasText(comment)) {
                        amenagementsWithNumbers.append(" (").append(status).append(StringUtils.hasText(comment) ? " : " + comment : "").append(")");
                    }
                    amenagementsWithNumbers.append("\n");
                    i++;
                }
            }
        } else if (amenagement.getAmenagementText() != null && !amenagement.getAmenagementText().isBlank()) {
            for (String line : amenagement.getAmenagementText().split("\n")) {
                if (!line.isBlank()) {
                    appendAmenagementLine(amenagementsWithNumbers, null, i, line);
                    i++;
                }
            }
        }
        certificatPdf.setAutresTypeEpreuve(amenagement.getAutresTypeEpreuve());
        certificatPdf.setAutresTempsMajores(amenagement.getAutresTempsMajores());
        certificatPdf.setAmenagementText(amenagementsWithNumbers.toString());
        if (amenagement.getValideMedecinDate() != null) {
            certificatPdf.setValideMedecinDate(amenagement.getValideMedecinDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        certificatPdf.setNomMedecin(amenagement.getNomMedecin());
        if(amenagement.getStatusAmenagement().equals(StatusAmenagement.VISE_ADMINISTRATION) && typeWorkflow.equals(TypeWorkflow.CERTIFICAT)) {
            if(amenagement.getAdministrationDate() != null) {
                certificatPdf.setAdministrationDate(amenagement.getAdministrationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            certificatPdf.setNomValideur(amenagement.getNomValideur());
        }
        TypeReference<Map<String, String>> datasTypeReference = new TypeReference<>(){};
        return generatePdf(amenagement, objectMapper.convertValue(certificatPdf, datasTypeReference), modelBytes, withSign);
    }

    private void appendAmenagementLine(StringBuilder amenagementsWithNumbers, Integer ordre, int fallbackNumber, String libelle) {
        Integer displayNumber = ordre != null ? ordre : fallbackNumber;
        amenagementsWithNumbers.append(displayNumber).append(" - ").append(libelle).append("\n");
    }

    private byte[] generatePdf(Amenagement amenagement, Map<String, String> datas, byte[] model, boolean withSign) throws IOException {
        try (PDDocument document = Loader.loadPDF(model);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDAcroForm pdAcroForm = document.getDocumentCatalog().getAcroForm();
            if (pdAcroForm == null) {
                throw new IOException("Le modèle PDF ne contient pas d'AcroForm");
            }

            byte[] ttfBytes = new ClassPathResource("/static/fonts/LiberationSans-Regular.ttf").getInputStream().readAllBytes();
            PDFont pdFont = PDTrueTypeFont.load(document, new ByteArrayInputStream(ttfBytes), WinAnsiEncoding.INSTANCE);
            PDResources resources = pdAcroForm.getDefaultResources();
            if (resources == null) {
                resources = new PDResources();
            }
            resources.put(COSName.getPDFName("LiberationSans"), pdFont);
            pdAcroForm.setDefaultResources(resources);
            pdAcroForm.setNeedAppearances(false);

            List<PDField> fields = new ArrayList<>();
            for (PDField field : pdAcroForm.getFieldTree()) {
                fields.add(field);
            }

            for (PDField pdField : fields) {
                String fieldName = pdField.getFullyQualifiedName();
                if (pdField instanceof PDSignatureField) {
                    if (withSign && !pdField.getWidgets().isEmpty()) {
                        addVisualSignature(amenagement, document, pdField.getWidgets().get(0).getRectangle(), fieldName);
                    }
                } else {
                    pdField.getCOSObject().setString(COSName.DA, "/LiberationSans 11 Tf 0 g");
                    if (datas.containsKey(fieldName) && datas.get(fieldName) != null) {
                        pdField.setValue(datas.get(fieldName));
                    }
                }
            }

            pdAcroForm.refreshAppearances();

            List<PDField> dates = fields.stream()
                    .filter(field -> field.getFullyQualifiedName().equals("administrationDate"))
                    .toList();
            List<PDField> cleanedFields = fields.stream()
                    .filter(field -> !(field instanceof PDSignatureField) && !field.getFullyQualifiedName().equals("administrationDate"))
                    .toList();

            for (PDField field : cleanedFields) {
                for (PDAnnotationWidget pdAnnotationWidget : field.getWidgets()) {
                    if (pdAnnotationWidget.getPage() == null && document.getNumberOfPages() > 0) {
                        pdAnnotationWidget.setPage(document.getPage(0));
                    }
                }
                pdAcroForm.flatten(Collections.singletonList(field), false);
            }

            if (!dates.isEmpty() && pdAcroForm.getFields().stream().noneMatch(field -> field.getFullyQualifiedName().equals("administrationDate"))) {
                pdAcroForm.getFields().add(dates.get(0));
            }

            document.save(out);
            return out.toByteArray();
        }
    }

    private void addVisualSignature(Amenagement amenagement, PDDocument doc, PDRectangle signRectangle, String fieldName) {
        try {
            File signImage;
            if (StringUtils.hasText(applicationProperties.getSignaturesPath())) {
                signImage = new File(applicationProperties.getSignaturesPath() + "/signature-" + amenagement.getUidValideur() + ".jpg");
                if (!signImage.exists()) {
                    logger.warn("Image de signature absente pour l'aménagement {} : {}", amenagement.getId(), signImage.getAbsolutePath());
                    return;
                }
            } else {
                File tmpDir = Files.createTempDirectory("esupagape").toFile();
                signImage = new File(tmpDir, "signImage.jpg");
                ClassPathResource signImgResource = new ClassPathResource("/static/images/signature-" + amenagement.getUidValideur() + ".jpg");
                if (!signImgResource.exists()) {
                    signImgResource = new ClassPathResource("/static/images/" + fieldName + ".jpg");
                }
                if (!signImgResource.exists()) {
                    logger.warn("Aucune image de signature trouvée pour l'aménagement {} et le champ {}", amenagement.getId(), fieldName);
                    return;
                }
                FileUtils.copyInputStreamToFile(signImgResource.getInputStream(), signImage);
            }

            PDImageXObject img = PDImageXObject.createFromFileByExtension(signImage, doc);
            float ratio = img.getHeight() / signRectangle.getHeight();
            try (PDPageContentStream cs = new PDPageContentStream(doc, doc.getPage(0), PDPageContentStream.AppendMode.APPEND, false)) {
                cs.drawImage(img, signRectangle.getLowerLeftX(), signRectangle.getUpperRightY() - (img.getHeight() / ratio), img.getWidth() / ratio, img.getHeight() / ratio);
            }
        } catch (IOException e) {
            logger.warn("Impossible d'ajouter la signature visuelle pour l'aménagement {}", amenagement.getId(), e);
        }
    }

    @Transactional
    public void porteAdministration(Long id, PersonLdap personLdap) {
        Amenagement amenagement = getById(id);
        DossierAmenagement dossierAmenagement = getDossierAmenagementOfCurrentYear(amenagement);
        Dossier currentDossier;
        if(dossierAmenagement != null) {
            currentDossier = dossierAmenagement.getDossier();
            if (currentDossier.getStatusDossier().equals(StatusDossier.IMPORTE) || currentDossier.getStatusDossier().equals(StatusDossier.AJOUT_MANUEL)) {
                dossierService.changeStatutDossier(currentDossier.getId(), StatusDossier.RECONDUIT, personLdap.getEduPersonPrincipalName());
            }
        } else {
            currentDossier = dossierService.create(personLdap.getEduPersonPrincipalName(), dossierAmenagementRepository.findDossierAmenagementByAmenagement(amenagement).get(0).getDossier().getIndividu().getId(), TypeIndividu.ETUDIANT, StatusDossier.RECONDUIT);
            dossierAmenagement = dossierService.createDossierAmenagement(amenagement, currentDossier);
        }
        dossierAmenagement.setDossier(currentDossier);
        dossierAmenagement.setStatusDossierAmenagement(StatusDossierAmenagement.PORTE);
        dossierAmenagement.setMailValideurPortabilite(personLdap.getMail());
        dossierAmenagement.setNomValideurPortabilite(personLdap.getDisplayName());
        dossierService.syncStatusDossierAmenagement(dossierAmenagement.getDossier().getId());
        logger.info("porte amenagement " + id + " by " + personLdap.getMail());
//        sendAlert(amenagement);
//        sendAmenagementToIndividu(id, true);
    }

    @Transactional
    public void rejectAdministration(Long id, PersonLdap personLdap) {
        Amenagement amenagement = getById(id);
        DossierAmenagement dossierAmenagement = amenagement.getDossierAmenagements().stream().toList().get(0);
        Dossier currentDossier;
        try {
            currentDossier = dossierAmenagement.getDossier();
            if(currentDossier.getStatusDossier().equals(StatusDossier.IMPORTE) || currentDossier.getStatusDossier().equals(StatusDossier.AJOUT_MANUEL)) {
                dossierService.changeStatutDossier(currentDossier.getId(), StatusDossier.NON_RECONDUIT, personLdap.getEduPersonPrincipalName());
            }
        } catch (AgapeJpaException e) {
            currentDossier = dossierService.create(personLdap.getEduPersonPrincipalName(), dossierAmenagementRepository.findDossierAmenagementByAmenagement(amenagement).get(0).getDossier().getIndividu().getId(), TypeIndividu.ETUDIANT, StatusDossier.NON_RECONDUIT);
            dossierAmenagement = dossierService.createDossierAmenagement(amenagement, currentDossier);
        }
        dossierAmenagement.setDossier(currentDossier);
        amenagement.setStatusAmenagement(StatusAmenagement.REFUSE_ADMINISTRATION);
        dossierAmenagement.setStatusDossierAmenagement(StatusDossierAmenagement.REFUSE);
        dossierAmenagement.setMailValideurPortabilite(personLdap.getMail());
        dossierAmenagement.setNomValideurPortabilite(personLdap.getDisplayName());
        dossierAmenagement.setLastUpdate(LocalDateTime.now());
        dossierService.syncStatusDossierAmenagement(dossierAmenagement.getDossier().getId());
        logger.info("refuse amenagement " + id + " by " + personLdap.getMail());

    }

    @Transactional
    public SignatureStatus checkEsupSignatureStatus(Long amenagementId, TypeWorkflow typeWorkflow) {
        Amenagement amenagement = getById(amenagementId);
        DossierAmenagement dossierAmenagement = getDossierAmenagementOfCurrentYear(amenagement);
        StatusAmenagement previousStatus = amenagement.getStatusAmenagement();
        SignatureStatus signatureStatus = esupSignatureService.getStatus(dossierAmenagement, typeWorkflow);
        if (previousStatus != amenagement.getStatusAmenagement()) {
            sendReferentAlertIfNeeded(amenagement);
        }
        if(signatureStatus.equals(SignatureStatus.COMPLETED)) {
            esupSignatureService.getLastPdf(dossierAmenagement, typeWorkflow);
            logger.info("aménagement " + amenagementId + " status esup-signature " + typeWorkflow.name() + " : COMPLETED");
        }
        return signatureStatus;
    }

    @Transactional
    public void syncEsupSignature(Long amenagementId) throws AgapeException {
        Amenagement amenagement = getById(amenagementId);
        if(StringUtils.hasText(applicationProperties.getEsupSignatureUrl())) {
            if (amenagementWorkflowService.isPendingAdministrationValidation(amenagement)) {
                if(amenagement.getCertificatSignatureStatus() == null) {
                    sendToCertificatWorkflow(amenagementId);
                }
                checkEsupSignatureStatus(amenagementId, TypeWorkflow.CERTIFICAT);
            } else if (amenagement.getStatusAmenagement().equals(StatusAmenagement.ENVOYE)) {
                SignatureStatus signatureStatus = checkEsupSignatureStatus(amenagementId, TypeWorkflow.AVIS);
                if(signatureStatus.equals(SignatureStatus.COMPLETED)
                        && !amenagementWorkflowService.isReferentValidationEnabled()
                        && amenagement.getCertificatSignatureStatus() == null) {
                    sendToCertificatWorkflow(amenagementId);
                }
            }
        }
    }

    @Transactional
    public void syncEsupSignatureAmenagements() throws AgapeException {
        List<StatusAmenagement> statusesToSync = amenagementWorkflowService.getStatusesToSyncEsupSignature();
        List<Amenagement> amenagementsToSync = dossierAmenagementRepository.findDossierAmenagementByLastYear(utilsService.getCurrentYear()).stream()
                .map(DossierAmenagement::getAmenagement)
                .filter(amenagement -> statusesToSync.contains(amenagement.getStatusAmenagement()))
                .toList();
        logger.debug(amenagementsToSync.size() + " aménagements à synchroniser");
        for(Amenagement amenagement : amenagementsToSync) {
            syncEsupSignature(amenagement.getId());
        }
    }

    @Transactional
    public void syncAmenagement(Long amenagementId) {
        try {
            Amenagement amenagement = getById(amenagementId);
            Dossier dossier = amenagement.getDossierByYear(utilsService.getCurrentYear());
            LocalDateTime now = LocalDateTime.now().minusDays(1);
            DossierAmenagement dossierAmenagement = getDossierAmenagementOfCurrentYear(amenagement);
            if(dossierAmenagement != null) {
                if(amenagement.getTypeAmenagement().equals(TypeAmenagement.DATE)) {
                    if (!StatusDossierAmenagement.EXPIRE.equals(dossierAmenagement.getStatusDossierAmenagement())
                        && (amenagement.getEndDate().isBefore(now) || amenagement.getEndDate().equals(now))
                        && amenagement.getStatusAmenagement().equals(StatusAmenagement.VISE_ADMINISTRATION)) {
                        logger.info("amenagement " + amenagement.getId() + " EXPIRE");
                        StatusDossierAmenagement initialStatus = dossierAmenagement.getStatusDossierAmenagement();
                        dossierAmenagement.setStatusDossierAmenagement(StatusDossierAmenagement.EXPIRE);
                        logService.create("SYSTEM", dossierAmenagement.getDossier().getId(), "AMENAGEMENT", initialStatus.name(), StatusDossierAmenagement.EXPIRE.name());
                    } else {
                        dossier.setStatusDossierAmenagement(StatusDossierAmenagement.VALIDE);
                        dossierAmenagement.setStatusDossierAmenagement(StatusDossierAmenagement.VALIDE);
                        logger.info("amenagement " + amenagement.getId() + " valide");
                    }
                }
            } else {
                Optional<DossierAmenagement> lastDossierAmenagement = amenagement.getDossierAmenagements().stream().max(Comparator.comparingInt(DossierAmenagement::getLastYear));
                if (lastDossierAmenagement.isPresent()
                        && amenagement.getTypeAmenagement().equals(TypeAmenagement.DATE)
                        && amenagement.getEndDate().isAfter(now)
                        && amenagement.getStatusAmenagement().equals(StatusAmenagement.VISE_ADMINISTRATION)
                        && BooleanUtils.isNotTrue(lastDossierAmenagement.get().getDossier().getIndividu().getDesinscrit())) {
                    if (dossier == null) {
                        Dossier lastDossier = dossierAmenagementRepository.findDossierAmenagementByAmenagement(amenagement).get(0).getDossier();
                        dossier = dossierService.create("system", lastDossier.getIndividu().getId(), TypeIndividu.ETUDIANT, StatusDossier.RECONDUIT);
                        dossierAmenagement = dossierService.createDossierAmenagement(amenagement, dossier);
                        dossierAmenagement.setStatusDossierAmenagement(StatusDossierAmenagement.VALIDE);
                        logService.create("SYSTEM", dossier.getId(), "AMENAGEMENT", "RECONDUCTION", StatusDossierAmenagement.VALIDE.name());
                        logger.info("amenagement " + amenagement.getId() + " reconduit");
                    } else if (dossier.getDossierAmenagements().stream().noneMatch(da -> da.getAmenagement().equals(amenagement))) {
                        dossierAmenagement = dossierService.createDossierAmenagement(amenagement, dossier);
                        logService.create("SYSTEM", dossier.getId(), "AMENAGEMENT", "RECONDUCTION", StatusDossierAmenagement.VALIDE.name());
                        logger.info("amenagement " + amenagement.getId() + " reconduit");
                        dossierAmenagement.setStatusDossierAmenagement(StatusDossierAmenagement.VALIDE);
                    }
                    dossier.setStatusDossierAmenagement(StatusDossierAmenagement.VALIDE);
                }
            }
            if (amenagement.getIndividuSendDate() == null) {
                amenagementRepository.save(amenagement);
                sendAlert(amenagement);
                sendAmenagementToIndividu(amenagement.getId(), false);
                amenagement.setIndividuSendDate(LocalDateTime.now());
                amenagementRepository.save(amenagement);
            }
            if(dossier != null) {
                dossierRepository.save(dossier);
                dossierService.syncStatusDossierAmenagement(dossier.getId());
            }
        } catch (Exception e) {
            logger.warn(e.getMessage() + " on sync amenagement " + amenagementId, e);
        }
    }

//    @Transactional
//    public void sendAmenagementToIndividu(Long amenagementId, boolean force) throws Exception {
//        Amenagement amenagement = getById(amenagementId);
//        sendAmenagementToIndividu(amenagement, force);
//    }

    @Transactional
    public void sendAmenagementToIndividu(Long amenagementId, boolean force) throws Exception {
        Amenagement amenagement = getById(amenagementId);
        DossierAmenagement dossierAmenagement = getDossierAmenagementOfCurrentYear(amenagement);
        String to = dossierAmenagement.getDossier().getIndividu().getEmailEtu();
        if(StringUtils.hasText(applicationProperties.getTestEmail())) to = applicationProperties.getTestEmail();
        if((force || amenagement.getIndividuSendDate() == null) && amenagement.getStatusAmenagement().equals(StatusAmenagement.VISE_ADMINISTRATION)) {
            byte[] certificat;
            if(amenagement.getCertificat() != null ) {
                certificat = documentService.getDocument(amenagement.getId());
            } else {
                byte[] modelBytes = new ClassPathResource("models/certificat.pdf").getInputStream().readAllBytes();
                certificat = generateDocument(amenagement, modelBytes, TypeWorkflow.CERTIFICAT, true);
            }
            mailService.sendCertificat(new ByteArrayInputStream(certificat), to);
            amenagement.setIndividuSendDate(LocalDateTime.now());
            logger.info("amenagement " + amenagement.getId() + " sended to " + to);
        }
    }

    public void sendAlert(Amenagement amenagement) {
        List<String> to = resolveAlertRecipients(amenagement, ldapProperties.getScolariteMemberOfSearch());
        if(amenagement.getStatusAmenagement().equals(StatusAmenagement.VISE_ADMINISTRATION) && amenagement.getIndividuSendDate() == null) {
            try {
                if(!to.isEmpty()) {
                    logger.info("Mail d'alerte envoyer, aménagement : " + amenagement.getId() + " to " + to);
                    mailService.sendAlert(to);
                }
            } catch (Exception e) {
                logger.warn("Impossible d'envoyer le mail d'alerte, aménagement : " + amenagement.getId(), e);
            }
        }
    }

    public void sendReferentAlert(Amenagement amenagement) {
        List<String> to = resolveAlertRecipients(amenagement, ldapProperties.getReferentMemberOfSearch());
        if (!amenagementWorkflowService.isReferentValidationEnabled()
                || !amenagement.getStatusAmenagement().equals(StatusAmenagement.VALIDE_MEDECIN)) {
            return;
        }
        try {
            if(!to.isEmpty()) {
                logger.info("Mail d'alerte referent envoyer, aménagement : " + amenagement.getId() + " to " + to);
                mailService.sendReferentAlert(to);
            }
        } catch (Exception e) {
            logger.warn("Impossible d'envoyer le mail d'alerte referent, aménagement : " + amenagement.getId(), e);
        }
    }

    private void sendReferentAlertIfNeeded(Amenagement amenagement) {
        if (amenagementWorkflowService.isPendingReferentValidation(amenagement)) {
            sendReferentAlert(amenagement);
        }
    }

    private List<String> resolveAlertRecipients(Amenagement amenagement, String memberOfSearch) {
        Set<String> recipients = new LinkedHashSet<>();
        if (StringUtils.hasText(applicationProperties.getTestEmail())) {
            recipients.add(applicationProperties.getTestEmail());
            return new ArrayList<>(recipients);
        }
        if (!StringUtils.hasText(ldapProperties.getAffectationPrincipaleRefIdPrefixFromApo())
                || !StringUtils.hasText(memberOfSearch)) {
            return new ArrayList<>(recipients);
        }
        DossierAmenagement dossierAmenagement = getDossierAmenagementOfCurrentYear(amenagement);
        if (dossierAmenagement == null || dossierAmenagement.getDossier() == null) {
            return new ArrayList<>(recipients);
        }
        String codComposante = dossierAmenagement.getDossier().getCodComposante();
        List<OrganizationalUnitLdap> organizationalUnitLdaps = organizationalUnitLdapRepository.findBySupannRefId(
                ldapProperties.getAffectationPrincipaleRefIdPrefixFromApo() + codComposante
        );
        List<String> affectations = organizationalUnitLdaps.stream()
                .map(OrganizationalUnitLdap::getSupannCodeEntite)
                .distinct()
                .toList();
        List<String> uids = userOthersAffectationsRepository.findByCodComposante(codComposante).stream()
                .map(UserOthersAffectations::getUid)
                .toList();
        for (PersonLdap personLdap : personLdapRepository.findByMemberOf(memberOfSearch)) {
            if ((uids.contains(personLdap.getUid())
                    || affectations.contains(personLdap.getSupannEntiteAffectationPrincipale()))
                    && StringUtils.hasText(personLdap.getMail())) {
                recipients.add(personLdap.getMail());
            }
        }
        return new ArrayList<>(recipients);
    }

    public void addLibelle(String newLibelle, Integer previousIndex) {
        int newOrderIndex = previousIndex + 1;
        LibelleAmenagement libelleAmenagement = new LibelleAmenagement();
        libelleAmenagement.setTitle(newLibelle);
        libelleAmenagement.setOrderIndex(newOrderIndex);
        List<LibelleAmenagement> allRecords = libelleAmenagementRepository.findAll();
        for (LibelleAmenagement existingRecord : allRecords) {
            if (existingRecord.getOrderIndex() >= newOrderIndex) {
                existingRecord.setOrderIndex(existingRecord.getOrderIndex() + 1);
            }
        }
        libelleAmenagementRepository.save(libelleAmenagement);
    }

    @Transactional
    public void viewedByUid(Long amenagementId, String uid) {
        Amenagement amenagement = getById(amenagementId);
        amenagement.getViewByUid().add(uid);
    }

    @Transactional
    public void notViewedByUid(Long amenagementId, String uid) {
        Amenagement amenagement = getById(amenagementId);
        amenagement.getViewByUid().remove(uid);
    }

    @Transactional
    public boolean isPortable(Long amenagementId, Long currentDossier) {
        Amenagement amenagement = getById(amenagementId);
        Dossier dossier = dossierService.getById(currentDossier);
        return dossierAmenagementRepository.findDossierAmenagementByDossierAndAmenagement(dossier, amenagement).isEmpty();
    }

    @Transactional
    public boolean isDossierContainsAmenagement(Dossier dossier) {
        return !dossierAmenagementRepository.findDossierAmenagementByDossier(dossier).isEmpty();
    }

    @Transactional
    public boolean isDossierContainsValidAmenagementCursus(Dossier dossier) {
        List<DossierAmenagement> dossierAmenagements = dossierAmenagementRepository.findDossierAmenagementByDossier(dossier);
        return !dossierAmenagements.isEmpty() && dossierAmenagements.stream().anyMatch(da -> da.getAmenagement().getTypeAmenagement().equals(TypeAmenagement.CURSUS) && da.getAmenagement().getStatusAmenagement().equals(StatusAmenagement.VISE_ADMINISTRATION));
    }

    @Transactional
    public List<Amenagement> getAmenagementToResend() {
        return amenagementRepository.findAmenagementToResend(LocalDateTime.parse("31/12/2023 23:59:59", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
    }

    @Transactional
    public void resendAmenagements() throws Exception {
        List<Amenagement> amenagements = getAmenagementToResend();
        for(Amenagement amenagement : amenagements) {
            sendAmenagementToIndividu(amenagement.getId(), false);
        }
    }

    @Transactional
    public List<Amenagement> getAmenagementsToSync() {
        return amenagementRepository.findDossierAmenagementToSync();
    }

    public List<CodMeae> getCodMeaeList(String amenagementText) {
        List<DataMapping> dataMappings = dataMappingService.getValues("Amenagement", "amenagementText", DataType.agape, DataType.enquete);
        List<CodMeae> codMeaes = new ArrayList<>();
        String[] lignes = amenagementText.split("\\r?\\n");
        for (String line : lignes) {
            String normalizedLine = normalize(line);
            for (DataMapping dataMapping : dataMappings) {
                String normalizedKey = normalize(dataMapping.getSourceValue());
                String pattern = "\\b" + Pattern.quote(normalizedKey) + "\\b";
                if (normalizedLine.matches(".*" + pattern + ".*")) {
                    codMeaes.add(CodMeae.valueOf(dataMapping.getDestinationValue()));
                }
            }
        }
        return codMeaes;
    }

    private static String normalize(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").trim().toLowerCase();
    }

}
