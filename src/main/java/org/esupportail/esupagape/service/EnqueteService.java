package org.esupportail.esupagape.service;

import org.esupportail.esupagape.dtos.EnqueteForm;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Enquete;
import org.esupportail.esupagape.entity.enums.enquete.CodAmL;
import org.esupportail.esupagape.entity.enums.enquete.CodMeae;
import org.esupportail.esupagape.entity.enums.enquete.CodMeahF;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.repository.EnqueteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class EnqueteService {

    private final EnqueteRepository enqueteRepository;

    private final DossierService dossierService;

    private final AmenagementService amenagementService;

    public EnqueteService(EnqueteRepository enqueteRepository, DossierService dossierService, AmenagementService amenagementService) {
        this.enqueteRepository = enqueteRepository;
        this.dossierService = dossierService;
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
        return enquete;
    }
}
