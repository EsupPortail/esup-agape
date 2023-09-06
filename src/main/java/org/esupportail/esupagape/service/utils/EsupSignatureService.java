package org.esupportail.esupagape.service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.Document;
import org.esupportail.esupagape.entity.enums.SignatureStatus;
import org.esupportail.esupagape.entity.enums.StatusAmenagement;
import org.esupportail.esupagape.entity.enums.StatusDossierAmenagement;
import org.esupportail.esupagape.entity.enums.TypeWorkflow;
import org.esupportail.esupagape.exception.AgapeRuntimeException;
import org.esupportail.esupagape.repository.AmenagementRepository;
import org.esupportail.esupagape.service.DocumentService;
import org.esupportail.esupagape.service.mail.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EsupSignatureService {

    private final Logger logger = LoggerFactory.getLogger(EsupSignatureService.class);

    private final ApplicationProperties applicationProperties;
    private final AmenagementRepository amenagementRepository;
    private final DocumentService documentService;

    private final MailService mailService;

    public EsupSignatureService(ApplicationProperties applicationProperties, AmenagementRepository amenagementRepository, DocumentService documentService, MailService mailService) {
        this.applicationProperties = applicationProperties;
        this.amenagementRepository = amenagementRepository;
        this.documentService = documentService;
        this.mailService = mailService;
    }

    @Transactional
    public void send(Long amenagementId, byte[] bytes, TypeWorkflow typeWorkflow) {
        Amenagement amenagement = amenagementRepository.findById(amenagementId).orElseThrow();
        String title = "";
        String signRequestId;
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("createByEppn", "system");
        String nomFichier = "amenagement_" + amenagementId + ".pdf";
        ByteArrayResource contentsAsResource = new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return nomFichier;
            }
        };
        map.add("multipartFiles", contentsAsResource);
        if (typeWorkflow.equals(TypeWorkflow.AVIS)) {
            map.add("recipientEmails", "1*" + amenagement.getMailMedecin());
        } else {

            map.add("recipientEmails", getRecipientEmails());
        }
        map.add("title", title);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        RestTemplate restTemplate = new RestTemplate();
        String workflowId;
        if (typeWorkflow.equals(TypeWorkflow.AVIS)) {
            workflowId = applicationProperties.getEsupSignatureAvisWorkflowId();
        } else {
            workflowId = applicationProperties.getEsupSignatureCertificatsWorkflowId();
        }
        String urlPostWorkflow = String.format("%s/ws/workflows/%s/new", applicationProperties.getEsupSignatureUrl(), workflowId);
        signRequestId = restTemplate.postForObject(urlPostWorkflow, requestEntity, String.class);
        if (signRequestId != null) {
            if (signRequestId.equals("-1")) {
                throw new AgapeRuntimeException("Erreur lors de la mise à la signature");
            }
            if (typeWorkflow.equals(TypeWorkflow.AVIS)) {
                amenagement.setAvisSignatureId(signRequestId);
                amenagement.setAvisSignatureStatus(SignatureStatus.PENDING);
            } else {
                amenagement.setCertificatSignatureId(signRequestId);
                amenagement.setCertificatSignatureStatus(SignatureStatus.PENDING);
            }
        }
        logger.info("aménagement : " + amenagement.getId() + " envoyé vie esup-signature à " + getRecipientEmails());
    }

    @Transactional
    public void getLastPdf(Long amenagementId, TypeWorkflow typeWorkflow) {
        Amenagement amenagement = amenagementRepository.findById(amenagementId).orElseThrow();
        String signId;
        if (typeWorkflow.equals(TypeWorkflow.AVIS)) {
            if (amenagement.getAvisSignatureStatus() == null || !amenagement.getAvisSignatureStatus().equals(SignatureStatus.COMPLETED)) {
                return;
            }
            signId = amenagement.getAvisSignatureId();
        } else {
            if (amenagement.getCertificatSignatureStatus() == null || !amenagement.getCertificatSignatureStatus().equals(SignatureStatus.COMPLETED)) {
                return;
            }
            signId = amenagement.getCertificatSignatureId();
        }
        RestTemplate restTemplate = new RestTemplate();
        String urlStatus = String.format("%s/ws/signrequests/get-last-file/%s", applicationProperties.getEsupSignatureUrl(), signId);
        ResponseEntity<byte[]> bytes = restTemplate.getForEntity(urlStatus, byte[].class);
        byte[] pdf = bytes.getBody();
        Document document = documentService.createDocument(new ByteArrayInputStream(pdf), "Avis_" + amenagementId, "application/pdf", amenagementId, Amenagement.class.getSimpleName(), amenagement.getDossier());
        if (typeWorkflow.equals(TypeWorkflow.AVIS)) {
            amenagement.setAvis(document);
            amenagement.setAvisSignatureStatus(SignatureStatus.DOWNLOADED);
        } else {
            amenagement.setCertificat(document);
            amenagement.setCertificatSignatureStatus(SignatureStatus.DOWNLOADED);
        }
        deletePDF(amenagementId, typeWorkflow);
    }
    
    @Transactional
    public SignatureStatus getStatus(Long amenagementId, TypeWorkflow typeWorkflow) {
        Amenagement amenagement = amenagementRepository.findById(amenagementId).orElseThrow();
        String signId;
        if (typeWorkflow.equals(TypeWorkflow.AVIS)) {
            SignatureStatus avisSignatureStatus = amenagement.getAvisSignatureStatus();
            if (avisSignatureStatus != null && avisSignatureStatus.equals(SignatureStatus.DOWNLOADED)) {
                return avisSignatureStatus;
            }
            signId = amenagement.getAvisSignatureId();
        } else {
            SignatureStatus certificatSignatureStatus = amenagement.getCertificatSignatureStatus();
            if (certificatSignatureStatus != null && certificatSignatureStatus.equals(SignatureStatus.DOWNLOADED)) {
                return certificatSignatureStatus;
            }
            signId = amenagement.getCertificatSignatureId();
        }
        RestTemplate restTemplate = new RestTemplate();
        if (signId == null || signId.isEmpty()) {
            return SignatureStatus.PENDING;
        } else {
            String urlStatus = String.format("%s/ws/signrequests/status/%s", applicationProperties.getEsupSignatureUrl(), signId);
            ResponseEntity<String> responseEntityStatus = restTemplate.getForEntity(urlStatus, String.class);
            SignatureStatus signatureStatus = SignatureStatus.valueOf(responseEntityStatus.getBody().toUpperCase());
            if (typeWorkflow.equals(TypeWorkflow.AVIS)) {
                amenagement.setAvisSignatureStatus(signatureStatus);
                if(signatureStatus.equals(SignatureStatus.COMPLETED)) {
                    amenagement.setStatusAmenagement(StatusAmenagement.VALIDE_MEDECIN);
                    amenagement.getDossier().setStatusDossierAmenagement(StatusDossierAmenagement.EN_ATTENTE);
                } else if(signatureStatus.equals(SignatureStatus.REFUSED)) {
                    amenagement.setStatusAmenagement(StatusAmenagement.SUPPRIME);
                    amenagement.getDossier().setStatusDossierAmenagement(StatusDossierAmenagement.NON);
                }
            } else {
                amenagement.setCertificatSignatureStatus(signatureStatus);
                if(signatureStatus.equals(SignatureStatus.COMPLETED)) {
                    amenagement.setStatusAmenagement(StatusAmenagement.VISE_ADMINISTRATION);
                    amenagement.getDossier().setStatusDossierAmenagement(StatusDossierAmenagement.VALIDE);
                } else if(signatureStatus.equals(SignatureStatus.REFUSED)) {
                    String urlSignRequest = String.format("%s/ws/signrequests/%s", applicationProperties.getEsupSignatureUrl(), signId);
                    ResponseEntity<String> responseEntitySignRequest = restTemplate.getForEntity(urlSignRequest, String.class);
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> signRequestMap;
                    try {
                        signRequestMap = mapper.readValue(responseEntitySignRequest.getBody(), Map.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    amenagement.setMotifRefus(((HashMap<String, Object>)((ArrayList) signRequestMap.get("comments")).get(0)).get("text").toString());
                    amenagement.setStatusAmenagement(StatusAmenagement.REFUSE_ADMINISTRATION);
                    amenagement.getDossier().setStatusDossierAmenagement(StatusDossierAmenagement.NON);
                }
            }
            return signatureStatus;
        }
    }
    public void deletePDF(Long amenagementId, TypeWorkflow typeWorkflow) {
        Amenagement amenagement = amenagementRepository.findById(amenagementId).orElseThrow();
        String signId;
        if (typeWorkflow.equals(TypeWorkflow.AVIS)) {
            signId = amenagement.getAvisSignatureId();
        } else {
            signId = amenagement.getCertificatSignatureId();
        }
        String urlDeletePdf = String.format("%s/ws/signrequests/soft/%s", applicationProperties.getEsupSignatureUrl(), signId);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(urlDeletePdf);
    }

    public String getRecipientEmails() {
        String strEmails = "";
        List<String> emails = applicationProperties.getEsupSignatureValideursEmails();
        if(!emails.isEmpty()) {
            List<String> formattedEmails = new ArrayList<String>();
            for(String email : emails) {
                formattedEmails.add("1*".concat(email));
            }
            strEmails = StringUtils.join(formattedEmails, ",");
        }

        return strEmails;
    }

}