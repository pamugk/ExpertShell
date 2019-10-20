package expertsystem;

import java.util.ArrayList;
import java.util.List;

public class TraverseNode<T> {
    private TraverseNode parent;
    private List<TraverseNode<T>> children;
    private T content;

    TraverseNode(TraverseNode parent) {
        this.parent = parent;
        children = new ArrayList<>();
    }

    public TraverseNode getParent() {
        return parent;
    }

    public List<TraverseNode<T>> getChildren() {
        return children;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T newContent) {
        content = newContent;
    }
}
