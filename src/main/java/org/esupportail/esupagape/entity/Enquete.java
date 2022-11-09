package org.esupportail.esupagape.entity;

import org.esupportail.esupagape.entity.enums.Civilite;
import org.esupportail.esupagape.entity.enums.enquete.*;

import javax.persistence.*;
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
    private Civilite civilite;

    @Enumerated(EnumType.STRING)
    private TypeFrmn typefrmn;

    @Enumerated(EnumType.STRING)
    private ModFrmn modfrmn;

    @Enumerated(EnumType.STRING)
    private CodSco codsco;

    @Enumerated(EnumType.STRING)
    private CodFmt codfmt;

    @Enumerated(EnumType.STRING)
    private CodFil codfil;

    @Enumerated(EnumType.STRING)
    private CodHd codhd;

    private boolean hdtmp;

    @Column(columnDefinition = "TEXT")
    private String com;

    @Enumerated(EnumType.STRING)
    private CodPfpp codpfpp;

    @Enumerated(EnumType.STRING)
    private CodPfas codpfas;

    @ElementCollection(targetClass=CodMeahF.class)
    @Enumerated(EnumType.STRING)
    private Set<CodMeahF> codmeahF;

    private int interpH;

    private int codeurH;

    @Column(columnDefinition = "TEXT")
    private String aidhnat;

    @ElementCollection(targetClass=CodMeae.class)
    @Enumerated(EnumType.STRING)
    private Set<CodMeae> codmeae;

    @Column(columnDefinition = "TEXT")
    private String autae;

    @Enumerated(EnumType.STRING)
    private CodMeaa codmeaa;

    @Column(columnDefinition = "TEXT")
    private String autaa;

    @ElementCollection(targetClass=CodAmL.class)
    @Enumerated(EnumType.STRING)
    private Set<CodAmL> codamL;

    private String djaCop;

    private String newnum;

    private String newid;

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

    public Civilite getCivilite() {
        return civilite;
    }

    public void setCivilite(Civilite civilite) {
        this.civilite = civilite;
    }

    public TypeFrmn getTypefrmn() {
        return typefrmn;
    }

    public void setTypefrmn(TypeFrmn typefrmn) {
        this.typefrmn = typefrmn;
    }

    public ModFrmn getModfrmn() {
        return modfrmn;
    }

    public void setModfrmn(ModFrmn modfrmn) {
        this.modfrmn = modfrmn;
    }

    public CodSco getCodsco() {
        return codsco;
    }

    public void setCodsco(CodSco codsco) {
        this.codsco = codsco;
    }

    public CodFmt getCodfmt() {
        return codfmt;
    }

    public void setCodfmt(CodFmt codfmt) {
        this.codfmt = codfmt;
    }

    public CodFil getCodfil() {
        return codfil;
    }

    public void setCodfil(CodFil codfil) {
        this.codfil = codfil;
    }

    public CodHd getCodhd() {
        return codhd;
    }

    public void setCodhd(CodHd codhd) {
        this.codhd = codhd;
    }

    public boolean isHdtmp() {
        return hdtmp;
    }

    public void setHdtmp(boolean hdtmp) {
        this.hdtmp = hdtmp;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public CodPfpp getCodpfpp() {
        return codpfpp;
    }

    public void setCodpfpp(CodPfpp codpfpp) {
        this.codpfpp = codpfpp;
    }

    public CodPfas getCodpfas() {
        return codpfas;
    }

    public void setCodpfas(CodPfas codpfas) {
        this.codpfas = codpfas;
    }

    public Set<CodMeahF> getCodmeahF() {
        return codmeahF;
    }

    public void setCodmeahF(Set<CodMeahF> codmeahF) {
        this.codmeahF = codmeahF;
    }

    public int getInterpH() {
        return interpH;
    }

    public void setInterpH(int interpH) {
        this.interpH = interpH;
    }

    public int getCodeurH() {
        return codeurH;
    }

    public void setCodeurH(int codeurH) {
        this.codeurH = codeurH;
    }

    public String getAidhnat() {
        return aidhnat;
    }

    public void setAidhnat(String aidhnat) {
        this.aidhnat = aidhnat;
    }

    public Set<CodMeae> getCodmeae() {
        return codmeae;
    }

    public void setCodmeae(Set<CodMeae> codmeae) {
        this.codmeae = codmeae;
    }

    public String getAutae() {
        return autae;
    }

    public void setAutae(String autae) {
        this.autae = autae;
    }

    public CodMeaa getCodmeaa() {
        return codmeaa;
    }

    public void setCodmeaa(CodMeaa codmeaa) {
        this.codmeaa = codmeaa;
    }

    public String getAutaa() {
        return autaa;
    }

    public void setAutaa(String autaa) {
        this.autaa = autaa;
    }

    public Set<CodAmL> getCodamL() {
        return codamL;
    }

    public void setCodamL(Set<CodAmL> codamL) {
        this.codamL = codamL;
    }

    public String getDjaCop() {
        return djaCop;
    }

    public void setDjaCop(String djaCop) {
        this.djaCop = djaCop;
    }

    public String getNewnum() {
        return newnum;
    }

    public void setNewnum(String newnum) {
        this.newnum = newnum;
    }

    public String getNewid() {
        return newid;
    }

    public void setNewid(String newid) {
        this.newid = newid;
    }

    public Dossier getDossier() {
        return dossier;
    }

    public void setDossier(Dossier dossier) {
        this.dossier = dossier;
    }
}
