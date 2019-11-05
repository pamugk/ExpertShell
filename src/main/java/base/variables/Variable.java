package base.variables;

import base.domains.Domain;
import base.environmentvars.CFVA;
import base.environmentvars.WHN;
import base.environmentvars.RIGR;
import base.rules.Assignable;

import java.io.Serializable;
import java.util.UUID;

public class Variable implements Assignable, Serializable {
    private UUID guid;
    private Domain domain;
    private Types varClass;

    private String name;
    private String label;
    private WHN when;
    private CFVA type;
    private int limit;
    private RIGR rigor;
    private String question;

    public Variable(UUID guid, String name, Domain domain, Types varClass) {
        this.guid = guid;
        this.name = name;
        this.domain = domain;
        this.varClass = varClass;
    }

    public Domain getDomain(){
        return domain;
    }

    public void setDomain(Domain domain) {
        if (domain == null)
            throw new NullPointerException();
        this.domain = domain;
    }

    public UUID getGuid() { return guid; }
    public Types getVarClass() { return varClass; }
    public void setVarClass(Types varClass) { this.varClass = varClass; }
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
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    @Override
    public String toString() {  return name; }

    @Override
    public String getFactPart() {
        return name;
    }
}
