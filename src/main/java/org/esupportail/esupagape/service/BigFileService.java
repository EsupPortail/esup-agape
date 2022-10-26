package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.BigFile;
import org.esupportail.esupagape.repository.BigFileRepository;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class BigFileService {

	private final BigFileRepository bigFileRepository;

	public BigFileService(BigFileRepository bigFileRepository) {
		this.bigFileRepository = bigFileRepository;
	}

	public void setBinaryFileStream(BigFile bigFile, InputStream inputStream, long length) {
		bigFileRepository.addBinaryFileStream(bigFile, inputStream, length);
	}

	public void delete(long id) {
		bigFileRepository.deleteById(id);
	}

}
