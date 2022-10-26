package org.esupportail.esupagape.service;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.esupportail.esupagape.entity.BigFile;
import org.esupportail.esupagape.entity.Document;
import org.esupportail.esupagape.entity.Dossier;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.esupportail.esupagape.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class DocumentService {

	private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

	@Resource
	private DocumentRepository documentRepository;

	@Resource
	private BigFileService bigFileService;

	@Transactional
	public Document createDocument(InputStream inputStream, String name, String contentType, Long parentId, String parentType, Dossier dossier) throws AgapeIOException {
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
			copyFileStreamToHttpResponse(document.getFileName(), document.getContentType(), document.getInputStream(), httpServletResponse);
		} catch (IOException e) {
			throw new AgapeIOException(e.getMessage());
		}
	}

	public void copyFileStreamToHttpResponse(String name, String contentType, InputStream inputStream, HttpServletResponse httpServletResponse) throws IOException {
		httpServletResponse.setContentType(contentType);
		httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(name, StandardCharsets.UTF_8.toString()));
		IOUtils.copyLarge(inputStream, httpServletResponse.getOutputStream());
	}
}
