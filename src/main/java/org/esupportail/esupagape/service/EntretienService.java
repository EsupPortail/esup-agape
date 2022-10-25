package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.Entretien;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.repository.EntretienRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class EntretienService {

    @Resource
    private EntretienRepository entretienRepository;

    public List<Entretien> getAllEntretiens() {
        return entretienRepository.findAll();
    }

    @Transactional
    public List<Entretien> getEntretiensByDossier(Long dossierId) {
        return entretienRepository.findEntretienByDossierId(dossierId);
    }

    @Transactional
    public void save(Entretien entretien) {
        entretienRepository.save(entretien);
    }

    public Entretien findById(Long id) throws AgapeException {
        Optional<Entretien> optionalEntretien = entretienRepository.findById(id);
        if (optionalEntretien.isPresent()) {
            return optionalEntretien.get();
        } else {
            throw  new AgapeException("Je n'ai pas trouvé cette entretien");
        }
    }

    @Transactional
    public void deleteEntretien(long id) {
        this.entretienRepository.deleteById(id);
    }

}
