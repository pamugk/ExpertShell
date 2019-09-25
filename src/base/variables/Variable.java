package base.variables;

import base.domains.Domain;
import base.environmentvars.CFVA;
import base.environmentvars.WHN;
import base.environmentvars.RIGR;

public class Variable<T extends Comparable<T>> {
    private String guid;
    private Domain<T> domain;
    private Classes varClass;

    private String name;
    private T value;
    private String label;
    private WHN when;
    private CFVA type;
    private int limit;
    private RIGR rigor;

    public Variable(String guid, String name, Domain<T> domain, Classes varClass) {
        this.guid = guid;
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

    public T getValue() { return value; }
    public void setValue(T value) { this.value = value; }
    public String getGuid() { return guid; }
    public Classes getVarClass() { return varClass; }
    public void setVarClass(Classes varClass) { this.varClass = varClass; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLabel(){ return label; }
    public void setLabel(String label) { this.label = label; }
    public WHN getWhen() { return when; }
    public void setWhen(WHN when) { this.when = when; }
    public CFVA getType() { return type; }
    public void setType(CFVA type) { this.type = type; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
    public RIGR getRigor() { return rigor; }
    public void setRigor(RIGR rigor) { this.rigor = rigor; }
}
