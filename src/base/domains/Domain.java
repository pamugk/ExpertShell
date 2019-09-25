package base.domains;

import java.util.*;

public class Domain {
    private UUID guid;
    private String name;
    private List<Value> values;
    private String type;

    public Domain(UUID guid, String name) {
        this.guid = guid;
        this.name= name;
        values = new ArrayList<>();
        type = "String";
    }

    public UUID getGuid() {
        return guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Value> getValues() {
        return values;
    }

    public boolean contains(String value) {
        return values.contains(value);
    }

    public boolean containsAll(Collection<String> values) {
        return this.values.containsAll(values);
    }

    public boolean remove(String value) {
        return values.remove(value);
    }

    public boolean removeAll(Collection<String> values) {
        return this.values.removeAll(values);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
