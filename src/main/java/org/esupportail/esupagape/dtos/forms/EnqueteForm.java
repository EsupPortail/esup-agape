package org.esupportail.esupagape.dtos.forms;

import org.esupportail.esupagape.entity.enums.enquete.CodAmL;
import org.esupportail.esupagape.entity.enums.enquete.CodHd;
import org.esupportail.esupagape.entity.enums.enquete.CodMeaa;
import org.esupportail.esupagape.entity.enums.enquete.CodMeae;
import org.esupportail.esupagape.entity.enums.enquete.CodPfas;
import org.esupportail.esupagape.entity.enums.enquete.CodPfpp;
import org.esupportail.esupagape.entity.enums.enquete.ModFrmn;
import org.esupportail.esupagape.entity.enums.enquete.TypFrmn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnqueteForm {

    private Long id;
    private String an;
    private String sexe;
    private TypFrmn typFrmn;
    private ModFrmn modFrmn;
    private Boolean alternance;
    private String codSco;
    private String codFmt;
    private String codFil;
    private CodHd codHd;
    private Boolean hdTmp;
    private String com;
    private CodPfpp codPfpp;
    private String codPfasOn;
    private Set<CodPfas> codPfas = new HashSet<>();
    private String AHS0;
    private List<String> AHS1 = new ArrayList<>();
    private List<String> AHS2 = new ArrayList<>();
    private String AHS3;
    private String AHS4;
    private String AHS5;
    private String aidHNat;
    private Set<CodMeae> codMeae = new HashSet<>();
    private String autAE;

    private CodMeaa codMeaaStructure;
    private Set<CodMeaa> codMeaa = new HashSet<>();
    private Set<CodAmL> codAmL = new HashSet<>();
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getAlternance() {
        return alternance;
    }

    public void setAlternance(Boolean alternance) {
        this.alternance = alternance;
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

    public Set<CodPfas> getCodPfas() {
        return codPfas;
    }

    public String getCodPfasOn() {
        return codPfasOn;
    }

    public void setCodPfasOn(String codPfasOn) {
        this.codPfasOn = codPfasOn;
    }

    public void setCodPfas(Set<CodPfas> codPfas) {
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

    public CodMeaa getCodMeaaStructure() {
        return codMeaaStructure;
    }

    public void setCodMeaaStructure(CodMeaa codMeaaStructure) {
        this.codMeaaStructure = codMeaaStructure;
    }

    public Set<CodMeaa> getCodMeaa() {
        return codMeaa;
    }

    public void setCodMeaa(Set<CodMeaa> codMeaa) {
        this.codMeaa = codMeaa;
    }


    public String getAutAA() {
        return autAA;
    }

    public void setAutAA(String autAA) {
        this.autAA = autAA;
    }

    public Set<CodAmL> getCodAmL() {
        return codAmL;
    }

    public void setCodAmL(Set<CodAmL> codAmL) {
        this.codAmL = codAmL;
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

}
