package org.esupportail.esupagape.service.interfaces.recupresultats;

import org.esupportail.esupagape.entity.Individu;

import java.util.List;
import java.util.Map;

public interface RecupResultatsService {

    List<Map<String, String>> getDossierProperties(Individu individu, Integer annee, boolean getAllSteps);

}
