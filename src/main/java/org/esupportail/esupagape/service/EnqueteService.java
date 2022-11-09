package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.Enquete;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.repository.EnqueteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EnqueteService {

    private final EnqueteRepository enqueteRepository;

    private final DossierService dossierService;

    public EnqueteService(EnqueteRepository enqueteRepository, DossierService dossierService) {
        this.enqueteRepository = enqueteRepository;
        this.dossierService = dossierService;
    }


    @Transactional
    public List<Enquete> findEntretiensByDossierId(Long dossierId) {
        List<Enquete> enquetes = enqueteRepository.findEnquetesByDossierId(dossierId);
        return enquetes;
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
    public void save(Enquete enquete) {
        enqueteRepository.save(enquete);
    }

    @Transactional
    public void updateEnquete(Long id, Enquete enquete) throws AgapeJpaException {
        Enquete enqueteToUpdate = getById(id);
        enqueteToUpdate.setId(enqueteToUpdate.getId());
        enqueteToUpdate.setNfic(enquete.getNfic());
        enqueteToUpdate.setNumetu(enquete.getNumetu());
        enqueteToUpdate.setAn(enqueteToUpdate.getAn());
        enqueteToUpdate.setCivilite(enquete.getCivilite());
        enqueteToUpdate.setTypefrmn(enquete.getTypefrmn());
        enqueteToUpdate.setModfrmn(enquete.getModfrmn());
        enqueteToUpdate.setCodsco(enquete.getCodsco());
        enqueteToUpdate.setCodfmt(enquete.getCodfmt());
        enqueteToUpdate.setCodfil(enquete.getCodfil());
        enqueteToUpdate.setCodhd(enquete.getCodhd());
        enqueteToUpdate.setHdtmp(enquete.isHdtmp());
        enqueteToUpdate.setCom(enquete.getCom());
        enqueteToUpdate.setCodpfpp(enquete.getCodpfpp());
        enqueteToUpdate.setCodpfas(enquete.getCodpfas());
        enqueteToUpdate.setCodmeahF(enquete.getCodmeahF());
        enqueteToUpdate.setInterpH(enquete.getInterpH());
        enqueteToUpdate.setCodamL(enquete.getCodamL());
        enqueteToUpdate.setCodmeahF(enquete.getCodmeahF());
        enqueteToUpdate.setCodeurH(enquete.getCodeurH());
        enqueteToUpdate.setAidhnat(enquete.getAidhnat());
        enqueteToUpdate.setCodmeae(enquete.getCodmeae());
        enqueteToUpdate.setAutae(enquete.getAutae());
        enqueteToUpdate.setCodmeaa(enquete.getCodmeaa());
        enqueteToUpdate.setAutaa(enquete.getAutaa());
        enqueteToUpdate.setCodamL(enquete.getCodamL());
        enqueteToUpdate.setDjaCop(enquete.getDjaCop());
        enqueteToUpdate.setNewnum(enquete.getNewnum());
        enqueteToUpdate.setNewid(enquete.getNewid());
    }
}
