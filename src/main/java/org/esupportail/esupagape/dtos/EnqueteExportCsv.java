package org.esupportail.esupagape.dtos;

import org.esupportail.esupagape.entity.enums.enquete.TypFrmn;

public class EnqueteExportCsv {

    private Long dossierId;

    private String numEtu;

    private String an;

    private TypFrmn typFrmn;

    public EnqueteExportCsv() {
    }

    public EnqueteExportCsv(Long dossierId, String numEtu, String an, TypFrmn typFrmn) {
        this.dossierId = dossierId;
        this.numEtu = numEtu;
        this.an = an;
        this.typFrmn =typFrmn;

    }

    public Long getDossierId() {
        return dossierId;
    }

    public void setDossierId(Long dossierId) {
        this.dossierId = dossierId;
    }

    public String getNumEtu() {
        return numEtu;
    }

    public void setNumEtu(String numEtu) {
        this.numEtu = numEtu;
    }

    public String getAn() {
        return an;
    }

    public void setAn(String an) {
        this.an = an;
    }

    public TypFrmn getTypFrmn() {return typFrmn;}

    public void setTypFrmn(TypFrmn typFrmn) {this.typFrmn = typFrmn;}
}
