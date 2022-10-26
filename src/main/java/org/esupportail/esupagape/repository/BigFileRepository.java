package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.BigFile;
import org.esupportail.esupagape.repository.custom.BigFileRepositoryCustom;
import org.springframework.data.repository.CrudRepository;

public interface BigFileRepository extends CrudRepository<BigFile, Long>, BigFileRepositoryCustom {

}
