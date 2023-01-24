package org.esupportail.esupagape.service;

import org.esupportail.esupagape.dtos.EnqueteForm;
import org.esupportail.esupagape.dtos.SlimSelectDto;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Enquete;
import org.esupportail.esupagape.entity.enums.Classification;
import org.esupportail.esupagape.entity.enums.enquete.CodAmL;
import org.esupportail.esupagape.entity.enums.enquete.CodHd;
import org.esupportail.esupagape.entity.enums.enquete.CodMeae;
import org.esupportail.esupagape.entity.enums.enquete.CodMeahF;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.repository.EnqueteEnumFilFmtScoLibelleRepository;
import org.esupportail.esupagape.repository.EnqueteEnumFilFmtScoRepository;
import org.esupportail.esupagape.repository.EnqueteRepository;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class EnqueteService {

    private final EnqueteRepository enqueteRepository;

    private final EnqueteEnumFilFmtScoRepository enqueteEnumFilFmtScoRepository;

    private final EnqueteEnumFilFmtScoLibelleRepository enqueteEnumFilFmtScoLibelleRepository;

    private final DossierService dossierService;

    private final MessageSource messageSource;

    private final AmenagementService amenagementService;

    public EnqueteService(EnqueteRepository enqueteRepository, EnqueteEnumFilFmtScoRepository enqueteEnumFilFmtScoRepositoryRepository, EnqueteEnumFilFmtScoLibelleRepository enqueteEnumFilFmtScoLibelleRepository, DossierService dossierService, MessageSource messageSource, AmenagementService amenagementService) {
        this.enqueteRepository = enqueteRepository;
        this.enqueteEnumFilFmtScoRepository = enqueteEnumFilFmtScoRepositoryRepository;
        this.enqueteEnumFilFmtScoLibelleRepository = enqueteEnumFilFmtScoLibelleRepository;
        this.dossierService = dossierService;
        this.messageSource = messageSource;
        this.amenagementService = amenagementService;
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
    public void create(Enquete enquete) {
        enqueteRepository.save(enquete);
    }

    @Transactional
    public void update(Long id, EnqueteForm enqueteForm, Dossier dossier) throws AgapeJpaException {
        Enquete enqueteToUpdate = getById(id);
        enqueteToUpdate.setNfic(enqueteForm.getNfic());
        enqueteToUpdate.setNumetu(enqueteForm.getNumetu());
        enqueteToUpdate.setGender(enqueteForm.getGender());
        enqueteToUpdate.setTypeFrmn(enqueteForm.getTypeFrmn());
        enqueteToUpdate.setModFrmn(enqueteForm.getModFrmn());
        enqueteToUpdate.setCodSco(enqueteForm.getCodSco());
        enqueteToUpdate.setCodFmt(enqueteForm.getCodFmt());
        enqueteToUpdate.setCodFil(enqueteForm.getCodFil());
        enqueteToUpdate.setCodHd(enqueteForm.getCodHd());
        enqueteToUpdate.setHdTmp(enqueteForm.getHdTmp());
        enqueteToUpdate.setCom(enqueteForm.getCom());
        if (enqueteForm.getCodPfpp() != null) {
            enqueteToUpdate.setCodPfpp(enqueteForm.getCodPfpp());
        }
        enqueteToUpdate.setCodPfas(enqueteForm.getCodPfas());
        enqueteToUpdate.getCodMeahF().clear();
        if (StringUtils.hasText(enqueteForm.getAHS0())) {
            enqueteToUpdate.getCodMeahF().add(CodMeahF.valueOf(enqueteForm.getAHS0()));
        } else {
            for(String AHS1 : enqueteForm.getAHS1()) {
                enqueteToUpdate.getCodMeahF().add(CodMeahF.valueOf(AHS1));
            }
            for(String AHS2 : enqueteForm.getAHS2()) {
                enqueteToUpdate.getCodMeahF().add(CodMeahF.valueOf(AHS2));
            }
            if (StringUtils.hasText(enqueteForm.getAHS3())) {
                enqueteToUpdate.getCodMeahF().add(CodMeahF.valueOf(enqueteForm.getAHS3()));
            }
            if (StringUtils.hasText(enqueteForm.getAHS4())) {
                if (enqueteForm.getAHS4().equals("on")) {
                    enqueteToUpdate.getCodMeahF().add(CodMeahF.AHS4);
                } else {
                    enqueteToUpdate.getCodMeahF().remove(CodMeahF.AHS4);
                }
            }
            if (StringUtils.hasText(enqueteForm.getAHS5())) {
                enqueteToUpdate.getCodMeahF().add(CodMeahF.valueOf(enqueteForm.getAHS5()));
            }
        }

        enqueteToUpdate.setInterpH(enqueteForm.getInterpH());
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
//        enqueteToUpdate.setCodAmL(enqueteForm.getCodAmL());
        // enqueteToUpdate.setCodMeahF(enqueteForm.getCodMeahF());
        enqueteToUpdate.setCodeurH(enqueteForm.getCodeurH());
        enqueteToUpdate.setAidHNat(enqueteForm.getAidHNat());
        enqueteToUpdate.setCodMeae(enqueteForm.getCodMeae());
        enqueteToUpdate.setAutAE(enqueteForm.getAutAE());
        enqueteToUpdate.setCodMeaa(enqueteForm.getCodMeaa());
        enqueteToUpdate.setAutAA(enqueteForm.getAutAA());
        enqueteToUpdate.setDjaCop(enqueteForm.getDjaCop());
        enqueteToUpdate.setNewNum(enqueteForm.getNewNum());
        enqueteToUpdate.setNewId(enqueteForm.getNewId());
        enqueteToUpdate.setDossier(dossier);
    }

    private Enquete createByDossierId(Long id) {
        Enquete enquete = new Enquete();
        Dossier dossier = dossierService.getById(id);
        enquete.setDossier(dossier);
        return enqueteRepository.save(enquete);
    }

    @Transactional
    public Enquete getAndUpdateByDossierId(Long id) {
        Dossier dossier = dossierService.getById(id);
        Enquete enquete = enqueteRepository.findByDossierId(id).orElseGet(() -> createByDossierId(id));
        enquete.setAn(String.valueOf(dossier.getYear()));
        enquete.setGender(dossier.getIndividu().getGender());
        enquete.setTypeFrmn(dossier.getTypeFormation());
        enquete.setModFrmn(dossier.getModeFormation());
        if (amenagementService.isAmenagementValid(id)) {
            enquete.getCodMeae().add(CodMeae.AE4);
        } else {
            enquete.getCodMeae().add(CodMeae.AE0);
        }

        enquete.setHdTmp(false);
        enquete.setCodHd(null);
        if(dossier.getClassification().size() > 2) {
            enquete.setCodHd(CodHd.PTA);
            if(dossier.getClassification().contains(Classification.TEMPORAIRE)) {
                enquete.setHdTmp(true);
            }
        } else if(dossier.getClassification().size() == 2 && dossier.getClassification().contains(Classification.TEMPORAIRE)) {
            for(Classification classification : dossier.getClassification()) {
                if(classification.equals(Classification.TEMPORAIRE)) {
                    enquete.setHdTmp(true);
                } else {
                    enquete.setCodHd(getClassificationEnqueteMap().get(classification));
                }
            }
        } else if (dossier.getClassification().size() == 2) {
            enquete.setCodHd(CodHd.PTA);
        } else if(dossier.getClassification().size() == 1) {
            if(dossier.getClassification().stream().toList().get(0).equals(Classification.TEMPORAIRE)) {
                enquete.setHdTmp(true);
            } else {
                enquete.setCodHd(getClassificationEnqueteMap().get(dossier.getClassification().stream().toList().get(0)));
            }
        }

        return enquete;
    }

    public void deleteByIndividu(long id) {
        List<Dossier> dossiers = dossierService.getAllByIndividu(id);
        for(Dossier dossier : dossiers) {
            Enquete enquete = enqueteRepository.findByDossierId(dossier.getId()).orElse(null);
            if(enquete != null) {
                enqueteRepository.delete(enquete);
            }
        }
    }

    public Map<Classification, CodHd> getClassificationEnqueteMap() {
        Map< Classification, CodHd> classificationMap = new HashMap<>();
        classificationMap.put(Classification.SURDITE_SEVERE_ET_PROFONDE, CodHd.AUD);
        classificationMap.put(Classification.MOTEUR, CodHd.MOT);
        classificationMap.put(Classification.CECITE, CodHd.VUE);
        classificationMap.put(Classification.DEFICIENCE_AUDTIVE_AUTRE, CodHd.AUA);
        classificationMap.put(Classification.DEFICIENCE_VISUELLE_AUTRE, CodHd.VUA);
        classificationMap.put(Classification.TROUBLES_VISCERAUX, CodHd.VIS);
        classificationMap.put(Classification.TROUBLES_VISCERAUX_CANCER, CodHd.VIS0);
        classificationMap.put(Classification.TROUBLE_DU_LANGAGE_ET_DE_LA_PAROLE, CodHd.LNG);
        classificationMap.put(Classification.AUTISME, CodHd.TSA);
        classificationMap.put(Classification.NON_COMMUNIQUE, CodHd.TND);
        classificationMap.put(Classification.REFUS, CodHd.TND);
        classificationMap.put(Classification.AUTRES_TROUBLES, CodHd.AUT);
        classificationMap.put(Classification.TROUBLES_INTELLECTUELS_ET_COGNITIFS, CodHd.COG);
        classificationMap.put(Classification.TROUBLES_PSYCHIQUES, CodHd.PSY);
        return classificationMap;
    }

    public List<String> getCodFils() {
        return enqueteEnumFilFmtScoRepository.findDistinctByCodScoIsNull();
    }

    public List<String> getCodFmtByCodFil(String codFil) {
        return enqueteEnumFilFmtScoRepository.findDistinctByCodFil(codFil);
    }

    public List<String> getCodScoByCodFmt(String codFmt) {
        return enqueteEnumFilFmtScoRepository.findDistinctByCodFmt(codFmt);
    }

    public List<SlimSelectDto> getSlimSelectDtosOfCodFmts(String codFil) {
        List<String> codFmts = getCodFmtByCodFil(codFil);
        List<SlimSelectDto> slimSelectDtos = new ArrayList<>();
        if(codFmts.size() > 0) {
            slimSelectDtos.add(new SlimSelectDto("", ""));
            for (String codFmt : codFmts) {
                slimSelectDtos.add(new SlimSelectDto(enqueteEnumFilFmtScoLibelleRepository.findByCod("FMT" + codFmt), codFmt));
            }
        }
        return slimSelectDtos;
    }

    public List<SlimSelectDto> getSlimSelectDtosOfCodScos(String codFmt) {
        List<String> codScos = getCodScoByCodFmt(codFmt);
        List<SlimSelectDto> slimSelectDtos = new ArrayList<>();
        if(codScos.size() > 0) {
            slimSelectDtos.add(new SlimSelectDto("", ""));
            for (String codSco : codScos) {
                slimSelectDtos.add(new SlimSelectDto(enqueteEnumFilFmtScoLibelleRepository.findByCod("SCO" + codSco), codSco));
            }
        }
        return slimSelectDtos;
    }


}
