package org.esupportail.esupagape.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.apache.pdfbox.util.Matrix;
import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.dtos.pdfs.CertificatPdf;
import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.exception.AgapeRuntimeException;
import org.esupportail.esupagape.exception.AgapeYearException;
import org.esupportail.esupagape.repository.AmenagementRepository;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AmenagementService {

    private final ApplicationProperties applicationProperties;
    private final AmenagementRepository amenagementRepository;
    private final DossierService dossierService;
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;
    private final UtilsService utilsService;

    public AmenagementService(ApplicationProperties applicationProperties, AmenagementRepository amenagementRepository, DossierService dossierService, ObjectMapper objectMapper, MessageSource messageSource, UtilsService utilsService) {
        this.applicationProperties = applicationProperties;
        this.amenagementRepository = amenagementRepository;
        this.dossierService = dossierService;
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
        this.utilsService = utilsService;
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

    @Transactional
    public void create(Amenagement amenagement, Long idDossier, PersonLdap personLdap) throws AgapeException {
        Dossier dossier = dossierService.getById(idDossier);
        if(dossier.getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        if(amenagement.getTypeAmenagement().equals(TypeAmenagement.DATE) && amenagement.getEndDate() == null) {
            throw new AgapeException("Impossible de créer l'aménagement sans date de fin");
        }
        if (dossier.getStatusDossier().equals(StatusDossier.IMPORTE)) {
            dossier.setStatusDossier(StatusDossier.RECU_PAR_LA_MEDECINE_PREVENTIVE);
        }
        amenagement.setDossier(dossier);
        amenagement.setNomMedecin(personLdap.getDisplayName());
        amenagement.setMailMedecin(personLdap.getMail());
        updateClassification(amenagement);
        amenagementRepository.save(amenagement);
    }

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

    @Transactional
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
    public void validationMedecin(Long id) throws AgapeException {
        Amenagement amenagement = getById(id);
        if(amenagement.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        if(amenagement.getStatusAmenagement().equals(StatusAmenagement.BROUILLON)) {
            amenagement.setValideMedecinDate(LocalDateTime.now());
            amenagement.setStatusAmenagement(StatusAmenagement.VALIDE_MEDECIN);
            amenagement.getDossier().setStatusDossierAmenagement(StatusDossierAmenagement.EN_ATTENTE);
        } else {
            throw new AgapeException("Impossible de valider un aménagement qui n'est pas au statut brouillon");
        }
    }

    @Transactional
    public void validationAdministration(Long id, PersonLdap personLdap) throws AgapeException {
        Amenagement amenagement = getById(id);
        if(amenagement.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        if(amenagement.getStatusAmenagement().equals(StatusAmenagement.VALIDE_MEDECIN)) {
            if(StringUtils.hasText(applicationProperties.getEsupSignatureUrl())) {

            } else {
                amenagement.setAdministrationDate(LocalDateTime.now());
                amenagement.setStatusAmenagement(StatusAmenagement.VISE_ADMINISTRATION);
                amenagement.setNomValideur(personLdap.getDisplayName());
                amenagement.setMailValideur(personLdap.getMail());
                amenagement.getDossier().setStatusDossierAmenagement(StatusDossierAmenagement.VALIDE);
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
        byte[] modelBytes = new ClassPathResource("models/certificat.pdf").getInputStream().readAllBytes();
        httpServletResponse.getOutputStream().write(generateDocument(amenagement, modelBytes));
    }

    @Transactional
    public void getAvis(Long id, HttpServletResponse httpServletResponse) throws IOException, AgapeException {
        Amenagement amenagement = getById(id);
        if(!amenagement.getStatusAmenagement().equals(StatusAmenagement.VALIDE_MEDECIN) && !amenagement.getStatusAmenagement().equals(StatusAmenagement.VISE_ADMINISTRATION)) {
            throw new AgapeException("L'avis ne peut pas être émis");
        }
        byte[] modelBytes = new ClassPathResource("models/avis.pdf").getInputStream().readAllBytes();
        httpServletResponse.getOutputStream().write(generateDocument(amenagement, modelBytes));
    }

    private byte[] generateDocument(Amenagement amenagement, byte[] modelBytes) throws IOException {
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
            amenagementsWithNumbers.append(i).append(" - ").append(line).append("\n");
            i++;
        }
        certificatPdf.setAutresTypeEpreuve(amenagement.getAutresTypeEpreuve());
        certificatPdf.setAutresTempsMajores(amenagement.getAutresTempsMajores());
        certificatPdf.setAmenagementText(amenagementsWithNumbers.toString());
        certificatPdf.setValideMedecinDate(amenagement.getValideMedecinDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        certificatPdf.setNomMedecin(amenagement.getNomMedecin());
        if(amenagement.getStatusAmenagement().equals(StatusAmenagement.VISE_ADMINISTRATION)) {
            //ajouter des données du visa
            certificatPdf.setAdministrationDate(amenagement.getAdministrationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            certificatPdf.setNomValideur(amenagement.getNomValideur());
        }
        TypeReference<Map<String, String>> datasTypeReference = new TypeReference<>(){};
        return generatePdf(objectMapper.convertValue(certificatPdf, datasTypeReference), modelBytes);
    }

    private byte[] generatePdf(Map<String, String> datas, byte[] model) throws IOException {
        PDDocument pdDocument = PDDocument.load(model);
        PDAcroForm pdAcroForm = pdDocument.getDocumentCatalog().getAcroForm();
        byte[] ttfBytes = new ClassPathResource("/static/fonts/LiberationSans-Regular.ttf").getInputStream().readAllBytes();
        PDFont pdFont = PDTrueTypeFont.load(pdDocument, new ByteArrayInputStream(ttfBytes), WinAnsiEncoding.INSTANCE);
        PDResources resources = pdAcroForm.getDefaultResources();
        resources.put(COSName.getPDFName("LiberationSans"), pdFont);
        pdAcroForm.setDefaultResources(resources);
        List<PDField> fields = pdAcroForm.getFields();
        for(PDField pdField : fields) {
            pdField.getCOSObject().setString(COSName.DA, "/LiberationSans 11 Tf 0 g");
            String fieldName = pdField.getPartialName();
            if(datas.containsKey(fieldName)) {
                pdField.setValue(datas.get(fieldName));
            }
            if(pdField instanceof PDSignatureField && pdField.getPartialName().equals("signatureValideur")) {
                PDSignature pdSignature = new PDSignature();
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(datas.get("administrationDate")));
                } catch (ParseException e) {
                    throw new AgapeRuntimeException(e.getMessage());
                }
                pdSignature.setSignDate(calendar);
                SignatureOptions signatureOptions = new SignatureOptions();
                signatureOptions.setVisualSignature(createVisualSignatureTemplate(pdDocument, pdField.getWidgets().get(0).getRectangle(), pdSignature, datas));
                signatureOptions.setPage(0);
                pdDocument.addSignature(pdSignature, signatureOptions);
                ((PDSignatureField) pdField).setValue(pdSignature);
                ((PDSignatureField) pdField).setDefaultValue(pdSignature);
            }
        }
        pdAcroForm.flatten();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        pdDocument.save(out);
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

    private InputStream createVisualSignatureTemplate(PDDocument srcDoc, PDRectangle rect, PDSignature signature, Map<String, String> datas) throws IOException
    {
        try (PDDocument doc = new PDDocument())
        {
            PDPage page = new PDPage(srcDoc.getPage(0).getMediaBox());
            doc.addPage(page);
            PDAcroForm acroForm = new PDAcroForm(doc);
            doc.getDocumentCatalog().setAcroForm(acroForm);
            PDSignatureField signatureField = new PDSignatureField(acroForm);
            PDAnnotationWidget widget = signatureField.getWidgets().get(0);
            List<PDField> acroFormFields = acroForm.getFields();
            acroForm.setSignaturesExist(true);
            acroForm.setAppendOnly(true);
            acroForm.getCOSObject().setDirect(true);
            acroFormFields.add(signatureField);

            widget.setRectangle(rect);

            // from PDVisualSigBuilder.createHolderForm()
            PDStream stream = new PDStream(doc);
            PDFormXObject form = new PDFormXObject(stream);
            PDResources res = new PDResources();
            form.setResources(res);
            form.setFormType(1);
            PDRectangle bbox = new PDRectangle(rect.getWidth(), rect.getHeight());
            float height = bbox.getHeight();
            Matrix initialScale = null;
            switch (srcDoc.getPage(0).getRotation())
            {
                case 90:
                    form.setMatrix(AffineTransform.getQuadrantRotateInstance(1));
                    initialScale = Matrix.getScaleInstance(bbox.getWidth() / bbox.getHeight(), bbox.getHeight() / bbox.getWidth());
                    height = bbox.getWidth();
                    break;
                case 180:
                    form.setMatrix(AffineTransform.getQuadrantRotateInstance(2));
                    break;
                case 270:
                    form.setMatrix(AffineTransform.getQuadrantRotateInstance(3));
                    initialScale = Matrix.getScaleInstance(bbox.getWidth() / bbox.getHeight(), bbox.getHeight() / bbox.getWidth());
                    height = bbox.getWidth();
                    break;
                case 0:
                default:
                    break;
            }
            form.setBBox(bbox);
            PDFont font = PDType1Font.HELVETICA;

            // from PDVisualSigBuilder.createAppearanceDictionary()
            PDAppearanceDictionary appearance = new PDAppearanceDictionary();
            appearance.getCOSObject().setDirect(true);
            PDAppearanceStream appearanceStream = new PDAppearanceStream(form.getCOSObject());
            appearance.setNormalAppearance(appearanceStream);
            widget.setAppearance(appearance);

            try (PDPageContentStream cs = new PDPageContentStream(doc, appearanceStream))
            {
                if (initialScale != null)
                {
                    cs.transform(initialScale);
                }
                Color color = new Color(0x00AAFFAA, true);
                cs.setNonStrokingColor(color);
                cs.addRect(-5000, -5000, 10000, 10000);
                cs.fill();
                cs.saveGraphicsState();
                cs.transform(Matrix.getScaleInstance(0.3f, 0.3f));
                ClassPathResource noImg = new ClassPathResource("/static/images/signature-valideur.png");
                PDImageXObject img = PDImageXObject.createFromFileByExtension(noImg.getFile(), doc);
                cs.drawImage(img, rect.getWidth() / 2, 0);
                cs.restoreGraphicsState();
                float fontSize = 10;
                float leading = fontSize * 1.5f;
                cs.beginText();
                cs.setFont(font, fontSize);
                cs.setNonStrokingColor(Color.black);
                cs.newLineAtOffset(fontSize, height - leading);
                cs.setLeading(leading);
                String date = new SimpleDateFormat("dd/MM/yyyy").format(signature.getSignDate().getTime());
                cs.showText("Valideur: " + datas.get("nomValideur"));
                cs.newLine();
                cs.showText("le : " + date);
                cs.endText();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return new ByteArrayInputStream(baos.toByteArray());
        }
    }

}
