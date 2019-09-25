package base.domains;

import java.util.UUID;

public class Value {
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
}
