package expertsystem;

public class TraverseTree<T> {
    private TraverseNode<T> root;

    TraverseTree() {
        root = new TraverseNode<>(null);
    }

    public TraverseNode<T> getRoot() {
        return root;
    }

    public void clear() {
        clearNode(root);
    }

    private void clearNode(TraverseNode<T> node) {
        for (var child: node.getChildren())
            clearNode(child);
        node.getChildren().clear();
    }
}
