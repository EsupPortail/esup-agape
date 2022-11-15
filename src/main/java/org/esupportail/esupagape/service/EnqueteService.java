package org.esupportail.esupagape.service;

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

    public EnqueteService(EnqueteRepository enqueteRepository, DossierService dossierService) {
        this.enqueteRepository = enqueteRepository;
        this.dossierService = dossierService;
    }


   /* @Transactional
    public List<Enquete> findEntretiensByDossierId(Long dossierId) {
        List<Enquete> enquetes = enqueteRepository.findEnquetesByDossierId(dossierId);
        return enquetes;
    } */

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
        enqueteToUpdate.setCivilite(enquete.getCivilite());
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
        enqueteRepository.save(enquete);
    }

   /* @Transactional
    public void updateEnquete(Long id, Enquete enquete) throws AgapeJpaException {
        Enquete enqueteToUpdate = getById();
        enqueteToUpdate.setId(enqueteToUpdate.getId());
        enqueteToUpdate.setNfic(enquete.getNfic());
        enqueteToUpdate.setNumetu(enquete.getNumetu());
        enqueteToUpdate.setAn(enqueteToUpdate.getAn());
        enqueteToUpdate.setCivilite(enquete.getCivilite());
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
        enqueteRepository.save(enquete);
    }*/
}
