package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.Amenagement;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.entity.DossierAmenagement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DossierAmenagementRepository extends JpaRepository <DossierAmenagement, Long> {

    List<DossierAmenagement> findDossierAmenagementByDossier(Dossier dossier);
    List<DossierAmenagement> findDossierAmenagementByAmenagement(Amenagement amenagement);
    List<DossierAmenagement> findDossierAmenagementByLastYear(int year);
    List<DossierAmenagement> findDossierAmenagementByDossierAndAmenagement(Dossier dossier, Amenagement amenagement);

}
