package org.esupportail.esupagape.service.interfaces.dossierinfos;

import org.esupportail.esupagape.entity.Individu;
import org.esupportail.esupagape.exception.AgapeException;

import java.sql.SQLException;
import java.util.Map;

public interface DossierInfosService {

    DossierInfos getDossierProperties(Individu individu, Integer annee, boolean getAllSteps, boolean getNotes, DossierInfos dossierInfos);
    Map<String, String> getCodComposanteLabels() throws AgapeException, SQLException;
}
