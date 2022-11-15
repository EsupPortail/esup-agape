package org.esupportail.esupagape.service.interfaces.dossierinfos;

import org.esupportail.esupagape.entity.Individu;

import java.util.List;

public interface DossierInfosService {

    List<DossierInfos> getDossierProperties(Individu individu, Integer annee, boolean getAllSteps);

}
