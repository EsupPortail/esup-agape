package org.esupportail.esupagape.dtos;

import org.esupportail.esupagape.entity.enums.enquete.CodAmL;
import org.esupportail.esupagape.entity.enums.enquete.CodHd;
import org.esupportail.esupagape.entity.enums.enquete.CodMeaa;
import org.esupportail.esupagape.entity.enums.enquete.CodMeae;
import org.esupportail.esupagape.entity.enums.enquete.CodMeahF;
import org.esupportail.esupagape.entity.enums.enquete.CodPfas;
import org.esupportail.esupagape.entity.enums.enquete.CodPfpp;
import org.esupportail.esupagape.entity.enums.enquete.ModFrmn;
import org.esupportail.esupagape.entity.enums.enquete.TypFrmn;

import java.util.Set;

public interface EnqueteExportCsv {


Long getId();
String getYear();
String getNfic();
String getNumetu();
String getAn();
String getSexe();
TypFrmn getTypFrmn();
ModFrmn getModFrmn();
String getCodSco();
String getCodFmt();
String getCodFil();
CodHd getCodHd();
Boolean getHdTmp();
String getCom();
CodPfpp getCodPfpp();
CodPfas getCodPfas();
Set<CodMeahF>getCodMeahF();
Integer getInterpH();
Integer getCodeurH();
String getAidHNat();
Set<CodMeae> getCodMeae();
String getAutAE();
Set<CodMeaa>getCodMeaa();
String getAutAA();
Set<CodAmL>getCodAmL();
String getDjaCop();
String getNewNum();
String getNewId();

}
