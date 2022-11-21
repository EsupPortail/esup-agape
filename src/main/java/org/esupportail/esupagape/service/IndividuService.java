package org.esupportail.esupagape.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.ExcludeIndividu;
import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.repository.ExcludeIndividuRepository;
import org.esupportail.esupagape.repository.IndividuRepository;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuInfos;
import org.esupportail.esupagape.service.interfaces.importindividu.IndividuSourceService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@EnableConfigurationProperties(ApplicationProperties.class)
public class IndividuService {

    private static final Logger logger = LoggerFactory.getLogger(IndividuService.class);

    private final List<IndividuSourceService> individuSourceServices;

    private final ApplicationProperties applicationProperties;

    private final IndividuRepository individuRepository;

    private final UtilsService utilsService;

    private final ExcludeIndividuRepository excludeIndividuRepository;

    private final DossierService dossierService;

    public IndividuService(List<IndividuSourceService> individuSourceServices, ApplicationProperties applicationProperties, IndividuRepository individuRepository, UtilsService utilsService, ExcludeIndividuRepository excludeIndividuRepository, DossierService dossierService) {
        this.individuSourceServices = individuSourceServices;
        this.applicationProperties = applicationProperties;
        this.individuRepository = individuRepository;
        this.utilsService = utilsService;
        this.excludeIndividuRepository = excludeIndividuRepository;
        this.dossierService = dossierService;
    }

    public Individu getIndividu(String numEtu) {
        return individuRepository.findByNumEtu(numEtu);
    }

    public Individu getIndividu(String name, String firstName, LocalDate dateOfBirth) {
        return individuRepository.findByNameIgnoreCaseAndFirstNameIgnoreCaseAndDateOfBirth(name, firstName, dateOfBirth);
    }

    public List<Individu> getAllIndividus() {
        return individuRepository.findAll();
    }

    @Transactional
    public void syncIndividu(Long id) {
        Individu individu = individuRepository.findById(id).orElseThrow();
        IndividuInfos individuInfos = new IndividuInfos();
        for (IndividuSourceService individuSourceService : individuSourceServices) {
            individuInfos = individuSourceService.getIndividuProperties(individu.getNumEtu(), individuInfos);
        }
        if(individuInfos.getEppn() != null && !individuInfos.getEppn().isEmpty()) {
            individu.setEppn(individuInfos.getEppn());
        }
        if(individuInfos.getName() != null && !individuInfos.getName().isEmpty()) {
            individu.setName(individuInfos.getName());
        }
        if(individuInfos.getFirstName() != null && !individuInfos.getFirstName().isEmpty()) {
            individu.setFirstName(individuInfos.getFirstName());
        }
        if(individuInfos.getGenre() != null && !individuInfos.getGenre().isEmpty()) {
            individu.setSex(individuInfos.getGenre());
        }
        if(individuInfos.getGenre() != null && !individuInfos.getGenre().isEmpty()) {
            individu.setGender(Gender.valueOf(individuInfos.getGenre()));
        }
        if(individuInfos.getEmailEtu() != null && !individuInfos.getEmailEtu().isEmpty()) {
            individu.setEmailEtu(individuInfos.getEmailEtu());
        }
        if(individuInfos.getEmailPerso() != null && !individuInfos.getEmailPerso().isEmpty()) {
            individu.setEmailPerso(individuInfos.getEmailPerso());
        }
        if(individuInfos.getFixAddress() != null && !individuInfos.getFixAddress().isEmpty()) {
            individu.setFixAddress(individuInfos.getFixAddress());
        }
        if(individuInfos.getFixCP() != null && !individuInfos.getFixCP().isEmpty()) {
            individu.setFixCP(individuInfos.getFixCP());
        }
        if(individuInfos.getFixCity() != null && !individuInfos.getFixCity().isEmpty()) {
            individu.setFixCity(individuInfos.getFixCity());
        }
        if(individuInfos.getFixCountry() != null && !individuInfos.getFixCountry().isEmpty()) {
            individu.setFixCountry(individuInfos.getFixCountry());
        }
        if(individuInfos.getFixPhone() != null && !individuInfos.getFixPhone().isEmpty()) {
            individu.setFixPhone(individuInfos.getFixPhone());
        }
        if(individuInfos.getContactPhone() != null && !individuInfos.getContactPhone().isEmpty()) {
            individu.setContactPhone(individuInfos.getContactPhone());
        }
        if(individuInfos.getPhotoId() != null && !individuInfos.getPhotoId().isEmpty()) {
            individu.setPhotoId(individuInfos.getPhotoId());
        }
    }

    @Transactional
    public void syncAllIndividus() {
        List<Individu> individus = individuRepository.findAll();
        for (Individu individu : individus) {
            syncIndividu(individu.getId());
        }
        logger.info("Sync individus done");
    }

    @Transactional
    public void importIndividus() {
        List<Individu> individus = individuRepository.findAll();
        List<ExcludeIndividu> excludeIndividus = excludeIndividuRepository.findAll();
        for (IndividuSourceService individuSourceService : individuSourceServices) {
            List<Individu> individusFromSource = individuSourceService.getAllIndividuNums();
            logger.info("{}", individusFromSource.size());
            List<Individu> individusToCreate = individusFromSource.stream().filter(
                    individuToCreate -> individus.stream().noneMatch(individuInDataBase -> individuInDataBase.getNumEtu() != null && individuInDataBase.getNumEtu().equals(individuToCreate.getNumEtu()))
                    &&
                    excludeIndividus.stream().noneMatch(excludeIndividu -> excludeIndividu.getNumEtuHash().equals(new DigestUtils("SHA3-256").digestAsHex(individuToCreate.getNumEtu())))
            ).toList();
            individuRepository.saveAll(individusToCreate);
            List<Individu> individusWithoutDossier = new ArrayList<>();
            individusWithoutDossier.addAll(individusToCreate);
            individusWithoutDossier.addAll(individus.stream().filter(individu -> individu.getDossiers().stream().noneMatch(dossier -> dossier.getYear() == utilsService.getCurrentYear())).toList());
            List<Dossier> dossiers = new ArrayList<>();
            for(Individu individu : individusWithoutDossier) {
                Dossier dossier = dossierService.create(individu, StatusDossier.IMPORTE);
                dossiers.add(dossier);
                individu.getDossiers().add(dossier);
            }
            dossierService.saveAll(dossiers);
        }
        logger.info("Import individus done");
    }

    public void save(Individu individuToAdd, String force) throws AgapeJpaException {
        ExcludeIndividu excludeIndividu = null;
        if(individuToAdd.getNumEtu() != null && individuToAdd.getNumEtu().isEmpty()) {
             excludeIndividu = excludeIndividuRepository.findByNumEtuHash(new DigestUtils("SHA3-256").digestAsHex(individuToAdd.getNumEtu()));
            if (force == null && excludeIndividu != null) {
                throw new AgapeJpaException("L'étudiant est dans la liste d'exclusion");
            }
        }
        Individu foundIndividu = null;
        if (individuToAdd.getNumEtu() != null && !individuToAdd.getNumEtu().isEmpty()) {
            foundIndividu = individuRepository.findByNumEtu(individuToAdd.getNumEtu());
        } else {
            //numEtu à null pour éviter la contrainte d’unicité
            individuToAdd.setNumEtu(null);
        }
        if (foundIndividu != null) {
            dossierService.create(foundIndividu, StatusDossier.AJOUT_MANUEL);
        } else {
            if (excludeIndividu != null) {
                // suppression de l'exclusion si l’insertion est forcée
                excludeIndividuRepository.delete(excludeIndividu);
            }
            individuRepository.save(individuToAdd);
            dossierService.create(individuToAdd, StatusDossier.AJOUT_MANUEL);
        }
    }

    public Individu findById(Long id) throws AgapeException {
        Optional<Individu> optionalIndividu = individuRepository.findById(id);
        if (optionalIndividu.isPresent()) {
            return optionalIndividu.get();
        } else {
            throw new AgapeException("Je n'ai pas trouvé cet individu");
        }
    }

    public Page<Individu> searchByName(String name, Pageable pageable) {
        return individuRepository.findAllByNameContainsIgnoreCase(name, pageable);
    }

    @Transactional
    public Individu create(Individu individu, String force) throws AgapeJpaException {
        Individu individuTestIsExist = null;
//        individu.setName(StringUtils.capitalize(individu.getName()));
//        individu.setFirstName(StringUtils.capitalize(individu.getFirstName()));
        if (individu.getNumEtu() != null && !individu.getNumEtu().isEmpty()) {
            individuTestIsExist = getIndividu(individu.getNumEtu());
            if (individuTestIsExist == null) {
                return createFromSources(individu.getNumEtu(), force);
            }
        } else if (!individu.getName().isEmpty() && !individu.getFirstName().isEmpty() && individu.getDateOfBirth() != null) {
            individuTestIsExist = getIndividu(individu.getName(), individu.getFirstName(), individu.getDateOfBirth());
            if (individuTestIsExist == null) {
                Individu newIndividu = createFromSources(individu.getName(), individu.getFirstName(), individu.getDateOfBirth(), force);
                if (newIndividu != null) {
                    return newIndividu;
                } else {
                    save(individu, force);
                    return individu;
                }
            }
        }
        if (individuTestIsExist != null) {
            return individuTestIsExist;
        } else if (!individu.getName().isEmpty() && !individu.getFirstName().isEmpty() && individu.getDateOfBirth() != null && !individu.getSex().isEmpty()) {
            save(individu, force);
        }
        return individu;
    }

    public Individu createFromSources(String numEtu, String force) throws AgapeJpaException {
        Individu individuFromSources = null;
        for (IndividuSourceService individuSourceService : individuSourceServices) {
            individuFromSources = individuSourceService.getIndividuByNumEtu(numEtu);
            if (individuFromSources != null) {
                break;
            }
        }
        if (individuFromSources != null) {
            save(individuFromSources, force);
        }
        return individuFromSources;
    }

    public Individu createFromSources(String name, String firstName, LocalDate dateOfBirth, String force) throws AgapeJpaException {
        Individu individuFromSources = null;
        for (IndividuSourceService individuSourceService : individuSourceServices) {
            individuFromSources = individuSourceService.getIndividuByProperties(name, firstName, dateOfBirth);
            if (individuFromSources != null) {
                break;
            }
        }
        if (individuFromSources != null) {
            save(individuFromSources, force);
        }
        return individuFromSources;
    }

    public Individu getById(Long id) {
        return individuRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void deleteIndividu(long id) {
        Individu individu = getById(id);
        if (individu.getNumEtu() != null && !individu.getNumEtu().isEmpty()) {
            ExcludeIndividu excludeIndividu = new ExcludeIndividu();
            excludeIndividu.setNumEtuHash(new DigestUtils("SHA3-256").digestAsHex(individu.getNumEtu()));
            excludeIndividuRepository.save(excludeIndividu);
        }
        this.individuRepository.delete(individu);
    }

    public ResponseEntity<byte[]> getPhoto(Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_OCTET_STREAM));
        ResponseEntity<byte[]> httpResponse = setNoPhoto();
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(1000);
        clientHttpRequestFactory.setReadTimeout(1000);
        RestTemplate template = new RestTemplate(clientHttpRequestFactory);
        MultiValueMap<String, Object> multipartMap = new LinkedMultiValueMap<>();
        HttpEntity<Object> request = new HttpEntity<>(multipartMap, headers);
        Individu individu = getById(id);
        if(!applicationProperties.getDisplayPhotoUriPattern().isEmpty() && individu != null && individu.getPhotoId() != null && !individu.getPhotoId().isEmpty()) {
            String uri = MessageFormat.format(applicationProperties.getDisplayPhotoUriPattern(), individu.getPhotoId());
            try {
                httpResponse = template.exchange(uri, HttpMethod.GET, request, byte[].class);
            } catch (RestClientException e) {
                logger.debug("photo not found for " + id);
            }
        }
        return httpResponse;
    }

    private ResponseEntity<byte[]> setNoPhoto() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_OCTET_STREAM));
            ClassPathResource noImg = new ClassPathResource("/static/images/NoPhoto.jpg");
            return new ResponseEntity<>(noImg.getInputStream().readAllBytes(), headers, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("NoPhoto.jpg not found", e);
        }
        return null;
    }

    public int computeAge(Individu individu) {
        Period agePeriod = Period.between(individu.getDateOfBirth(), LocalDate.now());
        return agePeriod.getYears();
    }
}
