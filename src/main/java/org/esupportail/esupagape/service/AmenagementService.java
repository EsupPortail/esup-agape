package org.esupportail.esupagape.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.esupportail.esupagape.dtos.CertificatPdf;
import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.enums.Autorisation;
import org.esupportail.esupagape.entity.enums.Classification;
import org.esupportail.esupagape.entity.enums.StatusAmenagement;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.repository.AmenagementRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AmenagementService {

    private final AmenagementRepository amenagementRepository;
    private final DossierService dossierService;
    private final ObjectMapper objectMapper;

    public AmenagementService(AmenagementRepository amenagementRepository, DossierService dossierService, ObjectMapper objectMapper) {
        this.amenagementRepository = amenagementRepository;
        this.dossierService = dossierService;
        this.objectMapper = objectMapper;
    }

    public Amenagement getById(Long id) {
        return amenagementRepository.findById(id).orElseThrow();
    }

    public Page<Amenagement> findByDossier(Dossier dossier) {
        return amenagementRepository.findByDossierId(dossier.getId(), Pageable.unpaged());
    }

    public boolean isAmenagementValid(Long dossierId) {
        return amenagementRepository.findByDossierIdAndStatusAmenagement(dossierId, StatusAmenagement.VISER_ADMINISTRATION).size() > 0;
    }

    @Transactional
    public void create(Amenagement amenagement, Long idDossier) {
        Dossier dossier = dossierService.getById(idDossier);
        if (dossier.getStatusDossier().equals(StatusDossier.IMPORTE)) {
            dossier.setStatusDossier(StatusDossier.RECU_PAR_LA_MEDECINE_PREVENTIVE);
        }
        amenagement.setDossier(dossier);
        updateClassification(amenagement);
        amenagementRepository.save(amenagement);
    }

    @Transactional
    public void deleteAmenagement(Long amenagementId) {
        amenagementRepository.deleteById(amenagementId);
    }

    @Transactional
    public void softDeleteAmenagement(Long amenagementId) throws AgapeException {
        Amenagement amenagement = getById(amenagementId);
        if(amenagement.getStatusAmenagement().equals(StatusAmenagement.BROUILLON)) {
            amenagement.setStatusAmenagement(StatusAmenagement.SUPPRIME);
        } else {
            throw new AgapeException("Impossible de supprimer un aménagement qui n'est pas au statut brouillon");
        }
    }

    @Transactional
    public void update(Long amenagementId, Amenagement amenagement) throws AgapeJpaException {
        Amenagement amenagementToUpdate = getById(amenagementId);
        if(amenagementToUpdate.getStatusAmenagement().equals(StatusAmenagement.BROUILLON)){
        amenagementToUpdate.setMailMedecin(amenagement.getMailMedecin());
        amenagementToUpdate.setNomMedecin(amenagement.getNomMedecin());
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
                amenagement.getDossier().setClassification(amenagement.getClassification());
            }
            if (amenagement.getAutorisation().equals(Autorisation.NON)) {
                amenagement.getDossier().getClassification().clear();
                amenagement.getDossier().getClassification().add(Classification.REFUS);
            }
            if (amenagement.getAutorisation().equals(Autorisation.NC)) {
                amenagement.getDossier().getClassification().clear();
                amenagement.getDossier().getClassification().add(Classification.NON_COMMUNIQUE);
            }
        }
    }

    public Page<Amenagement> findAllPaged(Pageable pageable) {
        return amenagementRepository.findAll(pageable);
    }

    public Page<Amenagement> getFullTextSearch(StatusAmenagement statusAmenagement, String codComposante, Pageable pageable) {
        return amenagementRepository.findByFullTextSearch(statusAmenagement, codComposante, pageable);
    }

    @Transactional
    public void validationMedecin(Long id) throws AgapeException {
        Amenagement amenagement = getById(id);
        if(amenagement.getStatusAmenagement().equals(StatusAmenagement.BROUILLON)) {
            amenagement.setValideMedecinDate(LocalDateTime.now());
            amenagement.setStatusAmenagement(StatusAmenagement.VALIDER_MEDECIN);
        } else {
            throw new AgapeException("Impossible de valider un aménagement qui n'est pas au statut brouillon");
        }
    }

    @Transactional
    public void viserAdministration(Long id) throws AgapeException {
        Amenagement amenagement = getById(id);
        if(amenagement.getStatusAmenagement().equals(StatusAmenagement.VALIDER_MEDECIN)) {
            amenagement.setAdministrationDate(LocalDateTime.now());
            amenagement.setStatusAmenagement(StatusAmenagement.VISER_ADMINISTRATION);
        } else {
            throw new AgapeException("Impossible de viser un aménagement qui n'est pas au statut validé par le medecin");
        }
    }

    @Transactional
    public void getCertificat(Long id, HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.getOutputStream().write(generateCertificat(id));
    }

    private byte[] generateAvis(Long id) throws IOException {
        byte[] modelBytes = new ClassPathResource("models/certificat.pdf").getInputStream().readAllBytes();
         return generatePdf(null, modelBytes);
    }

    private byte[] generateCertificat(Long id) throws IOException {
        byte[] modelBytes = new ClassPathResource("models/certificat.pdf").getInputStream().readAllBytes();
        Amenagement amenagement = getById(id);
        CertificatPdf certificatPdf = new CertificatPdf();
        certificatPdf.setName(amenagement.getDossier().getIndividu().getName());
        certificatPdf.setFirstname(amenagement.getDossier().getIndividu().getFirstName());
        certificatPdf.setAddress(amenagement.getDossier().getIndividu().getCurrentAddress());
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
        }
        pdAcroForm.flatten();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        pdDocument.save(out);
        return out.toByteArray();
    }

}


