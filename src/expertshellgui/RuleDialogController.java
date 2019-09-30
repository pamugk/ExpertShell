package expertshellgui;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import base.domains.Domain;
import base.knowledgebase.KnowledgeBase;
import base.rules.Fact;
import base.rules.Rule;
import base.variables.Classes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RuleDialogController {
    private KnowledgeBase kb;
    private Rule rule;
    private Image addImage;
    private Image editImage;

    //<editor-fold defaultstate="collapsed" desc="Вспомогательные методы">
    private void addFact(ListView<Fact> factView, List<Classes> forbiddenClasses) {
        Fact newFact;
        try {
            newFact = FactDialogController.showAndWait(new Fact(), resources.getString("addFact"),
                    addImage, kb, resources, forbiddenClasses);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        var currentIdx = factView.getSelectionModel().getSelectedIndex();
        if (currentIdx == -1) {
            factView.getItems().add(newFact);
        }
        else
            factView.getItems().add(currentIdx, newFact);
    }

    private void close() { ((Stage)okButton.getScene().getWindow()).close(); }

    private void disableOkButton(String name) {
        okButton.setDisable(name == null || name.trim().isEmpty() || kb.getRules().stream().anyMatch(
                anyRule -> !anyRule.getGuid().equals(rule.getGuid()) && anyRule.getName().equals(name)) ||
                premisesListView.getItems().isEmpty() || conclusionsListView.getItems().isEmpty());
    }

    private void editFact(ListView<Fact> factView, List<Classes> forbiddenClasses) {
        int idx = factView.getSelectionModel().getSelectedIndex();
        Fact editedFact;
        try {
            editedFact = FactDialogController.showAndWait(
                    factView.getItems().get(idx), resources.getString("editFact"),
                    editImage, kb, resources, forbiddenClasses);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        factView.getItems().set(idx, editedFact);
    }

    private Rule getRule() { return rule; }

    private void setup(Rule oldRule, KnowledgeBase kb) {
        this.rule = oldRule;
        this.kb = kb;
        nameTextField.setText(oldRule.getName());
        reasonTextArea.setText(oldRule.getReason());
        commentTextArea.setText(oldRule.getComment());
        premisesListView.getItems().addAll(oldRule.getPremises());
        conclusionsListView.getItems().addAll(oldRule.getConclusions());
    }

    static Rule showAndWait(Rule oldRule, String title, Image icon,
                            KnowledgeBase kb, ResourceBundle resources) throws IOException {
        FXMLLoader loader = new FXMLLoader(DomainDialogController.class.getResource("/fxml/ruledialog.fxml"),
                resources);
        Parent dialogRoot = loader.load();
        RuleDialogController dialog = loader.getController();
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.getIcons().add(icon);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(dialogRoot));
        dialog.setup(oldRule, kb);
        dialogStage.showAndWait();
        return dialog.getRule();
    }

    private void removeFact(ListView<Fact> factView) {
        factView.getItems().remove(factView.getSelectionModel().getSelectedIndex());
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Элементы управления">
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextArea reasonTextArea;
    @FXML
    private TextArea commentTextArea;
    @FXML
    private Button addPremiseBtn;
    @FXML
    private Button editPremiseBtn;
    @FXML
    private Button removePremiseBtn;
    @FXML
    private ListView<Fact> premisesListView;
    @FXML
    private Button addConclusionBtn;
    @FXML
    private Button editConclusionBtn;
    @FXML
    private Button removeConclusionBtn;
    @FXML
    private ListView<Fact> conclusionsListView;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Обработчики событий">
    @FXML
    void addConclusionBtn_OnAction(ActionEvent event) {
        addFact(conclusionsListView, Collections.singletonList(Classes.REQUESTED));
    }

    @FXML
    void addPremiseBtn_OnAction(ActionEvent event) {
        addFact(premisesListView, Collections.emptyList());
    }

    @FXML
    void cancelButton_OnAction(ActionEvent event) {
        close();
    }

    @FXML
    void editConclusionBtn_OnAction(ActionEvent event) {
        editFact(conclusionsListView, Collections.singletonList(Classes.REQUESTED));
    }

    @FXML
    void editPremiseBtn_OnAction(ActionEvent event) {
        editFact(premisesListView, Collections.emptyList());
    }

    @FXML
    void okButton_OnAction(ActionEvent event) {
        rule.setName(nameTextField.getText());
        rule.setReason(reasonTextArea.getText());
        rule.setComment(commentTextArea.getText());
        rule.getPremises().clear();
        rule.getPremises().addAll(premisesListView.getItems());
        rule.getConclusions().clear();
        rule.getConclusions().addAll(premisesListView.getItems());
        close();
    }

    @FXML
    void removeConclusionBtn_OnAction(ActionEvent event) {
        removeFact(conclusionsListView);
    }

    @FXML
    void removePremiseBtn_OnAction(ActionEvent event) {
        removeFact(premisesListView);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Инициализация">
    @FXML
    void initialize() {
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'ruledialog.fxml'.";
        assert reasonTextArea != null : "fx:id=\"reasonTextArea\" was not injected: check your FXML file 'ruledialog.fxml'.";
        assert commentTextArea != null : "fx:id=\"commentTextArea\" was not injected: check your FXML file 'ruledialog.fxml'.";
        assert addPremiseBtn != null : "fx:id=\"addPremiseBtn\" was not injected: check your FXML file 'ruledialog.fxml'.";
        assert editPremiseBtn != null : "fx:id=\"editPremiseBtn\" was not injected: check your FXML file 'ruledialog.fxml'.";
        assert removePremiseBtn != null : "fx:id=\"removePremiseBtn\" was not injected: check your FXML file 'ruledialog.fxml'.";
        assert premisesListView != null : "fx:id=\"premisesListView\" was not injected: check your FXML file 'ruledialog.fxml'.";
        assert addConclusionBtn != null : "fx:id=\"addConclusionBtn\" was not injected: check your FXML file 'ruledialog.fxml'.";
        assert editConclusionBtn != null : "fx:id=\"editConclusionBtn\" was not injected: check your FXML file 'ruledialog.fxml'.";
        assert removeConclusionBtn != null : "fx:id=\"removeConclusionBtn\" was not injected: check your FXML file 'ruledialog.fxml'.";
        assert conclusionsListView != null : "fx:id=\"conclusionsListView\" was not injected: check your FXML file 'ruledialog.fxml'.";
        assert okButton != null : "fx:id=\"okButton\" was not injected: check your FXML file 'ruledialog.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'ruledialog.fxml'.";

        nameTextField.textProperty().addListener((observableValue, oldName, newName) -> disableOkButton(newName));

        addImage = new Image("/icons/newItem.png");
        editImage = new Image("/icons/editItem.png");
    }
    //</editor-fold>
}
