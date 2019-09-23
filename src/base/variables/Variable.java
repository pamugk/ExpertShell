package base.variables;

import base.domains.Domain;
import base.exceptions.OutOfDomainException;

public class Variable<T extends Comparable<T>> {
    private int id;
    private String name;

    private Domain<T> domain;
    private T value;
    private String description;
    private Classes varClass;

    public Variable(int id, String name, Domain<T> domain, Classes varClass) {
        this.id = id;
        this.name = name;
        this.domain = domain;
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

    public Domain<T> getDomain(){
        return domain;
    }

    public void setDomain(Domain<T> domain) {
        if (domain == null)
            throw new NullPointerException();
        this.domain = domain;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) throws OutOfDomainException {
        if (!domain.contains(value))
            throw new OutOfDomainException();
        this.value = value;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Classes getVarClass() {
        return varClass;
    }

    public void setVarClass(Classes varClass) {
        this.varClass = varClass;
    }
}
