package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.AideMaterielle;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.repository.AideMaterielleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AideMaterielleService {


    private final AideMaterielleRepository aideMaterielleRepository;

    public AideMaterielleService(AideMaterielleRepository aideMaterielleRepository) {
        this.aideMaterielleRepository = aideMaterielleRepository;
    }

    public AideMaterielle getById(Long id) throws AgapeJpaException {
        Optional<AideMaterielle> optionalAideMaterielle = aideMaterielleRepository.findById(id);
        if (optionalAideMaterielle.isPresent()) {
            return optionalAideMaterielle.get();
        } else {
            throw new AgapeJpaException("Je n'ai pas trouvé cette aide");
        }
    }

    @Transactional
    public void create(AideMaterielle aideMaterielle) {
        aideMaterielleRepository.save(aideMaterielle);
    }

    public Page<AideMaterielle> findByDossier(Dossier dossier) {
        return aideMaterielleRepository.findByDossierId(dossier.getId(), Pageable.unpaged());
    }

    @Transactional
    public void delete(Long aideMaterielleId) {
        aideMaterielleRepository.deleteById(aideMaterielleId);
    }
}
