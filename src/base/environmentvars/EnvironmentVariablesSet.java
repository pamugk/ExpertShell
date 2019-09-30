package base.environmentvars;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EnvironmentVariablesSet implements Serializable {
    private boolean auto;
    private Color bacg;
    private boolean bell;
    private int best;
    private CFCO cfco;
    private CFVA cfva;
    private int deci;
    private String typ;
    private Color forg;
    private boolean guid;
    private boolean icas;
    private boolean imac;
    private boolean inup;
    private int hres;
    private int llog;
    private boolean lmod;
    private int lnum;
    private int lstr;
    private int numv;
    private boolean ocf;
    private int ofuz;
    private List<SORD> sord;
    private RIGR rigr;
    private TRAC trac;
    private TRYP tryp;
    private int unkn;
    private WHN whn;

    public EnvironmentVariablesSet() {
        sord = new ArrayList<>();
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public Color getBacg() {
        return bacg;
    }

    public void setBacg(Color bacg) {
        this.bacg = bacg;
    }

    public boolean isBell() {
        return bell;
    }

    public void setBell(boolean bell) {
        this.bell = bell;
    }

    public int getBest() {
        return best;
    }

    public void setBest(int best) {
        this.best = best;
    }

    public CFCO getCfco() {
        return cfco;
    }

    public void setCfco(CFCO cfco) {
        this.cfco = cfco;
    }

    public CFVA getCfva() {
        return cfva;
    }

    public void setCfva(CFVA cfva) {
        this.cfva = cfva;
    }

    public int getDeci() {
        return deci;
    }

    public void setDeci(int deci) {
        this.deci = deci;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public Color getForg() {
        return forg;
    }

    public void setForg(Color forg) {
        this.forg = forg;
    }

    public boolean isGuid() {
        return guid;
    }

    public void setGuid(boolean guid) {
        this.guid = guid;
    }

    public boolean isIcas() {
        return icas;
    }

    public void setIcas(boolean icas) {
        this.icas = icas;
    }

    public boolean isImac() {
        return imac;
    }

    public void setImac(boolean imac) {
        this.imac = imac;
    }

    public boolean isInup() {
        return inup;
    }

    public void setInup(boolean inup) {
        this.inup = inup;
    }

    public int getHres() {
        return hres;
    }

    public void setHres(int hres) {
        this.hres = hres;
    }

    public int getLlog() {
        return llog;
    }

    public void setLlog(int llog) {
        this.llog = llog;
    }

    public boolean isLmod() {
        return lmod;
    }

    public void setLmod(boolean lmod) {
        this.lmod = lmod;
    }

    public int getLnum() {
        return lnum;
    }

    public void setLnum(int lnum) {
        this.lnum = lnum;
    }

    public int getLstr() {
        return lstr;
    }

    public void setLstr(int lstr) {
        this.lstr = lstr;
    }

    public int getNumv() {
        return numv;
    }

    public void setNumv(int numv) {
        this.numv = numv;
    }

    public boolean isOcf() {
        return ocf;
    }

    public void setOcf(boolean ocf) {
        this.ocf = ocf;
    }

    public int getOfuz() {
        return ofuz;
    }

    public void setOfuz(int ofuz) {
        this.ofuz = ofuz;
    }

    public List<SORD> getSord() {
        return sord;
    }

    public RIGR getRigr() {
        return rigr;
    }

    public void setRigr(RIGR rigr) {
        this.rigr = rigr;
    }

    public TRAC getTrac() {
        return trac;
    }

    public void setTrac(TRAC trac) {
        this.trac = trac;
    }

    public TRYP getTryp() {
        return tryp;
    }

    public void setTryp(TRYP tryp) {
        this.tryp = tryp;
    }

    public int getUnkn() {
        return unkn;
    }

    public void setUnkn(int unkn) {
        this.unkn = unkn;
    }

    public WHN getWhn() {
        return whn;
    }

    public void setWhn(WHN whn) {
        this.whn = whn;
    }
}
