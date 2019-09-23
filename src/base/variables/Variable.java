package base.variables;

import base.domains.Domain;
import base.environmentvars.CFVA;
import base.environmentvars.WHN;
import base.environmentvars.RIGR;

public class Variable<T extends Comparable<T>> {
    private Domain<T> domain;
    private Classes varClass;

    private int id;
    private String name;
    private String label;
    private WHN when;
    private CFVA type;
    private int limit;
    private RIGR rigor;

    public Variable(int id, String name, Domain<T> domain, Classes varClass) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.varClass = varClass;
    }

    public Domain<T> getDomain(){
        return domain;
    }

    public void setDomain(Domain<T> domain) {
        if (domain == null)
            throw new NullPointerException();
        this.domain = domain;
    }

    public Classes getVarClass() {
        return varClass;
    }

    public void setVarClass(Classes varClass) {
        this.varClass = varClass;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel(){
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public WHN getWhen() {
        return when;
    }

    public void setWhen(WHN when) {
        this.when = when;
    }

    public CFVA getType() {
        return type;
    }

    public void setType(CFVA type) {
        this.type = type;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public RIGR getRigor() {
        return rigor;
    }

    public void setRigor(RIGR rigor) {
        this.rigor = rigor;
    }
}
