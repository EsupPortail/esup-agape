package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.DataMapping;
import org.esupportail.esupagape.entity.enums.DataType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DataMappingRepository extends JpaRepository<DataMapping, Long> {
    Optional<DataMapping> findBySourceTypeAndDestinationTypeAndSourceValueEquals(DataType sourceType, DataType destinationType, String sourceValue);

    Optional<DataMapping> findByEntityNameAndAttributNameAndSourceTypeAndDestinationTypeAndSourceValueEquals(String entityName, String attributName, DataType sourceType, DataType destinationType, String sourceValue);

    List<DataMapping> findByEntityNameAndAttributNameAndSourceTypeAndDestinationTypeAndSourceValueLike(String entityName, String attributName, DataType sourceType, DataType destinationType, String sourceValue);
}
