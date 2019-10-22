package expertshellgui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import base.rules.Fact;
import expertsystem.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

public class ReasoningDialogController {
    private void setup(ExpertSystem expertSystem) {
        scrattchStorageLV.getItems().addAll(expertSystem.getUsedFacts());
        ReasoningTree rtree = expertSystem.getReasoning();
        activatedRulesTreeView.setRoot(readVariableTreeNode(rtree.getRoot()));
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

    static void showAndWait(String title, ExpertSystem expertSystem, ResourceBundle resources)
            throws IOException {
        FXMLLoader loader = new FXMLLoader(DomainDialogController.class.getResource("/fxml/reasoningdialog.fxml"),
                resources);
        Parent dialogRoot = loader.load();
        ReasoningDialogController dialog = loader.getController();
        dialog.setup(expertSystem);
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.setScene(new Scene(dialogRoot));
        dialogStage.show();
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TreeView<ReasoningTreeNode> activatedRulesTreeView;

    @FXML
    private ListView<Fact> scrattchStorageLV;

    @FXML
    void initialize() {
        assert activatedRulesTreeView != null : "fx:id=\"activatedRulesTreeView\" was not injected: check your FXML file 'reasoningdialog.fxml'.";
        assert scrattchStorageLV != null : "fx:id=\"scrattchStorageLV\" was not injected: check your FXML file 'reasoningdialog.fxml'.";

    }
}
