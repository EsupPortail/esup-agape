package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.BigFile;
import org.esupportail.esupagape.repository.BigFileRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;

@Service
public class BigFileService {

	@Resource
	private BigFileRepository bigFileRepository;

	public void setBinaryFileStream(BigFile bigFile, InputStream inputStream, long length) {
		bigFileRepository.addBinaryFileStream(bigFile, inputStream, length);
	}

	public void delete(long id) {
		bigFileRepository.deleteById(id);
	}

}
