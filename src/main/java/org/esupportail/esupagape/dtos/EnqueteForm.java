package org.esupportail.esupagape.dtos;

import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.entity.enums.enquete.*;

import java.util.HashSet;
import java.util.Set;

public class EnqueteForm {

    private Long id;

    private String nfic;

    private String numetu;

    private String an;

    private Gender gender;

    private TypeFrmn typeFrmn;

    private ModFrmn modFrmn;

    private CodSco codSco;

    private CodFmt codFmt;

    private CodFil codFil;

    private CodHd codHd;

    private Boolean hdTmp;

    private String com;

    private CodPfpp codPfpp;

    private CodPfas codPfas = CodPfas.AS0;

    // private Set<CodMeahF> codMeahF;
    private String AHS0;
    private String AHS1;

    private String AHS2;

    private String AHS3;

    private String AHS4;

    private String AHS5;


    private Integer interpH;

    private Integer codeurH;

    private String aidHNat;

    private Set<CodMeae> codMeae = new HashSet<>();

    private String autAE;

    private CodMeaa codMeaa;

    private String autAA;

//    private Set<CodAmL> codAmL;

    private String AM0;

    private String AM1;

    private String AM2;

    private String AM3;

    private String AM4;

    private String AM5;

    private String AM6;

    private String AM7;

    private String AM8;

    private String djaCop;

    private String newNum;

    private String newId;

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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public TypeFrmn getTypeFrmn() {
        return typeFrmn;
    }

    public void setTypeFrmn(TypeFrmn typeFrmn) {
        this.typeFrmn = typeFrmn;
    }

    public ModFrmn getModFrmn() {
        return modFrmn;
    }

    public void setModFrmn(ModFrmn modFrmn) {
        this.modFrmn = modFrmn;
    }

    public CodSco getCodSco() {
        return codSco;
    }

    public void setCodSco(CodSco codSco) {
        this.codSco = codSco;
    }

    public CodFmt getCodFmt() {
        return codFmt;
    }

    public void setCodFmt(CodFmt codFmt) {
        this.codFmt = codFmt;
    }

    public CodFil getCodFil() {
        return codFil;
    }

    public void setCodFil(CodFil codFil) {
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

    public String getAHS1() {
        return AHS1;
    }

    public void setAHS1(String AHS1) {
        this.AHS1 = AHS1;
    }

    public String getAHS2() {
        return AHS2;
    }

    public void setAHS2(String AHS2) {
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

    public Set<CodMeae> getCodMeae() {
        return codMeae;
    }

    public void setCodMeae(Set<CodMeae> codMeae) {
        this.codMeae = codMeae;
    }

    public String getAutAE() {
        return autAE;
    }

    public void setAutAE(String autAE) {
        this.autAE = autAE;
    }

    public CodMeaa getCodMeaa() {
        return codMeaa;
    }

    public void setCodMeaa(CodMeaa codMeaa) {
        this.codMeaa = codMeaa;
    }

    public String getAutAA() {
        return autAA;
    }

    public void setAutAA(String autAA) {
        this.autAA = autAA;
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
