package org.esupportail.esupagape.entity;

import org.esupportail.esupagape.entity.enums.enquete.*;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Enquete {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String an;

    private String sexe;

    @Enumerated(EnumType.STRING)
    private TypFrmn typFrmn;

    @ElementCollection(targetClass=ModFrmn.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<ModFrmn> modFrmn;

    private String codSco;

    private String codFmt;

    private String codFil;

    @Enumerated(EnumType.STRING)
    private CodHd codHd;

    private Boolean hdTmp;

    @Column(columnDefinition = "TEXT")
    private String com;

    @Enumerated(EnumType.STRING)
    private CodPfpp codPfpp;

    @ElementCollection(targetClass=CodPfas.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<CodPfas> codPfas = new HashSet<>();

    @ElementCollection(targetClass=CodMeahF.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<CodMeahF> codMeahF = new HashSet<>();

    @Column(columnDefinition = "TEXT")
    private String aidHNat;

    @ElementCollection(targetClass=CodMeae.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<CodMeae> codMeae = new HashSet<>();

    @Column(columnDefinition = "TEXT")
    private String autAE;

    @ElementCollection(targetClass=CodMeaa.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<CodMeaa> codMeaa = new HashSet<>();

    @Column(columnDefinition = "TEXT")
    private String autAA;

    @ElementCollection(targetClass=CodAmL.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<CodAmL> codAmL = new HashSet<>();

    @OneToOne(optional = false)
    private Dossier dossier;

    private Boolean finished;

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

    public Set<ModFrmn> getModFrmn() {
        return modFrmn;
    }

    public void setModFrmn(Set<ModFrmn> modFrmn) {
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

    public Set<CodPfas> getCodPfas() {
        return codPfas;
    }

    public void setCodPfas(Set<CodPfas> codPfas) {
        this.codPfas = codPfas;
    }

    public Set<CodMeahF> getCodMeahF() {
        return codMeahF;
    }

    public void setCodMeahF(Set<CodMeahF> codMeahF) {
        this.codMeahF = codMeahF;
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

    public Dossier getDossier() {
        return dossier;
    }

    public void setDossier(Dossier dossier) {
        this.dossier = dossier;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }
}
