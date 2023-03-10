package org.esupportail.esupagape.dtos;

import com.opencsv.bean.CsvBindAndSplitByPosition;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import org.esupportail.esupagape.entity.enums.enquete.CodHd;
import org.esupportail.esupagape.entity.enums.enquete.CodMeaa;
import org.esupportail.esupagape.entity.enums.enquete.CodPfas;
import org.esupportail.esupagape.entity.enums.enquete.CodPfpp;
import org.esupportail.esupagape.entity.enums.enquete.ModFrmn;
import org.esupportail.esupagape.entity.enums.enquete.TypFrmn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnqueteForm {

    @CsvBindByPosition(position = 0)
    @CsvBindByName
            private Long id;

    @CsvBindByPosition(position = 1)
    @CsvBindByName
            private String nfic;

    @CsvBindByPosition(position = 2)
    @CsvBindByName
    private String numetu;

    @CsvBindByPosition(position = 3)
    private String an;

    @CsvBindByPosition(position = 4)
    private String sexe;

    @CsvBindByPosition(position = 5)
    private TypFrmn typFrmn;

    @CsvBindByPosition(position = 6)
    private ModFrmn modFrmn;

    @CsvBindByPosition(position = 7)
    private String codSco;

    @CsvBindByPosition(position = 8)
    private String codFmt;

    @CsvBindByPosition(position = 9)
    private String codFil;

    @CsvBindByPosition(position = 10)
    private CodHd codHd;

    @CsvBindByPosition(position = 11)
    private Boolean hdTmp;

    @CsvBindByPosition(position = 12)
    private String com;

    @CsvBindByPosition(position = 13)
    private CodPfpp codPfpp;

    @CsvBindByPosition(position = 14)
    private CodPfas codPfas = CodPfas.AS0;

    @CsvBindByPosition(position = 15)
    private String AHS0;

    @CsvBindByPosition(position = 16)
    private List<String> AHS1 = new ArrayList<>();

    @CsvBindByPosition(position = 17)
    private List<String> AHS2 = new ArrayList<>();

    @CsvBindByPosition(position = 18)
    private String AHS3;

    @CsvBindByPosition(position = 19)
    private String AHS4;

    @CsvBindByPosition(position = 20)
    private String AHS5;

    @CsvBindByPosition(position = 21)
    private Integer interpH;

    @CsvBindByPosition(position = 22)
    private Integer codeurH;

    @CsvBindByPosition(position = 23)
    private String aidHNat;

    @CsvBindAndSplitByPosition(position = 24, elementType = String.class)
    private Set<String> codMeae = new HashSet<>();

    @CsvBindByPosition(position = 25)
    private String autAE;

    @CsvBindByPosition(position = 26)
    private CodMeaa codMeaaStructure;

    @CsvBindAndSplitByPosition(position = 27, elementType = String.class)
    private Set<String> codMeaa = new HashSet<>();

    @CsvBindAndSplitByPosition(position = 41, elementType = String.class, writeDelimiter = "")
    private Set<String> codAmLs = new HashSet<>();
    @CsvBindByPosition(position = 28)
    private String autAA;
    private String AM0;
    private String AM1;
    private String AM2;
    private String AM3;
    private String AM4;
    private String AM5;
    private String AM6;
    private String AM7;
    private String AM8;

    @CsvBindByPosition(position = 38)
    private String djaCop;

    @CsvBindByPosition(position = 39)
    private String newNum;

    @CsvBindByPosition(position = 40)
    private String newId;



    public EnqueteForm(Long id, String numetu, String an, String sexe, TypFrmn typFrmn, ModFrmn modFrmn, String codSco, String codFmt, String codFil, CodHd codHd, Boolean hdTmp, String com, CodPfpp codPfpp, CodPfas codPfas, Integer interpH, Integer codeurH, String aidHNat, String autAE, String autAA) {
        this.id = id;
        this.numetu = numetu;
        this.an = an;
        this.sexe = sexe;
        this.typFrmn = typFrmn;
        this.modFrmn = modFrmn;
        this.codSco = codSco;
        this.codFmt = codFmt;
        this.codFil = codFil;
        this.codHd = codHd;
        this.hdTmp = hdTmp;
        this.com = com;
        this.codPfpp = codPfpp;
        this.codPfas = codPfas;
        this.interpH = interpH;
        this.codeurH = codeurH;
        this.aidHNat = aidHNat;
        this.autAE = autAE;
        this.autAA = autAA;
    }

    public EnqueteForm() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNfic() {
        return nfic;
    }

    public void setNfic(String nfic) {
        this.nfic = nfic;
    }

    public String getNumetu() {
        return numetu;
    }

    public void setNumetu(String numetu) {
        this.numetu = numetu;
    }

    public String getAn() {
        return an;
    }

    public void setAn(String an) {
        this.an = an;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public TypFrmn getTypFrmn() {
        return typFrmn;
    }

    public void setTypFrmn(TypFrmn typFrmn) {
        this.typFrmn = typFrmn;
    }

    public ModFrmn getModFrmn() {
        return modFrmn;
    }

    public void setModFrmn(ModFrmn modFrmn) {
        this.modFrmn = modFrmn;
    }

    public String getCodSco() {
        return codSco;
    }

    public void setCodSco(String codSco) {
        this.codSco = codSco;
    }

    public String getCodFmt() {
        return codFmt;
    }

    public void setCodFmt(String codFmt) {
        this.codFmt = codFmt;
    }

    public String getCodFil() {
        return codFil;
    }

    public void setCodFil(String codFil) {
        this.codFil = codFil;
    }

    public CodHd getCodHd() {
        return codHd;
    }

    public void setCodHd(CodHd codHd) {
        this.codHd = codHd;
    }

    public Boolean getHdTmp() {
        return hdTmp;
    }

    public void setHdTmp(Boolean hdTmp) {
        this.hdTmp = hdTmp;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public CodPfpp getCodPfpp() {
        return codPfpp;
    }

    public void setCodPfpp(CodPfpp codPfpp) {
        this.codPfpp = codPfpp;
    }

    public CodPfas getCodPfas() {
        return codPfas;
    }

    public void setCodPfas(CodPfas codPfas) {
        this.codPfas = codPfas;
    }

    public String getAHS0() {
        return AHS0;
    }

    public void setAHS0(String AHS0) {
        this.AHS0 = AHS0;
    }

    public List<String> getAHS1() {
        return AHS1;
    }

    public void setAHS1(List<String> AHS1) {
        this.AHS1 = AHS1;
    }

    public List<String> getAHS2() {
        return AHS2;
    }

    public void setAHS2(List<String> AHS2) {
        this.AHS2 = AHS2;
    }

    public String getAHS3() {
        return AHS3;
    }

    public void setAHS3(String AHS3) {
        this.AHS3 = AHS3;
    }

    public String getAHS4() {
        return AHS4;
    }

    public void setAHS4(String AHS4) {
        this.AHS4 = AHS4;
    }

    public String getAHS5() {
        return AHS5;
    }

    public void setAHS5(String AHS5) {
        this.AHS5 = AHS5;
    }

    public Integer getInterpH() {
        return interpH;
    }

    public void setInterpH(Integer interpH) {
        this.interpH = interpH;
    }

    public Integer getCodeurH() {
        return codeurH;
    }

    public void setCodeurH(Integer codeurH) {
        this.codeurH = codeurH;
    }

    public String getAidHNat() {
        return aidHNat;
    }

    public void setAidHNat(String aidHNat) {
        this.aidHNat = aidHNat;
    }

    public Set<String> getCodMeae() {
        return codMeae;
    }

    public void setCodMeae(Set<String> codMeae) {
        this.codMeae = codMeae;
    }

    public String getAutAE() {
        return autAE;
    }

    public void setAutAE(String autAE) {
        this.autAE = autAE;
    }

    public CodMeaa getCodMeaaStructure() {
        return codMeaaStructure;
    }

    public void setCodMeaaStructure(CodMeaa codMeaaStructure) {
        this.codMeaaStructure = codMeaaStructure;
    }

    public Set<String> getCodMeaa() {
        return codMeaa;
    }

    public void setCodMeaa(Set<String> codMeaa) {
        this.codMeaa = codMeaa;
    }

    public String getAutAA() {
        return autAA;
    }

    public void setAutAA(String autAA) {
        this.autAA = autAA;
    }

    public Set<String> getCodAmLs() {
        return codAmLs;
    }

    public void setCodAmLs(Set<String > codAmLs) {
        this.codAmLs = codAmLs;
    }

    public String getAM0() {
        return AM0;
    }

    public void setAM0(String AM0) {
        this.AM0 = AM0;
    }

    public String getAM1() {
        return AM1;
    }

    public void setAM1(String AM1) {
        this.AM1 = AM1;
    }

    public String getAM2() {
        return AM2;
    }

    public void setAM2(String AM2) {
        this.AM2 = AM2;
    }

    public String getAM3() {
        return AM3;
    }

    public void setAM3(String AM3) {
        this.AM3 = AM3;
    }

    public String getAM4() {
        return AM4;
    }

    public void setAM4(String AM4) {
        this.AM4 = AM4;
    }

    public String getAM5() {
        return AM5;
    }

    public void setAM5(String AM5) {
        this.AM5 = AM5;
    }

    public String getAM6() {
        return AM6;
    }

    public void setAM6(String AM6) {
        this.AM6 = AM6;
    }

    public String getAM7() {
        return AM7;
    }

    public void setAM7(String AM7) {
        this.AM7 = AM7;
    }

    public String getAM8() {
        return AM8;
    }

    public void setAM8(String AM8) {
        this.AM8 = AM8;
    }

    public String getDjaCop() {
        return djaCop;
    }

    public void setDjaCop(String djaCop) {
        this.djaCop = djaCop;
    }

    public String getNewNum() {
        return newNum;
    }

    public void setNewNum(String newNum) {
        this.newNum = newNum;
    }

    public String getNewId() {
        return newId;
    }

    public void setNewId(String newId) {
        this.newId = newId;
    }
}
