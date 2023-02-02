package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.BigFile;
import org.esupportail.esupagape.entity.Document;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.esupportail.esupagape.exception.AgapeYearException;
import org.esupportail.esupagape.repository.DocumentRepository;
import org.esupportail.esupagape.service.utils.UtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Service
public class DocumentService {

	private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

	private final DocumentRepository documentRepository;

	private final BigFileService bigFileService;

	private final UtilsService utilsService;

	public DocumentService(DocumentRepository documentRepository, BigFileService bigFileService, UtilsService utilsService) {
		this.documentRepository = documentRepository;
		this.bigFileService = bigFileService;
		this.utilsService = utilsService;
	}

	@Transactional
	public Document createDocument(InputStream inputStream, String name, String contentType, Long parentId, String parentType, Dossier dossier) throws AgapeIOException {
		if(dossier.getYear() != utilsService.getCurrentYear()) {
			throw new AgapeYearException();
		}
		Document document = new Document();
		document.setCreateDate(new Date());
		document.setFileName(name);
		document.setContentType(contentType);
		document.setParentId(parentId);
		document.setParentType(parentType);
		document.setDossier(dossier);
		BigFile bigFile = new BigFile();
		long size = 0;
		try {
			size = inputStream.available();
			if(size == 0) {
				logger.warn("upload aborted cause file size is 0");
				throw new AgapeIOException("File size is 0");
			}
		} catch (IOException e) {
			throw new AgapeIOException(e.getMessage(), e);
		}

		bigFileService.setBinaryFileStream(bigFile, inputStream, size);
		document.setBigFile(bigFile);
		document.setSize(size);
		documentRepository.save(document);
		return document;
	}

	public Document getById(Long id) {
		return documentRepository.findById(id).orElseThrow();
	}

	@Transactional
	public void delete(Long id) {
		Document document = documentRepository.findById(id).orElseThrow();
		delete(document);
	}

	@Transactional
	public void delete(Document document) {
		documentRepository.delete(document);
	}

	@Transactional
	public void getDocumentHttpResponse(Long id, HttpServletResponse httpServletResponse) throws AgapeIOException {
		Document document = getById(id);
		try {
			utilsService.copyFileStreamToHttpResponse(document.getFileName(), document.getContentType(), document.getInputStream(), httpServletResponse);
		} catch (IOException e) {
			throw new AgapeIOException(e.getMessage());
		}
	}

}
