package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Enquete;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.repository.EnqueteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void save(Long id, Enquete enquete) throws AgapeJpaException {
        Enquete enqueteToUpdate = getById(id);
        enqueteRepository.save(enquete);
        enqueteToUpdate.setId(enqueteToUpdate.getId());
        enqueteToUpdate.setNfic(enquete.getNfic());
        enqueteToUpdate.setNumetu(enquete.getNumetu());
        enqueteToUpdate.setAn(enqueteToUpdate.getAn());
        enqueteToUpdate.setGender(enquete.getGender());
        enqueteToUpdate.setTypeFrmn(enquete.getTypeFrmn());
        enqueteToUpdate.setModFrmn(enquete.getModFrmn());
        enqueteToUpdate.setCodSco(enquete.getCodSco());
        enqueteToUpdate.setCodFmt(enquete.getCodFmt());
        enqueteToUpdate.setCodFil(enquete.getCodFil());
        enqueteToUpdate.setCodHd(enquete.getCodHd());
        enqueteToUpdate.setHdTmp(enquete.getHdTmp());
        enqueteToUpdate.setCom(enquete.getCom());
        enqueteToUpdate.setCodPfpp(enquete.getCodPfpp());
        enqueteToUpdate.setCodPfas(enquete.getCodPfas());
        enqueteToUpdate.setCodMeahF(enquete.getCodMeahF());
        enqueteToUpdate.setInterpH(enquete.getInterpH());
        enqueteToUpdate.setCodAmL(enquete.getCodAmL());
        enqueteToUpdate.setCodMeahF(enquete.getCodMeahF());
        enqueteToUpdate.setCodeurH(enquete.getCodeurH());
        enqueteToUpdate.setAidHNat(enquete.getAidHNat());
        enqueteToUpdate.setCodMeae(enquete.getCodMeae());
        enqueteToUpdate.setAutAE(enquete.getAutAE());
        enqueteToUpdate.setCodMeaa(enquete.getCodMeaa());
        enqueteToUpdate.setAutAA(enquete.getAutAA());
        enqueteToUpdate.setCodAmL(enquete.getCodAmL());
        enqueteToUpdate.setDjaCop(enquete.getDjaCop());
        enqueteToUpdate.setNewNum(enquete.getNewNum());
        enqueteToUpdate.setNewId(enquete.getNewId());
        enqueteToUpdate.setDossier(enquete.getDossier());
        enqueteRepository.save(enquete);
    }

    private Enquete createByDossierId(Long id) {
        Enquete enquete = new Enquete();
        Dossier dossier = dossierService.getById(id);
        enquete.setDossier(dossier);
        return enqueteRepository.save(enquete);
    }

    @Transactional
    public Enquete findByDossierId(Long id) {
        return enqueteRepository.findByDossierId(id).orElseGet(() -> createByDossierId(id));
    }

    @Transactional
    public Enquete getAndUpdateByDossierId(Long id) {
        Dossier dossier = dossierService.getById(id);
        Enquete enquete = enqueteRepository.findByDossierId(id).orElseGet(() -> createByDossierId(id));
        enquete.setGender(dossier.getIndividu().getGender());
        enquete.setTypeFrmn(dossier.getTypeFormation());
        enquete.setModFrmn(dossier.getModeFormation());
       


        return enquete;
    }
}
