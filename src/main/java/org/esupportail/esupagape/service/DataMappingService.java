package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.DataMapping;
import org.esupportail.esupagape.entity.enums.DataType;
import org.esupportail.esupagape.repository.DataMappingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DataMappingService {

    private final DataMappingRepository dataMappingRepository;


    public DataMappingService(DataMappingRepository dataMappingRepository) {
        this.dataMappingRepository = dataMappingRepository;
    }

    public String getValue(String entityName, String attributName, DataType sourceType, DataType destinationType, String sourceValue) {
        Optional<DataMapping> dataMapping = dataMappingRepository.findByEntityNameAndAttributNameAndSourceTypeAndDestinationTypeAndSourceValueEquals(entityName, attributName, sourceType, destinationType, sourceValue);
        if(dataMapping.isPresent()) {
            return dataMapping.get().getDestinationValue();
        } else {
            return "no value found";
        }
    }

    public List<DataMapping> getValues(String entityName, String attributName, DataType sourceType, DataType destinationType, String sourceValue) {
        return dataMappingRepository.findByEntityNameAndAttributNameAndSourceTypeAndDestinationTypeAndSourceValueLike(entityName, attributName, sourceType, destinationType, sourceValue);
    }

}
