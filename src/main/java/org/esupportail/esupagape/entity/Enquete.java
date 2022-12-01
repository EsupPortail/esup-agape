package org.esupportail.esupagape.entity;

import org.esupportail.esupagape.entity.enums.Gender;
import org.esupportail.esupagape.entity.enums.enquete.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Enquete {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nfic;

    private String numetu;

    private String an;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private TypeFrmn typeFrmn;

    @Enumerated(EnumType.STRING)
    private ModFrmn modFrmn;

    @Enumerated(EnumType.STRING)
    private CodSco codSco;

    @Enumerated(EnumType.STRING)
    private CodFmt codFmt;

    @Enumerated(EnumType.STRING)
    private CodFil codFil;

    @Enumerated(EnumType.STRING)
    private CodHd codHd;

    private Boolean hdTmp;

    @Column(columnDefinition = "TEXT")
    private String com;

    @Enumerated(EnumType.STRING)
    private CodPfpp codPfpp;

    @Enumerated(EnumType.STRING)
    private CodPfas codPfas = CodPfas.AS0;

    @ElementCollection(targetClass=CodMeahF.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<CodMeahF> codMeahF;

    private Integer interpH;

    private Integer codeurH;

    @Column(columnDefinition = "TEXT")
    private String aidHNat;

    @ElementCollection(targetClass=CodMeae.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<CodMeae> codMeae = new HashSet<>();

    @Column(columnDefinition = "TEXT")
    private String autAE;

    @Enumerated(EnumType.STRING)
    private CodMeaa codMeaa;

    @Column(columnDefinition = "TEXT")
    private String autAA;

    @ElementCollection(targetClass=CodAmL.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<CodAmL> codAmL;

    @ElementCollection(targetClass = LibelleCodAmL.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<LibelleCodAmL> libelleCodAmL;

    private String djaCop;

    private String newNum;

    private String newId;

    @OneToOne
    private Dossier dossier;

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

    public Set<CodMeahF> getCodMeahF() {
        return codMeahF;
    }

    public void setCodMeahF(Set<CodMeahF> codMeahF) {
        this.codMeahF = codMeahF;
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

    public Set<CodAmL> getCodAmL() {
        return codAmL;
    }

    public void setCodAmL(Set<CodAmL> codAmL) {
        this.codAmL = codAmL;
    }

    public Set<LibelleCodAmL> getLibelleCodAmL() {
        return libelleCodAmL;
    }

    public void setLibelleCodAmL(Set<LibelleCodAmL> libelleCodAmL) {
        this.libelleCodAmL = libelleCodAmL;
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

    public Dossier getDossier() {
        return dossier;
    }

    public void setDossier(Dossier dossier) {
        this.dossier = dossier;
    }
}
