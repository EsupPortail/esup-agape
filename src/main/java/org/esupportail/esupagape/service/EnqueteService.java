package org.esupportail.esupagape.service;

import org.esupportail.esupagape.dtos.csvs.SiseDiplomeCsvDto;
import org.esupportail.esupagape.dtos.csvs.SiseSecteurDisciplinaireCsvDto;
import org.esupportail.esupagape.dtos.csvs.SiseTypeDiplomeCsvDto;
import org.esupportail.esupagape.dtos.forms.EnqueteForm;
import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Enquete;
import org.esupportail.esupagape.entity.EnqueteEnumFilFmtScoLibelle;
import org.esupportail.esupagape.entity.enums.*;
import org.esupportail.esupagape.entity.enums.enquete.*;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.exception.AgapeYearException;
import org.esupportail.esupagape.repository.EnqueteEnumFilFmtScoLibelleRepository;
import org.esupportail.esupagape.repository.EnqueteEnumFilFmtScoRepository;
import org.esupportail.esupagape.repository.EnqueteRepository;
import org.esupportail.esupagape.service.utils.SiseService;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.esupportail.esupagape.service.utils.slimselect.SlimSelectData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class EnqueteService {

    private static final Logger logger = LoggerFactory.getLogger(EnqueteService.class);


    private final EnqueteRepository enqueteRepository;
    private final EnqueteEnumFilFmtScoRepository enqueteEnumFilFmtScoRepository;
    private final EnqueteEnumFilFmtScoLibelleRepository enqueteEnumFilFmtScoLibelleRepository;
    private final DossierService dossierService;
    private final AmenagementService amenagementService;
    private final UtilsService utilsService;
    private final LogService logService;
    private final SiseService siseService;

    public EnqueteService(
            EnqueteRepository enqueteRepository,
            EnqueteEnumFilFmtScoRepository enqueteEnumFilFmtScoRepositoryRepository,
            EnqueteEnumFilFmtScoLibelleRepository enqueteEnumFilFmtScoLibelleRepository,
            DossierService dossierService,
            AmenagementService amenagementService,
            UtilsService utilsService, LogService logService, SiseService siseService) {
        this.enqueteRepository = enqueteRepository;
        this.enqueteEnumFilFmtScoRepository = enqueteEnumFilFmtScoRepositoryRepository;
        this.enqueteEnumFilFmtScoLibelleRepository = enqueteEnumFilFmtScoLibelleRepository;
        this.dossierService = dossierService;
        this.amenagementService = amenagementService;
        this.utilsService = utilsService;
        this.logService = logService;
        this.siseService = siseService;
    }

    public Enquete getById(Long id) throws AgapeJpaException {
        Optional<Enquete> optionalEnquete = enqueteRepository.findById(id);
        if (optionalEnquete.isPresent()) {
            return optionalEnquete.get();
        } else {
            throw new AgapeJpaException("Je n'ai pas trouvé cette enquête");
        }
    }

    @Transactional
    public void create(Enquete enquete, String eppn) {
        if(enquete.getDossier().getStatusDossier().equals(StatusDossier.AJOUT_MANUEL)
            ||
            enquete.getDossier().getStatusDossier().equals(StatusDossier.IMPORTE)) {
            dossierService.changeStatutDossier(enquete.getDossier().getId(), StatusDossier.SUIVI, eppn);

        }
        enqueteRepository.save(enquete);
    }

    @Transactional
    public void update(Long id, EnqueteForm enqueteForm, Long dossierId) throws AgapeJpaException {
        Enquete enqueteToUpdate = getById(id);
        if (enqueteToUpdate.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        enqueteToUpdate.setSexe(enqueteForm.getSexe());
        if (enqueteForm.getCodSco() != null) {
            enqueteToUpdate.setCodSco(enqueteForm.getCodSco());
        }
        if (enqueteForm.getCodFmt() != null) {
            enqueteToUpdate.setCodFmt(enqueteForm.getCodFmt());
        }
        if (enqueteForm.getAlternance() != null) {
            enqueteToUpdate.setAlternance(enqueteForm.getAlternance());
        }
        if (enqueteForm.getCodFil() != null) {
            enqueteToUpdate.setCodFil(enqueteForm.getCodFil());
        }
        if (enqueteForm.getCodHd() != null) {
            enqueteToUpdate.setCodHd(enqueteForm.getCodHd());
        }
        enqueteToUpdate.setHdTmp(enqueteForm.getHdTmp());
        enqueteToUpdate.setCom(enqueteForm.getCom());
        if (enqueteForm.getCodPfpp() != null) {
            enqueteToUpdate.setCodPfpp(enqueteForm.getCodPfpp());
        }
        enqueteToUpdate.getCodPfas().clear();
        enqueteToUpdate.getCodPfas().addAll(enqueteForm.getCodPfas());

        enqueteToUpdate.getCodMeahF().clear();
        if (!enqueteForm.getAHS1().isEmpty() || !enqueteForm.getAHS2().isEmpty()) {
            if (StringUtils.hasText(enqueteForm.getAHS1())) {
                if (enqueteForm.getAHS1().equals("on")) {
                    enqueteToUpdate.getCodMeahF().add(CodMeahF.AHS1);
                } else {
                    enqueteToUpdate.getCodMeahF().remove(CodMeahF.AHS1);
                }
            }
            if (StringUtils.hasText(enqueteForm.getAHS2())) {
                if (enqueteForm.getAHS2().equals("on")) {
                    enqueteToUpdate.getCodMeahF().add(CodMeahF.AHS2);
                } else {
                    enqueteToUpdate.getCodMeahF().remove(CodMeahF.AHS2);
                }
            }
            if (StringUtils.hasText(enqueteForm.getAHS3())) {
                if (enqueteForm.getAHS3().equals("on")) {
                    enqueteToUpdate.getCodMeahF().add(CodMeahF.AHS3);
                } else {
                    enqueteToUpdate.getCodMeahF().remove(CodMeahF.AHS3);
                }
            }
            if (StringUtils.hasText(enqueteForm.getAHS4())) {
                if (enqueteForm.getAHS4().equals("on")) {
                    enqueteToUpdate.getCodMeahF().add(CodMeahF.AHS4);
                } else {
                    enqueteToUpdate.getCodMeahF().remove(CodMeahF.AHS4);
                }
            }
            if (StringUtils.hasText(enqueteForm.getAHS5())) {
                if (enqueteForm.getAHS5().equals("on")) {
                    enqueteToUpdate.getCodMeahF().add(CodMeahF.AHS5);
                } else {
                    enqueteToUpdate.getCodMeahF().remove(CodMeahF.AHS5);
                }
            }
        }
        enqueteToUpdate.getCodAmL().clear();
        if (StringUtils.hasText(enqueteForm.getAM0())) {
            enqueteToUpdate.getCodAmL().add(CodAmL.valueOf(enqueteForm.getAM0()));
        } else {
            if (StringUtils.hasText(enqueteForm.getAM1())) {
                enqueteToUpdate.getCodAmL().add(CodAmL.valueOf(enqueteForm.getAM1()));
            }
            if (StringUtils.hasText(enqueteForm.getAM2())) {
                enqueteToUpdate.getCodAmL().add(CodAmL.valueOf(enqueteForm.getAM2()));
            }
            if (StringUtils.hasText(enqueteForm.getAM3())) {
                enqueteToUpdate.getCodAmL().add(CodAmL.valueOf(enqueteForm.getAM3()));
            }
            if (StringUtils.hasText(enqueteForm.getAM4())) {
                enqueteToUpdate.getCodAmL().add(CodAmL.valueOf(enqueteForm.getAM4()));
            }
            if (StringUtils.hasText(enqueteForm.getAM5())) {
                enqueteToUpdate.getCodAmL().add(CodAmL.valueOf(enqueteForm.getAM5()));
            }
            if (StringUtils.hasText(enqueteForm.getAM6())) {
                enqueteToUpdate.getCodAmL().add(CodAmL.valueOf(enqueteForm.getAM6()));
            }
            if (StringUtils.hasText(enqueteForm.getAM7())) {
                enqueteToUpdate.getCodAmL().add(CodAmL.valueOf(enqueteForm.getAM7()));
            }
            if (StringUtils.hasText(enqueteForm.getAM8())) {
                enqueteToUpdate.getCodAmL().add(CodAmL.valueOf(enqueteForm.getAM8()));
            }
        }
        enqueteToUpdate.setAidHNat(enqueteForm.getAidHNat());
        enqueteToUpdate.setCodMeae(enqueteForm.getCodMeae());

        enqueteToUpdate.setAutAE(enqueteForm.getAutAE());
        enqueteToUpdate.getCodMeaa().clear();
        enqueteToUpdate.getCodMeaa().addAll(enqueteForm.getCodMeaa());
        enqueteToUpdate.setAutAA(enqueteForm.getAutAA());
        enqueteToUpdate.setDossier(dossierService.getById(dossierId));
    }

    private Enquete createByDossierId(Long id, String eppn) {
        Dossier dossier = dossierService.getById(id);
        if(dossier.getType().equals(TypeIndividu.ETUDIANT)) {
            Enquete enquete = new Enquete();
            enquete.setDossier(dossier);
            logService.create(eppn, id, dossier.getStatusDossier().name(), "Création enquête");
            return enqueteRepository.save(enquete);
        }
        return null;
    }

    @Transactional
    public Enquete getAndUpdateByDossierId(Long id, String eppn) {
        Dossier dossier = dossierService.getById(id);
        Enquete enquete = enqueteRepository.findByDossierId(id).orElseGet(() -> createByDossierId(id, eppn));
        if(enquete == null) return null;
        if (dossier.getYear() == utilsService.getCurrentYear()) {
            enquete.setAn(String.valueOf(dossier.getIndividu().getDateOfBirth().getYear()));
            if (dossier.getIndividu().getGender() != null) {
                if (dossier.getIndividu().getGender().equals(Gender.FEMININ)) {
                    enquete.setSexe("0");
                } else if (dossier.getIndividu().getGender().equals(Gender.MASCULIN)) {
                    enquete.setSexe("1");
                } else if (dossier.getIndividu().getGender().equals(Gender.NE_SAIS_PAS)) {
                    enquete.setSexe("2");
                }
            } else {
                enquete.setSexe("2");
            }

            if (enquete.getCodMeahF().isEmpty()) {
                if (dossier.getAidesHumaines().stream().anyMatch(ah -> ah.getFonctionAidants().contains(FonctionAidant.PRENEUR_NOTES))) {
                    enquete.getCodMeahF().add(CodMeahF.AHS3);
                }
                if (dossier.getAidesHumaines().stream().anyMatch(ah -> ah.getFonctionAidants().contains(FonctionAidant.TUTEUR_PEDAGO) || ah.getFonctionAidants().contains(FonctionAidant.TUTEUR_ACC))) {
                    enquete.getCodMeahF().add(CodMeahF.AHS5);
                }
            }
            if (enquete.getTypFrmn() == null) {
                enquete.setTypFrmn(dossier.getTypeFormation());
            }
            if (dossier.getModeFormation() != null) {
                enquete.setModFrmn(dossier.getModeFormation());
            }
            if (dossier.getStatusDossier().equals(StatusDossier.SUIVI) || dossier.getStatusDossier().equals(StatusDossier.RECU_PAR_LA_MEDECINE_PREVENTIVE) || dossier.getStatusDossier().equals(StatusDossier.RECONDUIT)) {
                enquete.setCodPfpp(CodPfpp.MH1);
            } else {
                enquete.setCodPfpp(CodPfpp.PP0);
            }
            Amenagement amenagement = amenagementService.getCurrentAmenagement(id);
            if(amenagement != null) {
                if (amenagement.getAmenagementText().contains("Allègement du cursus")) {
                    enquete.getCodPfas().add(CodPfas.AS2);
                }
                if (amenagement.getAmenagementText().contains("Conservation et/ou report des notes")) {
                    enquete.getCodPfas().add(CodPfas.AS3);
                }
                if (amenagement.getAmenagementText().contains("Autorisation d’absences sans production de justificatifs")) {
                    enquete.getCodPfas().add(CodPfas.AS5);
                }
            }
            enquete.setAlternance(false);
            if (dossier.getAlternance() != null && dossier.getAlternance()) {
                enquete.setAlternance(true);
            }
            Boolean isAmenagementTempsMajore = amenagementService.isAmenagementTempsMajore(id);
            if (isAmenagementTempsMajore != null) {
                enquete.getCodMeae().add(CodMeae.AE4);
                if (isAmenagementTempsMajore) {
                    enquete.getCodMeae().add(CodMeae.AE7);
                }
            }
            if (StringUtils.hasText(enquete.getAutAE())) {
                enquete.getCodMeae().add(CodMeae.AEO);
            } else {
                enquete.getCodMeae().remove(CodMeae.AEO);
            }
            if (enquete.getCodMeae().isEmpty()) {
                enquete.getCodMeae().clear();
            }
            enquete.setHdTmp(false);
            enquete.setCodHd(null);
            if (dossier.getStatusDossier() != null && (dossier.getStatusDossier().equals(StatusDossier.SUIVI) || dossier.getStatusDossier().equals(StatusDossier.RECU_PAR_LA_MEDECINE_PREVENTIVE) || dossier.getStatusDossier().equals(StatusDossier.RECONDUIT))) {
                enquete.getCodMeaa().add(CodMeaa.AA1);
            } else {
                enquete.getCodMeaa().remove(CodMeaa.AA1);
            }
            if (dossier.getClassifications().size() > 2) {
                enquete.setCodHd(CodHd.PTA);
                if (dossier.getClassifications().contains(Classification.TEMPORAIRE)) {
                    enquete.setHdTmp(true);
                }
            } else if (dossier.getClassifications().size() == 2 && dossier.getClassifications().contains(Classification.TEMPORAIRE)) {
                for (Classification classification : dossier.getClassifications()) {
                    if (classification.equals(Classification.TEMPORAIRE)) {
                        enquete.setHdTmp(true);
                    } else {
                        enquete.setCodHd(getClassificationEnqueteMap().get(classification));
                    }
                }
            } else if (dossier.getClassifications().size() == 2) {
                enquete.setCodHd(CodHd.PTA);
            } else if (dossier.getClassifications().size() == 1) {
                if (dossier.getClassifications().stream().toList().get(0).equals(Classification.TEMPORAIRE)) {
                    enquete.setHdTmp(true);
                } else {
                    enquete.setCodHd(getClassificationEnqueteMap().get(dossier.getClassifications().stream().toList().get(0)));
                }
            }
        }
        if(StringUtils.hasText(dossier.getSecteurDisciplinaire())) {
            try {
                String codFil = SiseSecteurDisciplinaireCsvDto.getCodFil(siseService.getCodeSecteurDisciplinaire( dossier.getSecteurDisciplinaire()));
                if(StringUtils.hasText(codFil)) {
                    enquete.setCodFil(codFil);
                }
            } catch (Exception e) {
                logger.error("enquete codfil not found for " + dossier.getSecteurDisciplinaire());
            }
        }
        if(StringUtils.hasText(dossier.getTypeDiplome())) {
            try {
                String codFmt = SiseTypeDiplomeCsvDto.getCodFmt(siseService.getCodeTypeDiplome( dossier.getTypeDiplome()));
                if(StringUtils.hasText(codFmt)) {
                    enquete.setCodFmt(codFmt);
                }
            } catch (Exception e) {
                logger.error("enquete codFmt not found for " + dossier.getTypeDiplome());
            }
        }
        if(StringUtils.hasText(dossier.getNiveauEtudes())) {
            try {
                String codSco = SiseDiplomeCsvDto.getCodSco(dossier.getNiveauEtudes());
                if(StringUtils.hasText(codSco)) {
                    enquete.setCodSco(codSco);
                }
            } catch (Exception e) {
                logger.error("enquete codSco not found for " + dossier.getNiveauEtudes());
            }
        }
        if (!dossier.getMdphs().isEmpty()) {
            if (dossier.getMdphs().contains(Mdph.PCH_AIDE_HUMAINE) ||
                    dossier.getMdphs().contains(Mdph.PCH_AIDE_TECHNIQUE)) {
                enquete.getCodAmL().add(CodAmL.AM2);
            }

            if (dossier.getMdphs().contains(Mdph.TRANSPORT_INDIVIDUEL_ADAPTE)) {
                enquete.getCodAmL().add(CodAmL.AM3);
            }

            if (dossier.getMdphs().contains(Mdph.RQTH)) {
                enquete.getCodAmL().add(CodAmL.AM4);
            }

            if (dossier.getMdphs().contains(Mdph.AEEH)) {
                enquete.getCodAmL().add(CodAmL.AM5);
            }

            if (dossier.getMdphs().contains(Mdph.AAH) ||
                    dossier.getMdphs().contains(Mdph.CARTE_INVALIDITE) ||
                    dossier.getMdphs().contains(Mdph.CARTE_PRIORITE) ||
                    dossier.getMdphs().contains(Mdph.CARTE_INVALIDITE_PRIORITE)) {
                enquete.getCodAmL().add(CodAmL.AM8);
            }
        }
        return enquete;
    }

    public void detachAllByDossiers(long id) {
        List<Dossier> dossiers = dossierService.getAllByIndividu(id);
        for (Dossier dossier : dossiers) {
            Enquete enquete = enqueteRepository.findByDossierId(dossier.getId()).orElse(null);
            if (enquete != null) {
                enquete.setDossier(null);
            }
        }
    }

    public Map<Classification, CodHd> getClassificationEnqueteMap() {
        Map<Classification, CodHd> classificationMap = new HashMap<>();
        classificationMap.put(Classification.TROUBLES_DES_FONCTIONS_AUDITIVES, CodHd.AUD);
        classificationMap.put(Classification.MOTEUR, CodHd.MOT);
        classificationMap.put(Classification.TROUBLES_DES_FONCTIONS_VISUELLES, CodHd.VUE);
        classificationMap.put(Classification.TROUBLES_VISCERAUX, CodHd.VIS);
        classificationMap.put(Classification.TROUBLES_VISCERAUX_CANCER, CodHd.VIS0);
        classificationMap.put(Classification.TROUBLE_DU_LANGAGE_OU_DE_LA_PAROLE, CodHd.LNG);
        classificationMap.put(Classification.AUTISME, CodHd.TSA);
        classificationMap.put(Classification.NON_COMMUNIQUE, CodHd.TND);
        classificationMap.put(Classification.REFUS, CodHd.TND);
        classificationMap.put(Classification.AUTRES_TROUBLES, CodHd.AUT);
        classificationMap.put(Classification.TROUBLES_INTELLECTUELS_ET_COGNITIFS, CodHd.COG);
        classificationMap.put(Classification.TROUBLES_PSYCHIQUES, CodHd.PSY);
        return classificationMap;
    }

    public List<String> getCodFils() {
        return enqueteEnumFilFmtScoRepository.findCodFils();
    }

    public List<String> getCodFmts() {
        return enqueteEnumFilFmtScoRepository.findCodFmts();
    }

    public List<String> getCodScos() {
        return enqueteEnumFilFmtScoRepository.findCodScos();
    }

    public Map<String, String> getAllCodFmt() {
        Map<String, String> codFmts = new HashMap<>();
        List<EnqueteEnumFilFmtScoLibelle> enqueteEnumFilFmtScoLibelles = enqueteEnumFilFmtScoLibelleRepository.findAll();
        for (EnqueteEnumFilFmtScoLibelle enqueteEnumFilFmtScoLibelle : enqueteEnumFilFmtScoLibelles) {
            codFmts.put(enqueteEnumFilFmtScoLibelle.getCod().toLowerCase(), enqueteEnumFilFmtScoLibelle.getLibelle());
        }
        return codFmts;
    }

    public List<SlimSelectData> getSlimSelectDtosOfCodFmts() {
        List<String> codFmts = getCodFmts();
        List<SlimSelectData> slimSelectDtos = new ArrayList<>();
        if (!codFmts.isEmpty()) {
            slimSelectDtos.add(new SlimSelectData("", ""));
            for (String codFmt : codFmts) {
                slimSelectDtos.add(new SlimSelectData(enqueteEnumFilFmtScoLibelleRepository.findByCod("FMT" + codFmt), codFmt));
            }
        }
        return slimSelectDtos;
    }

    public List<SlimSelectData> getSlimSelectDtosOfCodScos() {
        List<String> codScos = getCodScos();
        List<SlimSelectData> slimSelectDtos = new ArrayList<>();
        if (!codScos.isEmpty()) {
            slimSelectDtos.add(new SlimSelectData("", ""));
            for (String codSco : codScos) {
                SlimSelectData slimSelectDto = new SlimSelectData(enqueteEnumFilFmtScoLibelleRepository.findByCod("SCO" + codSco), codSco);
                if (slimSelectDto.getValue() != null) {
                    slimSelectDtos.add(slimSelectDto);
                }
            }
        }
        return slimSelectDtos;
    }

    public Enquete findByDossierId(Long dossierId) {
        return enqueteRepository.findByDossierId(dossierId).get();
    }

    @Transactional
    public void finished(Long enqueteId) {
        Enquete enquete = getById(enqueteId);
        if (enquete.getFinished() != null) {
            enquete.setFinished(!enquete.getFinished());
        } else {
            enquete.setFinished(true);
        }
    }

    public List<Enquete> findAllByDossierYear(int year) {
        return enqueteRepository.findAllByDossierYear(year);

    }
}
