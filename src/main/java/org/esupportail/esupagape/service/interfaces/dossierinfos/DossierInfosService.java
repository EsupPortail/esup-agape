package org.esupportail.esupagape.service.interfaces.dossierinfos;

import org.esupportail.esupagape.entity.Individu;

import java.util.List;
import java.util.Map;

public interface DossierInfosService {

    List<Map<String, Object>> getDossierProperties(Individu individu, Integer annee, boolean getAllSteps);

}
