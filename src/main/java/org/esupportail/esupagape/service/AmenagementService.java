package org.esupportail.esupagape.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
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
import org.esupportail.esupagape.dtos.pdfs.CertificatPdf;
import org.esupportail.esupagape.entity.*;
import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeJpaException;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    public AmenagementService(ApplicationProperties applicationProperties, LdapProperties ldapProperties, AmenagementRepository amenagementRepository, DossierAmenagementRepository dossierAmenagementRepository, DossierService dossierService, ObjectMapper objectMapper, MessageSource messageSource, UtilsService utilsService, EsupSignatureService esupSignatureService, MailService mailService, LogService logService, DocumentService documentService, LibelleAmenagementRepository libelleAmenagementRepository, UserOthersAffectationsRepository userOthersAffectationsRepository, PersonLdapRepository personLdapRepository, OrganizationalUnitLdapRepository organizationalUnitLdapRepository, DossierRepository dossierRepository) {
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
    }

    public Amenagement getById(Long id) {
        return amenagementRepository.findById(id).orElseThrow();
    }

    @Transactional
    public Page<Amenagement> findByDossier(Long dossierId) {
        Dossier dossier = dossierService.getById(dossierId);
        List<Amenagement> amenagements = dossierAmenagementRepository.findDossierAmenagementByDossier(dossier).stream().map(DossierAmenagement::getAmenagement).collect(Collectors.toList());
        return new PageImpl<>(amenagements, Pageable.unpaged(), amenagements.size());
    }

    public Boolean isAmenagementTempsMajore(Long dossierId) {
        Dossier dossier = dossierService.getById(dossierId);
        List<DossierAmenagement> dossierAmenagements = dossierAmenagementRepository.findDossierAmenagementByDossier(dossier);
        if(!dossierAmenagements.isEmpty()) {
            return dossierAmenagements.stream().anyMatch(dossierAmenagement -> (dossierAmenagement.getAmenagement().getTempsMajore() != null && !dossierAmenagement.getAmenagement().getTempsMajore().equals(TempsMajore.AUCUN)) || StringUtils.hasText(dossierAmenagement.getAmenagement().getAutresTempsMajores()));
        }
        List<Amenagement> amenagements =  amenagementRepository.findByDossierIdAndStatusAmenagement(dossierId, StatusAmenagement.VISE_ADMINISTRATION);
        if(!amenagements.isEmpty() && (amenagements.get(0).getTypeAmenagement().equals(TypeAmenagement.CURSUS) || amenagements.get(0).getEndDate().isAfter(LocalDateTime.now()))) {
            return amenagements.stream().anyMatch(amenagement -> amenagement.getTempsMajore() != null || !amenagement.getTempsMajore().equals(TempsMajore.AUCUN) || StringUtils.hasText(amenagement.getAutresTempsMajores()));
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
    public void create(Amenagement amenagement, Long idDossier, PersonLdap personLdap) throws AgapeException {
        Dossier dossier = dossierService.getById(idDossier);
        if (dossier.getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        if (amenagement.getTypeAmenagement().equals(TypeAmenagement.DATE) && amenagement.getEndDate() == null) {
            throw new AgapeException("Impossible de créer l'aménagement sans date de fin");
        }
        if (dossier.getStatusDossier().equals(StatusDossier.IMPORTE) || dossier.getStatusDossier().equals(StatusDossier.AJOUT_MANUEL)) {
            dossierService.changeStatutDossier(idDossier, StatusDossier.RECU_PAR_LA_MEDECINE_PREVENTIVE, personLdap.getEduPersonPrincipalName());
        }
        amenagement.setNomMedecin(personLdap.getDisplayName());
        amenagement.setMailMedecin(personLdap.getMail());
        Set<Classification> selectedClassifications = amenagement.getClassification();
        updateDossierClassification(dossier, selectedClassifications, amenagement.getAutorisation());
        if (!amenagement.getTypeEpreuves().contains(TypeEpreuve.AUCUN)) {
            amenagement.setTypeEpreuves(amenagement.getTypeEpreuves());
        } else {
            amenagement.getTypeEpreuves().clear();
            amenagement.getTypeEpreuves().add(TypeEpreuve.AUCUN);
        }
        amenagementRepository.save(amenagement);
        dossierService.createDossierAmenagement(amenagement, dossier);
    }

    @Transactional
    public void update(Long amenagementId, Amenagement amenagement) throws AgapeJpaException {
        Amenagement amenagementToUpdate = getById(amenagementId);
        DossierAmenagement dossierAmenagement = getDossierAmenagementOfCurrentYear(amenagementToUpdate);
        if(dossierAmenagement.getDossier() == null) {
            throw new AgapeYearException();
        }
        if (amenagementToUpdate.getStatusAmenagement().equals(StatusAmenagement.BROUILLON)) {
            amenagementToUpdate.setTypeAmenagement(amenagement.getTypeAmenagement());
            amenagementToUpdate.setAmenagementText(amenagement.getAmenagementText());
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

            Set<Classification> selectedClassifications = amenagement.getClassification();
            if(amenagement.getAutorisation().equals(Autorisation.OUI)) {
                amenagementToUpdate.getClassification().addAll(selectedClassifications);
            } else {
                amenagementToUpdate.getClassification().clear();
            }
            updateDossierClassification(dossierAmenagement.getDossier(), selectedClassifications, amenagement.getAutorisation());
            amenagementRepository.save(amenagementToUpdate);
        }
    }

    public DossierAmenagement getDossierAmenagementOfCurrentYear(Amenagement amenagement) {
        List<DossierAmenagement> dossierAmenagements = dossierAmenagementRepository.findDossierAmenagementByAmenagement(amenagement);
        return dossierAmenagements.stream().filter(da -> da.getLastYear() == utilsService.getCurrentYear()).findFirst().orElse(null);
    }

    private void updateDossierClassification(Dossier dossier, Set<Classification> selectedClassifications, Autorisation autorisation) {
        if (dossier.getStatusDossier().equals(StatusDossier.RECU_PAR_LA_MEDECINE_PREVENTIVE)) {
            if(autorisation.equals(Autorisation.OUI)) {
                if (selectedClassifications != null && !selectedClassifications.isEmpty()) {
                    dossier.getClassifications().addAll(selectedClassifications);
                }
            } else if (autorisation.equals(Autorisation.NON)) {
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
        return amenagementRepository.countToValidate(utilsService.getCurrentYear());
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
            amenagement.setValideMedecinDate(LocalDateTime.now());
            dossierAmenagement.setStatusDossierAmenagement(StatusDossierAmenagement.EN_ATTENTE);
            amenagement.setMailMedecin(personLdap.getMail());
            if(!StringUtils.hasText(applicationProperties.getEsupSignatureAvisWorkflowId()) && StringUtils.hasText(applicationProperties.getEsupSignatureCertificatsWorkflowId())) {
                sendToCertificatWorkflow(id);
                amenagement.setStatusAmenagement(StatusAmenagement.VALIDE_MEDECIN);
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
                amenagement.setStatusAmenagement(StatusAmenagement.VALIDE_MEDECIN);
                logger.info("aménagement : " + amenagement.getId() + " validé par " + personLdap.getMail());
            }
            dossierService.syncStatusDossierAmenagement(dossierAmenagement.getDossier().getId());
        } else {
            throw new AgapeException("Impossible de valider un aménagement qui n'est pas au statut brouillon");
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
            esupSignatureService.send(amenagement, generateDocument(amenagement, modelBytes, TypeWorkflow.CERTIFICAT, false), TypeWorkflow.CERTIFICAT);
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
        if(amenagement.getStatusAmenagement().equals(StatusAmenagement.VALIDE_MEDECIN)) {
            if(!StringUtils.hasText(applicationProperties.getEsupSignatureUrl())) {
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
                dossierService.syncStatusDossierAmenagement(dossierAmenagement.getDossier().getId());
                amenagementRepository.save(amenagement);
                sendAlert(amenagement);
                sendAmenagementToIndividu(amenagement.getId(), false);
            }
        } else {
            throw new AgapeException("Impossible de valider un aménagement qui n'est pas au statut Validé par le médecin");
        }
    }

    @Transactional
    public void refusAdministration(Long id, PersonLdap personLdap, String motif) throws AgapeException {
        Amenagement amenagement = getById(id);
        DossierAmenagement dossierAmenagement = getDossierAmenagementOfCurrentYear(amenagement);
        if(dossierAmenagement.getDossier() == null) {
            throw new AgapeYearException();
        }
        if(amenagement.getStatusAmenagement().equals(StatusAmenagement.VALIDE_MEDECIN)) {
            amenagement.setAdministrationDate(LocalDateTime.now());
            amenagement.setStatusAmenagement(StatusAmenagement.REFUSE_ADMINISTRATION);
            amenagement.setNomValideur(personLdap.getDisplayName());
            amenagement.setUidValideur(personLdap.getUid());
            amenagement.setMotifRefus(motif);
            dossierAmenagement.setStatusDossierAmenagement(StatusDossierAmenagement.REFUSE);
            dossierService.syncStatusDossierAmenagement(dossierAmenagement.getDossier().getId());
            logger.info("amenagement " + id + " refused");

        } else {
            throw new AgapeException("Impossible de valider un aménagement qui n'est pas au statut Validé par le médecin");
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
            byte[] modelBytes;
            if(StringUtils.hasText(applicationProperties.getModelsPath())) {
                modelBytes = Files.readAllBytes(new File(applicationProperties.getModelsPath() + "/certificat.pdf").toPath());
            } else {
                modelBytes = new ClassPathResource("models/certificat.pdf").getInputStream().readAllBytes();
            }
            certificat = generateDocument(amenagement, modelBytes, TypeWorkflow.CERTIFICAT, true);
        }
        httpServletResponse.getOutputStream().write(certificat);
    }

    @Transactional
    public void getAvis(Long id, HttpServletResponse httpServletResponse) throws IOException, AgapeException {
        Amenagement amenagement = getById(id);
        if(!(amenagement.getStatusAmenagement().equals(StatusAmenagement.BROUILLON) || amenagement.getStatusAmenagement().equals(StatusAmenagement.VALIDE_MEDECIN) || amenagement.getStatusAmenagement().equals(StatusAmenagement.VISE_ADMINISTRATION) || amenagement.getStatusAmenagement().equals(StatusAmenagement.REFUSE_ADMINISTRATION))) {
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
        for(String line : amenagement.getAmenagementText().split("\n")) {
            if (!amenagement.getAmenagementText().isEmpty()) {
                amenagementsWithNumbers.append(i).append(" - ").append(line).append("\n");
                i++;
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

    private byte[] generatePdf(Amenagement amenagement, Map<String, String> datas, byte[] model, boolean withSign) throws IOException {
        byte[] savedPdf;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PDDocument modelDocument = PDDocument.load(model);
        PDAcroForm pdAcroForm = modelDocument.getDocumentCatalog().getAcroForm();
        byte[] ttfBytes = new ClassPathResource("/static/fonts/LiberationSans-Regular.ttf").getInputStream().readAllBytes();
        PDFont pdFont = PDTrueTypeFont.load(modelDocument, new ByteArrayInputStream(ttfBytes), WinAnsiEncoding.INSTANCE);
        PDResources resources = pdAcroForm.getDefaultResources();
        resources.put(COSName.getPDFName("LiberationSans"), pdFont);
        pdAcroForm.setDefaultResources(resources);
        List<String> fieldsNames = pdAcroForm.getFields().stream().map(PDField::getFullyQualifiedName).toList();
        modelDocument.save(out);
        modelDocument.close();
        savedPdf = out.toByteArray();
        for(String fieldName : fieldsNames) {
            PDDocument toFillDocument = PDDocument.load(savedPdf);
            PDField pdField = toFillDocument.getDocumentCatalog().getAcroForm().getField(fieldName);
            if(pdField != null) {
                if(pdField instanceof PDSignatureField) {
                    if(withSign) {
                        addVisualSignature(amenagement, toFillDocument, pdField.getWidgets().get(0).getRectangle(), fieldName);
                    }
                } else {
                    pdField.getCOSObject().setString(COSName.DA, "/LiberationSans 11 Tf 0 g");
                    if(datas.containsKey(fieldName)) {
                        pdField.setValue(datas.get(fieldName));
                    }
                }
                out = new ByteArrayOutputStream();
                toFillDocument.save(out);
                toFillDocument.close();
                savedPdf = out.toByteArray();
            }
        }
        PDDocument finishedDocument = PDDocument.load(savedPdf);
        List<PDField> fields = finishedDocument.getDocumentCatalog().getAcroForm().getFields();
        List<PDField> dates = fields.stream().filter(f -> f.getFullyQualifiedName().equals("administrationDate")).toList();
        List<PDField> cleannedFields = fields.stream().filter(f -> !(f instanceof PDSignatureField) && !f.getFullyQualifiedName().equals("administrationDate")).toList();
        for(PDField field : cleannedFields) {
            for(PDAnnotationWidget pdAnnotationWidget : field.getWidgets()) {
                if(pdAnnotationWidget.getPage() == null) {
                    pdAnnotationWidget.setPage(finishedDocument.getPage(0));
                }
            }
            finishedDocument.getDocumentCatalog().getAcroForm().flatten(Collections.singletonList(field), false);
        }

        if(!dates.isEmpty()) {
            finishedDocument.getDocumentCatalog().getAcroForm().getFields().add(dates.get(0));
        }
        out = new ByteArrayOutputStream();
        finishedDocument.save(out);
        finishedDocument.close();
        return out.toByteArray();
    }

    private void addVisualSignature(Amenagement amenagement, PDDocument doc, PDRectangle signRectangle, String fieldName) throws IOException
    {
        PDPageContentStream cs = new PDPageContentStream(doc, doc.getPage(0), PDPageContentStream.AppendMode.APPEND, false);
        File tmpDir = Files.createTempDirectory("esupagape").toFile();
        File signImage;
        if(StringUtils.hasText(applicationProperties.getSignaturesPath())) {
            signImage = new File(applicationProperties.getSignaturesPath() + "/signature-" + amenagement.getUidValideur() + ".jpg");
        } else {
            signImage = new File(tmpDir + "/signImage.jpg");
            ClassPathResource signImgResource = new ClassPathResource("/static/images/signature-" + amenagement.getUidValideur() + ".jpg");
            if(!signImgResource.exists()) {
                signImgResource = new ClassPathResource("/static/images/" + fieldName + ".jpg");
            }
            FileUtils.copyInputStreamToFile(signImgResource.getInputStream(), signImage);
        }
        PDImageXObject img = PDImageXObject.createFromFileByExtension(signImage, doc);
        float ratio = img.getHeight() / signRectangle.getHeight();
        cs.drawImage(img, signRectangle.getLowerLeftX(), signRectangle.getUpperRightY() - (img.getHeight() / ratio), img.getWidth() / ratio, img.getHeight() / ratio);
        cs.close();
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
            currentDossier = dossierService.create(personLdap.getEduPersonPrincipalName(), dossierAmenagementRepository.findDossierAmenagementByAmenagement(amenagement).get(0).getDossier().getIndividu(), TypeIndividu.ETUDIANT, StatusDossier.RECONDUIT);
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
            currentDossier = dossierService.create(personLdap.getEduPersonPrincipalName(), dossierAmenagementRepository.findDossierAmenagementByAmenagement(amenagement).get(0).getDossier().getIndividu(), TypeIndividu.ETUDIANT, StatusDossier.NON_RECONDUIT);
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
        SignatureStatus signatureStatus = esupSignatureService.getStatus(dossierAmenagement, typeWorkflow);
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
            if (amenagement.getStatusAmenagement().equals(StatusAmenagement.VALIDE_MEDECIN)) {
                if(amenagement.getCertificatSignatureStatus() == null) {
                    sendToCertificatWorkflow(amenagementId);
                }
                checkEsupSignatureStatus(amenagementId, TypeWorkflow.CERTIFICAT);
            } else if (amenagement.getStatusAmenagement().equals(StatusAmenagement.ENVOYE)) {
                SignatureStatus signatureStatus = checkEsupSignatureStatus(amenagementId, TypeWorkflow.AVIS);
                if(signatureStatus.equals(SignatureStatus.COMPLETED)) {
                    sendToCertificatWorkflow(amenagementId);
                }
            }
        }
    }

    @Transactional
    public void syncEsupSignatureAmenagements() throws AgapeException {
        List<Amenagement> amenagementsToSync = dossierAmenagementRepository.findDossierAmenagementByLastYear(utilsService.getCurrentYear()).stream().filter(da -> da.getAmenagement().getStatusAmenagement().equals(StatusAmenagement.ENVOYE) || da.getAmenagement().getStatusAmenagement().equals(StatusAmenagement.VALIDE_MEDECIN)).map(DossierAmenagement::getAmenagement).toList();
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
                if (!StatusDossierAmenagement.EXPIRE.equals(dossierAmenagement.getStatusDossierAmenagement()) && amenagement.getTypeAmenagement().equals(TypeAmenagement.DATE) && (amenagement.getEndDate().isBefore(now) || amenagement.getEndDate().equals(now)) && amenagement.getStatusAmenagement().equals(StatusAmenagement.VISE_ADMINISTRATION)) {
                    logger.info("amenagement " + amenagement.getId() + " EXPIRE");
                    dossierAmenagement.setStatusDossierAmenagement(StatusDossierAmenagement.EXPIRE);
                    logService.create("SYSTEM", dossierAmenagement.getId(), dossierAmenagement.getStatusDossierAmenagement().name(), StatusDossierAmenagement.EXPIRE.name());
                } else {
                    dossier.setStatusDossierAmenagement(StatusDossierAmenagement.VALIDE);
                }
            } else {
                Optional<DossierAmenagement> lastDossierAmenagement = amenagement.getDossierAmenagements().stream().max(Comparator.comparingInt(DossierAmenagement::getLastYear));
                if (lastDossierAmenagement.isPresent() && !lastDossierAmenagement.get().getStatusDossierAmenagement().equals(StatusDossierAmenagement.REFUSE) && amenagement.getTypeAmenagement().equals(TypeAmenagement.DATE) && amenagement.getEndDate().isAfter(now) && amenagement.getStatusAmenagement().equals(StatusAmenagement.VISE_ADMINISTRATION)) {
                    if (dossier == null) {
                        Dossier lastDossier = dossierAmenagementRepository.findDossierAmenagementByAmenagement(amenagement).get(0).getDossier();
                        dossier = dossierService.create("system", lastDossier.getIndividu(), TypeIndividu.ETUDIANT, StatusDossier.RECONDUIT);
                        dossierAmenagement = dossierService.createDossierAmenagement(amenagement, dossier);
                        dossierAmenagement.setStatusDossierAmenagement(StatusDossierAmenagement.VALIDE);
                        logService.create("SYSTEM", dossierAmenagement.getId(), "reconduction pour date de fin ultérieure", StatusDossierAmenagement.VALIDE.name());
                        logger.info("amenagement " + amenagement.getId() + " reconduit");
                    } else if (dossier.getDossierAmenagements().stream().noneMatch(da -> da.getAmenagement().equals(amenagement))) {
                        dossierAmenagement = dossierService.createDossierAmenagement(amenagement, dossier);
                        logService.create("SYSTEM", dossierAmenagement.getId(), "reconduction pour date de fin ultérieure", StatusDossierAmenagement.VALIDE.name());
                        logger.info("amenagement " + amenagement.getId() + " reconduit");
                    }
                    dossier.setStatusDossierAmenagement(StatusDossierAmenagement.VALIDE);
                }
            }
            if (amenagement.getIndividuSendDate() == null) {
                amenagementRepository.save(amenagement);
                sendAlert(amenagement);
                sendAmenagementToIndividu(amenagement.getId(), false);
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
        List<String> to = new ArrayList<>();
        if(StringUtils.hasText(applicationProperties.getTestEmail())) {
            to.add(applicationProperties.getTestEmail());
        } else {
            if(ldapProperties.getAffectationPrincipaleRefIdPrefixFromApo() != null) {
                DossierAmenagement dossierAmenagement = getDossierAmenagementOfCurrentYear(amenagement);
                List<OrganizationalUnitLdap> organizationalUnitLdaps = organizationalUnitLdapRepository.findBySupannRefId(ldapProperties.getAffectationPrincipaleRefIdPrefixFromApo() + dossierAmenagement.getDossier().getCodComposante());
                List<String> affectations = organizationalUnitLdaps.stream().map(OrganizationalUnitLdap::getSupannCodeEntite).distinct().toList();
                List<PersonLdap> personLdaps = personLdapRepository.findByMemberOf(ldapProperties.getScolariteMemberOfSearch());
                List<UserOthersAffectations> userOthersAffectations = userOthersAffectationsRepository.findByCodComposante(dossierAmenagement.getDossier().getCodComposante());
                List<String> uids = userOthersAffectations.stream().map(UserOthersAffectations::getUid).toList();
                for (PersonLdap personLdap : personLdaps) {
                    if (uids.contains(personLdap.getUid()) || affectations.contains(personLdap.getSupannEntiteAffectationPrincipale())) {
                        to.add(personLdap.getMail());
                    }
                }
            }
        }
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

    public void syncAllAmenagments() {
        List<Amenagement> amenagementsToSync = getAmenagementsToSync();
        for(Amenagement amenagement : amenagementsToSync) {
            syncAmenagement(amenagement.getId());
        }
    }


}
