package org.esupportail.esupagape.dtos;

import java.util.Date;

public interface DocumentDto {

    Long getId();

    String getFileName();

    Long getSize();

    String getContentType();

    Date getCreateDate();

    Long getParentId();

    String getParentType();

}
