package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.Entretien;
import org.esupportail.esupagape.entity.enums.StatusDossier;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.exception.AgapeYearException;
import org.esupportail.esupagape.repository.DocumentRepository;
import org.esupportail.esupagape.repository.EntretienRepository;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class EntretienService {

    private final EntretienRepository entretienRepository;

    private final DossierService dossierService;

    private final DocumentRepository documentRepository;

    private final DocumentService documentService;

    private final UtilsService utilsService;

    public EntretienService(EntretienRepository entretienRepository, DossierService dossierService, DocumentRepository documentRepository, DocumentService documentService, UtilsService utilsService) {
        this.entretienRepository = entretienRepository;
        this.dossierService = dossierService;
        this.documentRepository = documentRepository;
        this.documentService = documentService;
        this.utilsService = utilsService;
    }

    @Transactional
    public void save(Entretien entretien) {
        Dossier dossier = dossierService.getById(entretien.getDossier().getId());
        if (dossier.getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        if (dossier.getStatusDossier().equals(StatusDossier.IMPORTE) || dossier.getStatusDossier().equals(StatusDossier.AJOUT_MANUEL)) {
            dossier.setStatusDossier(StatusDossier.ACCUEILLI);
        }
        entretienRepository.save(entretien);
    }

    @Transactional
    public void create(Entretien entretien, Long idDossier, PersonLdap personLdap) {
        Dossier dossier = dossierService.getById(idDossier);
        if (dossier.getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        entretien.setDossier(dossier);
        entretien.setInterlocuteur(personLdap.getDisplayName());
        entretienRepository.save(entretien);
        dossier.setStatusDossier(StatusDossier.ACCUEILLI);
    }

    public Entretien getById(Long id) throws AgapeJpaException {
        Optional<Entretien> optionalEntretien = entretienRepository.findById(id);
        if (optionalEntretien.isPresent()) {
            return optionalEntretien.get();
        } else {
            throw new AgapeJpaException("Je n'ai pas trouvé cet entretien");
        }
    }

    @Transactional
    public void deleteEntretien(Long id) {
        Entretien entretien = getById(id);
        if (entretien.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        entretienRepository.deleteById(id);
    }

    @Transactional
    public void update(Long entretienId, Entretien entretien, PersonLdap personLdap) throws AgapeJpaException {
        Entretien entretienToUpdate = getById(entretienId);
        if (entretienToUpdate.getDossier().getYear() != utilsService.getCurrentYear()) {
            throw new AgapeYearException();
        }
        entretienToUpdate.setDate(entretien.getDate());
        entretienToUpdate.setTypeContact(entretien.getTypeContact());
        entretienToUpdate.setInterlocuteur(personLdap.getDisplayName());
        entretienToUpdate.setCompteRendu(entretien.getCompteRendu());
        entretienRepository.save(entretienToUpdate);
    }

    public Page<Entretien> findByDossier(Long dossierId, Pageable pageable) {
        return entretienRepository.findEntretiensByDossierId(dossierId, pageable);
    }

}
