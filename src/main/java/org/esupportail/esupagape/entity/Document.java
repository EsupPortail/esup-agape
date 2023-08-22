package org.esupportail.esupagape.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.esupportail.esupagape.entity.enums.TypeDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;

@Entity
@Configurable
public class Document {

	private static final Logger logger = LoggerFactory.getLogger(Document.class);

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@Version
    private Integer version;
	
	private String fileName;

    private Long size;
    
    private String contentType;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    private Date createDate;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    private BigFile bigFile = new BigFile();

    private Long parentId;

    private String parentType;

    @ManyToOne(optional = false)
    private Dossier dossier;

    @Enumerated(EnumType.STRING)
    private TypeDocument typeDocument;

    @JsonIgnore
    public InputStream getInputStream() {
        try {
            if(this.bigFile != null && this.bigFile.getBinaryFile() != null) {
                return this.bigFile.getBinaryFile().getBinaryStream();
            }
        } catch (SQLException e) {
            logger.error("unable to get inputStream", e);
        }
        return null;
    }

	public String getFileName() {
        return this.fileName;
    }

	public void setFileName(String fileName) {
        this.fileName = fileName;
    }

	public Long getSize() {
        return this.size;
    }

	public void setSize(Long size) {
        this.size = size;
    }

	public Long getParentId() {
        return this.parentId;
    }

	public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

	public String getContentType() {
        return this.contentType;
    }

	public void setContentType(String contentType) {
        this.contentType = contentType;
    }

	public Date getCreateDate() {
        return this.createDate;
    }

	public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

	public BigFile getBigFile() {
        return this.bigFile;
    }

	public void setBigFile(BigFile bigFile) {
        this.bigFile = bigFile;
    }

	public Long getId() {
        return this.id;
    }

	public void setId(Long id) {
        this.id = id;
    }

	public Integer getVersion() {
        return this.version;
    }

	public void setVersion(Integer version) {
        this.version = version;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public Dossier getDossier() {
        return dossier;
    }

    public void setDossier(Dossier dossier) {
        this.dossier = dossier;
    }

    public TypeDocument getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(TypeDocument typeDocument) {
        this.typeDocument = typeDocument;
    }
}
