package org.esupportail.esupagape.service.utils;

import org.apache.commons.lang3.StringUtils;
import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.Document;
import org.esupportail.esupagape.entity.enums.SignatureStatus;
import org.esupportail.esupagape.exception.AgapeRuntimeException;
import org.esupportail.esupagape.repository.AmenagementRepository;
import org.esupportail.esupagape.service.DocumentService;
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
import java.util.List;

@Service
public class EsupSignatureService {

    private final Logger log = LoggerFactory.getLogger(EsupSignatureService.class);

    private final ApplicationProperties applicationProperties;
    private final AmenagementRepository amenagementRepository;
    private final DocumentService documentService;

    public EsupSignatureService(ApplicationProperties applicationProperties, AmenagementRepository amenagementRepository, DocumentService documentService) {
        this.applicationProperties = applicationProperties;
        this.amenagementRepository = amenagementRepository;
        this.documentService = documentService;
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
        if(typeWorkflow.equals(TypeWorkflow.AVIS)) {
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
        if(typeWorkflow.equals(TypeWorkflow.AVIS)) {
            workflowId = applicationProperties.getEsupSignatureAvisWorkflowId();
        } else {
            workflowId = applicationProperties.getEsupSignatureCertificatsWorkflowId();
        }
        String urlPostWorkflow = String.format("%s/ws/workflows/%s/new", applicationProperties.getEsupSignatureUrl(), workflowId);
        signRequestId = restTemplate.postForObject(urlPostWorkflow, requestEntity, String.class);
        if(signRequestId != null) {
            if(signRequestId.equals("-1")) {
                throw new AgapeRuntimeException("Erreur lors de la mise à la signature");
            }
            if(typeWorkflow.equals(TypeWorkflow.AVIS)) {
                amenagement.setAvisSignatureId(signRequestId);
            } else {
                amenagement.setCertificatSignatureId(signRequestId);
            }
        }
    }

    public void getLastPdf(Long amenagementId, TypeWorkflow typeWorkflow) {
        Amenagement amenagement = amenagementRepository.findById(amenagementId).orElseThrow();
        String signId;
        if (typeWorkflow.equals(TypeWorkflow.AVIS)) {
            signId = amenagement.getAvisSignatureId();
        } else {
            signId = amenagement.getCertificatSignatureId();
        }
        RestTemplate restTemplate = new RestTemplate();
        String urlStatus = String.format("%s/ws/signrequests/get-last-file/%s", applicationProperties.getEsupSignatureUrl(), signId);
        ResponseEntity<byte[]> bytes = restTemplate.getForEntity(urlStatus, byte[].class);
        byte[] pdf = bytes.getBody();
        Document document = documentService.createDocument(new ByteArrayInputStream(pdf), "Avis_" + amenagementId, "application/pdf", amenagementId, Amenagement.class.getTypeName(), amenagement.getDossier());
        if (typeWorkflow.equals(TypeWorkflow.AVIS)) {
            amenagement.setAvis(document);
        } else {
            amenagement.setCertificat(document);
        }
    }

    public void getStatus(Long amenagementId, TypeWorkflow typeWorkflow) {
        Amenagement amenagement = amenagementRepository.findById(amenagementId).orElseThrow();
        String signId;
        if (typeWorkflow.equals(TypeWorkflow.AVIS)) {
            signId = amenagement.getAvisSignatureId();
        } else {
            signId = amenagement.getCertificatSignatureId();
        }
        RestTemplate restTemplate = new RestTemplate();
        String urlStatus = String.format("%s/ws/signrequests/status/%s", applicationProperties.getEsupSignatureUrl(), signId);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(urlStatus, String.class);
        String status = responseEntity.getBody().toUpperCase();
        if (typeWorkflow.equals(TypeWorkflow.AVIS)) {
            amenagement.setAvisSignatureStatus(SignatureStatus.valueOf(status));
        } else {
            amenagement.setCertificatSignatureStatus(SignatureStatus.valueOf(status));
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
        String urlDeletePdf = String.format("%s/ws/signrequests/%s", applicationProperties.getEsupSignatureUrl(), signId);
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

    public enum TypeWorkflow {
        AVIS, CERTIFICAT
    }
}