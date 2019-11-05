package expertshellgui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import base.rules.Fact;
import expertsystem.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ReasoningDialogController {
    private void changeExpandedTreeState(TreeItem<ReasoningTreeNode> node, boolean expanded) {
        node.setExpanded(expanded);
        node.getChildren().forEach(childNode -> changeExpandedTreeState(childNode, expanded));
    }

    private TreeItem<ReasoningTreeNode> readVariableTreeNode(VariableReasoningNode varnode) {
        TreeItem<ReasoningTreeNode> node = new TreeItem<>(varnode);
        if (varnode.getActivatedRule() != null)
            node.getChildren().add(readRuleTreeNode(varnode.getActivatedRule()));
        return node;
    }

    private TreeItem<ReasoningTreeNode> readRuleTreeNode(RuleReasoningNode rulenode) {
        TreeItem<ReasoningTreeNode> node = new TreeItem<>(rulenode);
        for (VariableReasoningNode lookedUpVar : rulenode.getLookedUpVariables())
            node.getChildren().add(readVariableTreeNode(lookedUpVar));
        return node;
    }

    private void setup(ExpertSystem expertSystem) {
        scratchStorageLV.getItems().addAll(expertSystem.getUsedFacts());
        ReasoningTree rtree = expertSystem.getReasoning();
        activatedRulesTreeView.setRoot(readVariableTreeNode(rtree.getRoot()));
    }

    static void show(String title, ExpertSystem expertSystem, ResourceBundle resources,
                     EventHandler<WindowEvent> onCLose, Modality modality)
            throws IOException {
        FXMLLoader loader = new FXMLLoader(DomainDialogController.class.getResource("/fxml/reasoningdialog.fxml"),
                resources);
        Parent dialogRoot = loader.load();
        ReasoningDialogController dialog = loader.getController();
        dialog.setup(expertSystem);
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.initModality(modality);
        dialogStage.setScene(new Scene(dialogRoot));
        dialogStage.setOnCloseRequest(onCLose);
        dialogStage.show();
    }

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Button expandAllBtn;
    @FXML
    private Button collapseAllBtn;
    @FXML
    private TreeView<ReasoningTreeNode> activatedRulesTreeView;
    @FXML
    private ListView<Fact> scratchStorageLV;
    @FXML
    private VBox reasoningVBox;
    @FXML
    private TextArea ruleReasonTextArea;
    @FXML
    void collapseAllBtn_OnAction(ActionEvent event) {
        activatedRulesTreeView.getSelectionModel().clearSelection();
        scratchStorageLV.getSelectionModel().clearSelection();
        changeExpandedTreeState(activatedRulesTreeView.getRoot(), false);
    }
    @FXML
    void expandAllBtnOnActon(ActionEvent event) {
        changeExpandedTreeState(activatedRulesTreeView.getRoot(), true);
    }

    @FXML
    void initialize() {
        assert activatedRulesTreeView != null : "fx:id=\"activatedRulesTreeView\" was not injected: check your FXML file 'reasoningdialog.fxml'.";
        assert scratchStorageLV != null : "fx:id=\"scrattchStorageLV\" was not injected: check your FXML file 'reasoningdialog.fxml'.";

        scratchStorageLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        scratchStorageLV.setCellFactory(factListView -> {
            ListCell<Fact> cell = new ListCell<>() {
                @Override
                protected void updateItem(Fact item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty)
                        setText(null);
                    else
                        setText(item.toString());
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (!cell.isEmpty())
                    activatedRulesTreeView.getSelectionModel().clearSelection();
            });
            return cell ;
        });

        activatedRulesTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        activatedRulesTreeView.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, unselectedNode, selectedNode) -> {
            scratchStorageLV.getSelectionModel().clearSelection();
            reasoningVBox.setVisible(false);
            if (selectedNode.getValue() == null)
                return;
            ReasoningTreeNode node = selectedNode.getValue();
            if (node instanceof VariableReasoningNode)
                scratchStorageLV.getSelectionModel()
                        .select(((VariableReasoningNode)node).getAssociatedFact());
            else {
                ((RuleReasoningNode)node).collectAssociatedFacts().forEach(fact -> {
                    reasoningVBox.setVisible(true);
                    scratchStorageLV.getSelectionModel().select(fact);
                });
                ruleReasonTextArea.setText(((RuleReasoningNode)node).getAssociatedRule().getReason());
            }
        });
    }
}
