package org.esupportail.esupagape.service.interfaces.importIndividu;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IndividuSourceService {

    public Map<String, String> getSourcesPropertiesMapping();
    public Map<String, String> getIndividuProperties(String numEtu);
    public Map<String, String> getIndividuProperties(String name, String firstname, LocalDateTime dateOfBirth);
    public List<String> getAllIndividuNums();

}
