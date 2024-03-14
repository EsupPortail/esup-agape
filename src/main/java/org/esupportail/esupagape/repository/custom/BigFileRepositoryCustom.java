package org.esupportail.esupagape.repository.custom;

import org.esupportail.esupagape.entity.BigFile;

import java.io.InputStream;

public interface BigFileRepositoryCustom {

	void addBinaryFileStream(BigFile bigFile, InputStream inputStream, long length);

}
