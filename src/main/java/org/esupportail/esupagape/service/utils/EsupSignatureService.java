package org.esupportail.esupagape.service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.Document;
import org.esupportail.esupagape.entity.DossierAmenagement;
import org.esupportail.esupagape.entity.enums.SignatureStatus;
import org.esupportail.esupagape.entity.enums.StatusAmenagement;
import org.esupportail.esupagape.entity.enums.StatusDossierAmenagement;
import org.esupportail.esupagape.entity.enums.TypeWorkflow;
import org.esupportail.esupagape.exception.AgapeRuntimeException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EsupSignatureService {

    private final Logger logger = LoggerFactory.getLogger(EsupSignatureService.class);

    private final ApplicationProperties applicationProperties;
    private final DocumentService documentService;

    public EsupSignatureService(ApplicationProperties applicationProperties, DocumentService documentService) {
        this.applicationProperties = applicationProperties;
        this.documentService = documentService;
    }

    @Transactional
    public void send(Amenagement amenagement, byte[] bytes, TypeWorkflow typeWorkflow) {
        String title = "";
        String signRequestId;
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("createByEppn", "system");
        String nomFichier = "amenagement_" + amenagement.getId() + ".pdf";
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
        requestEntity.getBody().add("scanSignatureFields", "true");
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        RestTemplate restTemplate = new RestTemplate();
        String workflowId;
        if (typeWorkflow.equals(TypeWorkflow.AVIS)) {
            workflowId = applicationProperties.getEsupSignatureAvisWorkflowId();
        } else {
            workflowId = applicationProperties.getEsupSignatureCertificatsWorkflowId();
        }
        String urlPostWorkflow = String.format("%s/ws/forms/%s/new-doc", applicationProperties.getEsupSignatureUrl(), workflowId);
        signRequestId = restTemplate.postForObject(urlPostWorkflow, requestEntity, String.class);
        if (org.springframework.util.StringUtils.hasText(signRequestId)) {
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
        } else {
            throw new AgapeRuntimeException("Erreur lors de la mise à la signature");
        }
        logger.info("aménagement : " + amenagement.getId() + " envoyé vie esup-signature à " + getRecipientEmails());
    }

    @Transactional
    public void getLastPdf(DossierAmenagement dossierAmenagement, TypeWorkflow typeWorkflow) {
        String signId;
        if (typeWorkflow.equals(TypeWorkflow.AVIS)) {
            if (dossierAmenagement.getAmenagement().getAvisSignatureStatus() == null || !dossierAmenagement.getAmenagement().getAvisSignatureStatus().equals(SignatureStatus.COMPLETED)) {
                return;
            }
            signId = dossierAmenagement.getAmenagement().getAvisSignatureId();
        } else {
            if (dossierAmenagement.getAmenagement().getCertificatSignatureStatus() == null || !dossierAmenagement.getAmenagement().getCertificatSignatureStatus().equals(SignatureStatus.COMPLETED)) {
                return;
            }
            signId = dossierAmenagement.getAmenagement().getCertificatSignatureId();
        }
        RestTemplate restTemplate = new RestTemplate();
        String urlStatus = String.format("%s/ws/signrequests/get-last-file/%s", applicationProperties.getEsupSignatureUrl(), signId);
        ResponseEntity<byte[]> bytes = restTemplate.getForEntity(urlStatus, byte[].class);
        byte[] pdf = bytes.getBody();
        Document document = documentService.createDocument(new ByteArrayInputStream(pdf), "Avis_" + dossierAmenagement.getAmenagement().getId(), "application/pdf", dossierAmenagement.getAmenagement().getId(), Amenagement.class.getSimpleName(), dossierAmenagement.getDossier());
        if (typeWorkflow.equals(TypeWorkflow.AVIS)) {
            dossierAmenagement.getAmenagement().setAvis(document);
            dossierAmenagement.getAmenagement().setAvisSignatureStatus(SignatureStatus.DOWNLOADED);
        } else {
            dossierAmenagement.getAmenagement().setCertificat(document);
            dossierAmenagement.getAmenagement().setCertificatSignatureStatus(SignatureStatus.DOWNLOADED);
        }
        deletePDF(dossierAmenagement.getAmenagement(), typeWorkflow);
    }
    
    @Transactional
    public SignatureStatus getStatus(DossierAmenagement dossierAmenagement, TypeWorkflow typeWorkflow) {
        String signId;
        if (typeWorkflow.equals(TypeWorkflow.AVIS)) {
            SignatureStatus avisSignatureStatus = dossierAmenagement.getAmenagement().getAvisSignatureStatus();
            if (avisSignatureStatus != null && avisSignatureStatus.equals(SignatureStatus.DOWNLOADED)) {
                return avisSignatureStatus;
            }
            signId = dossierAmenagement.getAmenagement().getAvisSignatureId();
        } else {
            SignatureStatus certificatSignatureStatus = dossierAmenagement.getAmenagement().getCertificatSignatureStatus();
            if (certificatSignatureStatus != null && certificatSignatureStatus.equals(SignatureStatus.DOWNLOADED)) {
                return certificatSignatureStatus;
            }
            signId = dossierAmenagement.getAmenagement().getCertificatSignatureId();
        }
        RestTemplate restTemplate = new RestTemplate();
        if (signId == null || signId.isEmpty()) {
            return SignatureStatus.PENDING;
        } else {
            String urlStatus = String.format("%s/ws/signrequests/status/%s", applicationProperties.getEsupSignatureUrl(), signId);
            ResponseEntity<String> responseEntityStatus = restTemplate.getForEntity(urlStatus, String.class);
            SignatureStatus signatureStatus = SignatureStatus.valueOf(responseEntityStatus.getBody().toUpperCase().replace('-', '_'));
            if (typeWorkflow.equals(TypeWorkflow.AVIS)) {
                dossierAmenagement.getAmenagement().setAvisSignatureStatus(signatureStatus);
                if(signatureStatus.equals(SignatureStatus.COMPLETED)) {
                    dossierAmenagement.getAmenagement().setStatusAmenagement(StatusAmenagement.VALIDE_MEDECIN);
                    dossierAmenagement.setStatusDossierAmenagement(StatusDossierAmenagement.EN_ATTENTE);
                } else if(signatureStatus.equals(SignatureStatus.REFUSED)) {
                    dossierAmenagement.getAmenagement().setStatusAmenagement(StatusAmenagement.SUPPRIME);
                    dossierAmenagement.setStatusDossierAmenagement(StatusDossierAmenagement.NON);
                }
            } else {
                dossierAmenagement.getAmenagement().setCertificatSignatureStatus(signatureStatus);
                if(signatureStatus.equals(SignatureStatus.COMPLETED)) {
                    dossierAmenagement.getAmenagement().setStatusAmenagement(StatusAmenagement.VISE_ADMINISTRATION);
                    dossierAmenagement.setStatusDossierAmenagement(StatusDossierAmenagement.VALIDE);
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
                    dossierAmenagement.getAmenagement().setMotifRefus(((HashMap<String, Object>)((ArrayList) signRequestMap.get("comments")).get(0)).get("text").toString());
                    dossierAmenagement.getAmenagement().setStatusAmenagement(StatusAmenagement.REFUSE_ADMINISTRATION);
                    dossierAmenagement.setStatusDossierAmenagement(StatusDossierAmenagement.NON);
                }
            }
            return signatureStatus;
        }
    }
    public void deletePDF(Amenagement amenagement, TypeWorkflow typeWorkflow) {
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