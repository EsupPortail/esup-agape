package org.esupportail.esupagape.service.interfaces.dossierinfos;

import org.esupportail.esupagape.entity.Individu;

public interface DossierInfosService {

    DossierInfos getDossierProperties(Individu individu, Integer annee, boolean getAllSteps, boolean getNotes, DossierInfos dossierInfos);

}
