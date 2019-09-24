package base.domains;

import java.util.*;

public class Domain<T extends Comparable<T>> {
    private String guid;
    private String name;
    private Set<T> values;

    public Domain(String guid, String name) {
        this.guid = guid;
        this.name= name;
        values = new HashSet<>();
    }

    public String getGuid() {
        return guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private List<T> getValues() {
        return new ArrayList<>(values);
    }

    public boolean contains(T value) {
        return values.contains(value);
    }

    public boolean containsAll(Collection<T> values) {
        return this.values.containsAll(values);
    }

    public boolean add(T value) {
        return values.add(value);
    }

    public boolean addAll(Collection<T> values) {
        return this.values.addAll(values);
    }

    public boolean remove(T value) {
        return values.remove(value);
    }

    public boolean removeAll(Collection<T> values) {
        return this.values.removeAll(values);
    }
}
