package org.esupportail.esupagape.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.dtos.pdfs.CertificatPdf;
import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.Document;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.exception.AgapeYearException;
import org.esupportail.esupagape.repository.AmenagementRepository;
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

import javax.servlet.http.HttpServletResponse;
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
    private final AmenagementRepository amenagementRepository;
    private final DossierService dossierService;
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;
    private final UtilsService utilsService;
    private final EsupSignatureService esupSignatureService;
    private final MailService mailService;
    private final DocumentService documentService;

    public AmenagementService(ApplicationProperties applicationProperties, AmenagementRepository amenagementRepository, DossierService dossierService, ObjectMapper objectMapper, MessageSource messageSource, UtilsService utilsService, EsupSignatureService esupSignatureService, MailService mailService, DocumentService documentService) {
        this.applicationProperties = applicationProperties;
        this.amenagementRepository = amenagementRepository;
        this.dossierService = dossierService;
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
        this.utilsService = utilsService;
        this.esupSignatureService = esupSignatureService;
        this.mailService = mailService;
        this.documentService = documentService;
    }

    public Amenagement getById(Long id) {
        return amenagementRepository.findById(id).orElseThrow();
    }

    public Page<Amenagement> findByDossier(Long dossierId) {
        Dossier dossier = dossierService.getById(dossierId);
        if(dossier.getAmenagementPorte() != null) {
            return new PageImpl<>(List.of(dossier.getAmenagementPorte()), Pageable.unpaged(), 1);
        }
        return amenagementRepository.findByDossierId(dossierId, Pageable.unpaged());
    }

    public Amenagement isAmenagementValid(Long dossierId) {
        Dossier dossier = dossierService.getById(dossierId);
        if(dossier.getAmenagementPorte() != null) {
            return dossier.getAmenagementPorte();
        }
        List<Amenagement> amenagements =  amenagementRepository.findByDossierIdAndStatusAmenagement(dossierId, StatusAmenagement.VISE_ADMINISTRATION);
        if(amenagements.size() > 0 && (amenagements.get(0).getTypeAmenagement().equals(TypeAmenagement.CURSUS) || amenagements.get(0).getEndDate().isAfter(LocalDateTime.now()))) {
            return amenagements.get(0);
        }
        return null;
    }

   /* @Transactional
    public void create(Amenagement amenagement, Long idDossier, PersonLdap personLdap) throws AgapeException {
        Dossier dossier = dossierService.getById(idDossier);
        if(dossier.getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        if(amenagement.getTypeAmenagement().equals(TypeAmenagement.DATE) && amenagement.getEndDate() == null) {
            throw new AgapeException("Impossible de créer l'aménagement sans date de fin");
        }
        if (dossier.getStatusDossier().equals(StatusDossier.IMPORTE) || dossier.getStatusDossier().equals(StatusDossier.AJOUT_MANUEL)) {
            dossier.setStatusDossier(StatusDossier.RECU_PAR_LA_MEDECINE_PREVENTIVE);
        }
        amenagement.setDossier(dossier);
        amenagement.setNomMedecin(personLdap.getDisplayName());
        amenagement.setMailMedecin(personLdap.getMail());
        updateClassification(amenagement);
        amenagementRepository.save(amenagement);
    }*/

    @Transactional
    public void deleteAmenagement(Long amenagementId) {
        Amenagement amenagement = getById(amenagementId);
        if(amenagement.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        amenagementRepository.deleteById(amenagementId);
    }

    @Transactional
    public void softDeleteAmenagement(Long amenagementId) throws AgapeException {
        Amenagement amenagement = getById(amenagementId);
        if(amenagement.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        if(amenagement.getStatusAmenagement().equals(StatusAmenagement.BROUILLON)) {
            amenagement.setStatusAmenagement(StatusAmenagement.SUPPRIME);
        } else {
            throw new AgapeException("Impossible de supprimer un aménagement qui n'est pas au statut brouillon");
        }
    }

/*    @Transactional
    public void update(Long amenagementId, Amenagement amenagement) throws AgapeJpaException {
        Amenagement amenagementToUpdate = getById(amenagementId);
        if(amenagementToUpdate.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        if(amenagementToUpdate.getStatusAmenagement().equals(StatusAmenagement.BROUILLON)){
        amenagementToUpdate.setTypeAmenagement(amenagement.getTypeAmenagement());
        amenagementToUpdate.setAmenagementText(amenagement.getAmenagementText());
        amenagementToUpdate.setAutorisation(amenagement.getAutorisation());
        amenagementToUpdate.setClassification(amenagement.getClassification());
        amenagementToUpdate.setTypeEpreuves(amenagement.getTypeEpreuves());
        amenagementToUpdate.setAutresTypeEpreuve(amenagement.getAutresTypeEpreuve());
        amenagementToUpdate.setEndDate(amenagement.getEndDate());
        amenagementToUpdate.setTempsMajore(amenagement.getTempsMajore());
        amenagementToUpdate.setAutresTempsMajores(amenagement.getAutresTempsMajores());
        updateClassification(amenagementToUpdate);}
    }

    private static void updateClassification(Amenagement amenagement) {
        if (amenagement.getDossier().getStatusDossier().equals(StatusDossier.RECU_PAR_LA_MEDECINE_PREVENTIVE)) {
            if (amenagement.getAutorisation().equals(Autorisation.OUI)) {
                amenagement.getDossier().setClassifications(amenagement.getClassification());
            }
            if (amenagement.getAutorisation().equals(Autorisation.NON)) {
                amenagement.getDossier().getClassifications().clear();
                amenagement.getDossier().getClassifications().add(Classification.REFUS);
            }
            if (amenagement.getAutorisation().equals(Autorisation.NC)) {
                amenagement.getDossier().getClassifications().clear();
                amenagement.getDossier().getClassifications().add(Classification.NON_COMMUNIQUE);
            }
        }
    }*/
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
        dossier.setStatusDossier(StatusDossier.RECU_PAR_LA_MEDECINE_PREVENTIVE);
    }
    amenagement.setDossier(dossier);
    amenagement.setNomMedecin(personLdap.getDisplayName());
    amenagement.setMailMedecin(personLdap.getMail());

    Set<Classification> selectedClassifications = amenagement.getClassification();
    updateClassification(dossier, selectedClassifications);

    amenagementRepository.save(amenagement);
}

    @Transactional
    public void update(Long amenagementId, Amenagement amenagement) throws AgapeJpaException {
        Amenagement amenagementToUpdate = getById(amenagementId);
        if (amenagementToUpdate.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        if (amenagementToUpdate.getStatusAmenagement().equals(StatusAmenagement.BROUILLON)) {
            amenagementToUpdate.setTypeAmenagement(amenagement.getTypeAmenagement());
            amenagementToUpdate.setAmenagementText(amenagement.getAmenagementText());
            amenagementToUpdate.setAutorisation(amenagement.getAutorisation());
            amenagementToUpdate.setTypeEpreuves(amenagement.getTypeEpreuves());
            amenagementToUpdate.setAutresTypeEpreuve(amenagement.getAutresTypeEpreuve());
            amenagementToUpdate.setEndDate(amenagement.getEndDate());
            amenagementToUpdate.setTempsMajore(amenagement.getTempsMajore());
            amenagementToUpdate.setAutresTempsMajores(amenagement.getAutresTempsMajores());

            Set<Classification> selectedClassifications = amenagement.getClassification();
            amenagementToUpdate.setClassification(selectedClassifications);

            updateClassification(amenagementToUpdate.getDossier(), selectedClassifications);

            amenagementRepository.save(amenagementToUpdate);
        }
    }

    private void updateClassification(Dossier dossier, Set<Classification> selectedClassifications) {
        if (dossier.getStatusDossier().equals(StatusDossier.RECU_PAR_LA_MEDECINE_PREVENTIVE)) {
            if (selectedClassifications != null && !selectedClassifications.isEmpty()) {
                dossier.setClassifications(selectedClassifications);
            } else {
                dossier.setClassifications(Collections.emptySet());
            }
        }
    }



    public Page<Amenagement> findAllPaged(Pageable pageable) {
        return amenagementRepository.findAll(pageable);
    }

    public Page<Amenagement> getFullTextSearch(StatusAmenagement statusAmenagement, String codComposante, Integer yearFilter, Pageable pageable) {
        return amenagementRepository.findByFullTextSearch(statusAmenagement, codComposante, yearFilter, pageable);
    }

    public Page<Amenagement> getFullTextSearchPorte(String codComposante, Integer yearFilter, Pageable pageable) {
        return amenagementRepository.findByFullTextSearchPortable(codComposante, yearFilter, pageable);
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
        if(amenagement.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        if(amenagement.getStatusAmenagement().equals(StatusAmenagement.BROUILLON)) {
            amenagement.setValideMedecinDate(LocalDateTime.now());
            amenagement.getDossier().setStatusDossierAmenagement(StatusDossierAmenagement.EN_ATTENTE);
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
                            "Avis-" + amenagement.getDossier().getIndividu().getNumEtu() + "-" + amenagement.getId() + ".pdf",
                            "application/pdf", amenagement.getId(), Amenagement.class.getSimpleName(),
                            amenagement.getDossier());
                    amenagement.setAvis(avis);
                } catch (IOException e) {
                    throw new AgapeException("Impossible de générer l'avis");
                }
                amenagement.setStatusAmenagement(StatusAmenagement.VALIDE_MEDECIN);
            }
        } else {
            throw new AgapeException("Impossible de valider un aménagement qui n'est pas au statut brouillon");
        }
    }

    @Transactional
    public void sendToCertificatWorkflow(Long id) throws AgapeException {
        Amenagement amenagement = getById(id);
        try {
            byte[] modelBytes = new ClassPathResource("models/certificat.pdf").getInputStream().readAllBytes();
            esupSignatureService.send(id, generateDocument(amenagement, modelBytes, TypeWorkflow.CERTIFICAT, false), TypeWorkflow.CERTIFICAT);
        } catch (IOException e) {
            throw new AgapeException("Envoi vers esup-signature impossible", e);
        }
    }

    @Transactional
    public void sendToAvisWorkflow(Long id) throws AgapeException {
        Amenagement amenagement = getById(id);
        try {
            byte[] modelBytes = new ClassPathResource("models/avis.pdf").getInputStream().readAllBytes();
            esupSignatureService.send(id, generateDocument(amenagement, modelBytes, TypeWorkflow.AVIS, false), TypeWorkflow.AVIS);
        } catch (IOException e) {
            throw new AgapeException("Envoi vers esup-signature impossible", e);
        }
    }

    @Transactional
    public void validationAdministration(Long amenagementId, PersonLdap personLdap) throws AgapeException, IOException {
        Amenagement amenagement = getById(amenagementId);
        if(amenagement.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        if(amenagement.getStatusAmenagement().equals(StatusAmenagement.VALIDE_MEDECIN)) {
            if(!StringUtils.hasText(applicationProperties.getEsupSignatureUrl())) {
                amenagement.setAdministrationDate(LocalDateTime.now());
                amenagement.setStatusAmenagement(StatusAmenagement.VISE_ADMINISTRATION);
                amenagement.setNomValideur(personLdap.getDisplayName());
                amenagement.setMailValideur(personLdap.getMail());
                amenagement.getDossier().setStatusDossierAmenagement(StatusDossierAmenagement.VALIDE);
                byte[] modelBytes = new ClassPathResource("models/certificat.pdf").getInputStream().readAllBytes();
                Document certificat = documentService.createDocument(
                        new ByteArrayInputStream(generateDocument(amenagement, modelBytes, TypeWorkflow.CERTIFICAT, true)),
                        "Certificat-" + amenagement.getDossier().getIndividu().getNumEtu() + "-" + amenagement.getId() + ".pdf",
                        "application/pdf", amenagement.getId(), Amenagement.class.getSimpleName(),
                        amenagement.getDossier());
                amenagement.setCertificat(certificat);
                sendAmenagementToIndividu(amenagementId, false);
            }
        } else {
            throw new AgapeException("Impossible de valider un aménagement qui n'est pas au statut Validé par le médecin");
        }
    }

    @Transactional
    public void refusAdministration(Long id, PersonLdap personLdap, String motif) throws AgapeException {
        Amenagement amenagement = getById(id);
        if(amenagement.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        if(amenagement.getStatusAmenagement().equals(StatusAmenagement.VALIDE_MEDECIN)) {
            amenagement.setAdministrationDate(LocalDateTime.now());
            amenagement.setStatusAmenagement(StatusAmenagement.REFUSE_ADMINISTRATION);
            amenagement.setNomValideur(personLdap.getDisplayName());
            amenagement.setMailValideur(personLdap.getMail());
            amenagement.setMotifRefus(motif);
            amenagement.getDossier().setStatusDossierAmenagement(StatusDossierAmenagement.NON);
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
        if(!(amenagement.getStatusAmenagement().equals(StatusAmenagement.VALIDE_MEDECIN) || amenagement.getStatusAmenagement().equals(StatusAmenagement.VISE_ADMINISTRATION) || amenagement.getStatusAmenagement().equals(StatusAmenagement.REFUSE_ADMINISTRATION))) {
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
        certificatPdf.setName(amenagement.getDossier().getIndividu().getName());
        certificatPdf.setFirstname(amenagement.getDossier().getIndividu().getFirstName());
        certificatPdf.setDateOfBirth(amenagement.getDossier().getIndividu().getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        certificatPdf.setLibelleFormation(amenagement.getDossier().getLibelleFormation());
        certificatPdf.setSite(amenagement.getDossier().getComposante());
        certificatPdf.setAddress(amenagement.getDossier().getIndividu().getFixAddress() + " " + amenagement.getDossier().getIndividu().getFixCP() + " " + amenagement.getDossier().getIndividu().getFixCity());
        certificatPdf.setNumEtu(amenagement.getDossier().getIndividu().getNumEtu());
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
        certificatPdf.setValideMedecinDate(amenagement.getValideMedecinDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        certificatPdf.setNomMedecin(amenagement.getNomMedecin());
        if(amenagement.getStatusAmenagement().equals(StatusAmenagement.VISE_ADMINISTRATION) && typeWorkflow.equals(TypeWorkflow.CERTIFICAT)) {
            certificatPdf.setAdministrationDate(amenagement.getAdministrationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
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
                pdField.getCOSObject().setString(COSName.DA, "/LiberationSans 11 Tf 0 g");
                if(datas.containsKey(fieldName)) {
                    pdField.setValue(datas.get(fieldName));
                }
                if(pdField instanceof PDSignatureField && withSign) {
                    addVisualSignature(amenagement, toFillDocument, pdField.getWidgets().get(0).getRectangle(), fieldName);
                }
                out = new ByteArrayOutputStream();
                toFillDocument.save(out);
                toFillDocument.close();
                savedPdf = out.toByteArray();
            }
        }
        PDDocument finishedDocument = PDDocument.load(savedPdf);
        finishedDocument.getDocumentCatalog().getAcroForm().flatten();
        out = new ByteArrayOutputStream();
        finishedDocument.save(out);
        finishedDocument.close();
        return out.toByteArray();
    }

    @Transactional
    public Amenagement getAmenagementPrec(Long amenagementId, Integer year) {
        Amenagement amenagement = getById(amenagementId);
        Individu individu = amenagement.getDossier().getIndividu();
        List<Amenagement> amenagements = amenagementRepository.findAmenagementPrec(individu, year);
        if(amenagements.size() > 0) {
            return amenagementRepository.findAmenagementPrec(individu, year).get(0);
        } else {
            return null;
        }
    }

    @Transactional
    public void porteAdministration(Long id, PersonLdap personLdap) throws AgapeJpaException {
        Amenagement amenagement = getById(id);
        Dossier currentDossier = dossierService.getCurrent(amenagement.getDossier().getIndividu().getId());
        currentDossier.setStatusDossierAmenagement(StatusDossierAmenagement.PORTE);
        currentDossier.setAmenagementPorte(amenagement);
        currentDossier.setMailValideurPortabilite(personLdap.getMail());
        currentDossier.setNomValideurPortabilite(personLdap.getDisplayName());
    }

    private void addVisualSignature(Amenagement amenagement, PDDocument doc, PDRectangle signRectangle, String fieldName) throws IOException
    {
        PDPageContentStream cs = new PDPageContentStream(doc, doc.getPage(0), PDPageContentStream.AppendMode.APPEND, false);
        File tmpDir = Files.createTempDirectory("esupagape").toFile();
        File signImage;
        if(StringUtils.hasText(applicationProperties.getSignaturesPath())) {
            signImage = new File(applicationProperties.getSignaturesPath() + "/signature-" + amenagement.getMailValideur() + ".jpg");
        } else {
            signImage = new File(tmpDir + "/signImage.jpg");
            ClassPathResource signImgResource = new ClassPathResource("/static/images/signature-" + amenagement.getMailValideur() + ".jpg");
            if(!signImgResource.exists()) {
                signImgResource = new ClassPathResource("/static/images/" + fieldName + ".png");
            }
            FileUtils.copyInputStreamToFile(signImgResource.getInputStream(), signImage);
        }
        PDImageXObject img = PDImageXObject.createFromFileByExtension(signImage, doc);
        float ratio = img.getHeight() / signRectangle.getHeight();
        cs.drawImage(img, signRectangle.getLowerLeftX(), signRectangle.getUpperRightY() - (img.getHeight() / ratio), img.getWidth() / ratio, img.getHeight() / ratio);
        cs.close();
    }

    @Transactional
    public SignatureStatus checkEsupSignatureStatus(Long amenagementId, TypeWorkflow typeWorkflow) {
        SignatureStatus signatureStatus = esupSignatureService.getStatus(amenagementId, typeWorkflow);
        if(signatureStatus.equals(SignatureStatus.COMPLETED)) {
            esupSignatureService.getLastPdf(amenagementId, typeWorkflow);
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
        List<Amenagement> amenagementsToSync = new ArrayList<>();
        amenagementsToSync.addAll(amenagementRepository.findByStatusAmenagementAndDossierYear(StatusAmenagement.ENVOYE, utilsService.getCurrentYear()));
        amenagementsToSync.addAll(amenagementRepository.findByStatusAmenagementAndDossierYear(StatusAmenagement.VALIDE_MEDECIN, utilsService.getCurrentYear()));
        logger.debug(amenagementsToSync.size() + " aménagements à synchroniser");
        for(Amenagement amenagement : amenagementsToSync) {
            syncEsupSignature(amenagement.getId());
        }
    }

    @Transactional
    public void syncAllAmenagements() {
        List<Amenagement> amenagementsToExpire = amenagementRepository.findByStatusAmenagementAndDossierYear(StatusAmenagement.VISE_ADMINISTRATION, utilsService.getCurrentYear());
        for(Amenagement amenagement : amenagementsToExpire) {
            LocalDateTime now = LocalDateTime.now().minusDays(1);
            if(amenagement.getTypeAmenagement().equals(TypeAmenagement.DATE) && amenagement.getEndDate().isBefore(now)) {
                amenagement.getDossier().setStatusDossierAmenagement(StatusDossierAmenagement.EXPIRE);
            }
        }
        List<Amenagement> amenagementsToSend = amenagementRepository.findByStatusAmenagementAndDossierYear(StatusAmenagement.VISE_ADMINISTRATION, utilsService.getCurrentYear());
        for(Amenagement amenagement : amenagementsToSend) {
            if (amenagement.getIndividuSendDate() == null) {
                sendAmenagementToIndividu(amenagement.getId(), false);
            }
        }
    }


    @Transactional
    public void sendAmenagementToIndividu(long amenagementId, boolean force) {
        Amenagement amenagement = getById(amenagementId);
        String to = amenagement.getDossier().getIndividu().getEmailEtu();
        if(StringUtils.hasText(applicationProperties.getTestEmail())) to = applicationProperties.getTestEmail();
        if((force || amenagement.getIndividuSendDate() == null) && amenagement.getStatusAmenagement().equals(StatusAmenagement.VISE_ADMINISTRATION)) {
            try {
                byte[] certificat;
                if(amenagement.getCertificat() != null ) {
                    certificat = amenagement.getCertificat().getInputStream().readAllBytes();
                } else {
                    byte[] modelBytes = new ClassPathResource("models/certificat.pdf").getInputStream().readAllBytes();
                    certificat = generateDocument(amenagement, modelBytes, TypeWorkflow.CERTIFICAT, true);
                }
                mailService.sendCertificat(new ByteArrayInputStream(certificat), to);
            } catch (Exception e) {
                logger.warn("Impossible d'envoyer le certificat par email, amenagementId : " + amenagementId, e);
            }
            amenagement.setIndividuSendDate(LocalDateTime.now());
        }
    }

}
