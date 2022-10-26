package org.esupportail.esupagape.repository.impl;

import org.esupportail.esupagape.entity.BigFile;
import org.esupportail.esupagape.repository.custom.BigFileRepositoryCustom;
import org.hibernate.Hibernate;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.InputStream;
import java.sql.Blob;

@Repository
public class BigFileRepositoryImpl implements BigFileRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public void addBinaryFileStream(BigFile bigFile, InputStream inputStream, long length) {
		Blob blob = Hibernate.getLobCreator(entityManager.unwrap(SessionImplementor.class)).createBlob(inputStream, length);
        bigFile.setBinaryFile(blob);
        entityManager.persist(bigFile);
	}

}
