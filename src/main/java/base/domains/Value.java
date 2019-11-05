package base.domains;

import base.rules.Assignable;

import java.io.Serializable;
import java.util.UUID;

public class Value implements Assignable, Serializable {
    private UUID guid;
    private String content;

    public Value(UUID guid, String content) {
        this.guid = guid;
        this.content = content;
    }

    public UUID getGuid() {
        return guid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }

    @Override
    public String getFactPart() {
        return content;
    }
}
