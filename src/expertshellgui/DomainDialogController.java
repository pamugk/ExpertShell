package expertshellgui;

import base.domains.Domain;
import base.domains.Value;
import base.knowledgebase.KnowledgeBase;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class DomainDialogController {
    private Domain domain;
    private KnowledgeBase kb;

    static Domain showAndWait(Domain oldDomain, String title, Image icon,
                              KnowledgeBase kb, ResourceBundle resources) throws IOException {
        FXMLLoader loader = new FXMLLoader(DomainDialogController.class.getResource("/fxml/domaindialog.fxml"),
                resources);
        Parent dialogRoot = loader.load();
        DomainDialogController dialog = loader.getController();
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.getIcons().add(icon);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(dialogRoot));
        dialog.setup(oldDomain, kb);
        dialogStage.showAndWait();
        return dialog.getDomain();
    }

    //<editor-fold defaultstate="collapsed" desc="Вспомогательные методы">
    private void close() { ((Stage)okButton.getScene().getWindow()).close(); }

    private void disableOkButton(String newName) {
        okButton.setDisable(newName == null || newName.trim().equals("")
                || kb.getUsedDomains().stream().anyMatch(someDomain ->
                !someDomain.getGuid().equals(domain.getGuid()) && someDomain.getName().equals(domain.getName()))
                || valuesListView.getItems().size() == 0);
    }

    private Domain getDomain() { return domain; }

    private void setup(Domain domain, KnowledgeBase kb) {
        this.domain = domain;
        nameTextField.setText(domain.getName());
        valuesListView.getItems().addAll(domain.getValues());
        this.kb = kb;
    }

    private boolean showValueDialog(Value oldValue, String title, String header) {
        TextInputDialog valueDialog = new TextInputDialog(oldValue.getContent());
        Button okButton = (Button) valueDialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(valuesListView.getItems().stream().anyMatch(value -> !value.getGuid().equals(oldValue.getGuid())
                && value.getContent().equals(oldValue.getContent())));
        valueDialog.getEditor().textProperty().addListener(((observableValue, oldName, newName) ->
                okButton.setDisable(newName.trim().equals("") ||
                    valuesListView.getItems().stream().anyMatch(value -> !value.getGuid().equals(oldValue.getGuid())
                            && value.getContent().equals(newName)))
        ));
        valueDialog.setTitle(title);
        valueDialog.setHeaderText(header);
        Optional<String> content = valueDialog.showAndWait();
        AtomicBoolean success = new AtomicBoolean(false);
        content.ifPresent(cont -> {
            oldValue.setContent(cont);
            success.set(true);
        });
        return success.get();
    }

    private void valuesSelectedIndexChanged(ObservableValue<? extends Number> observableValue, Number oldValue,
        Number newValue) {
        boolean disable = newValue.intValue() == -1;
        editButton.setDisable(disable);
        removeButton.setDisable(disable);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Элементы управления">
    @FXML
    private ResourceBundle resources;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField nameTextField;
    @FXML
    private Label valuesLabel;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button removeButton;
    @FXML
    private ListView<Value> valuesListView;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Обработчики событий">
    @FXML
    void addButton_OnAction(ActionEvent event) {
        Value newValue = new Value(UUID.randomUUID(), resources.getString("newValue"));
        if (showValueDialog(newValue, resources.getString("newValue"), resources.getString("newValue"))){
            int currentIdx = valuesListView.getSelectionModel().getSelectedIndex();
            if (currentIdx == -1)
                valuesListView.getItems().add(newValue);
            else
                valuesListView.getItems().add(currentIdx+1, newValue);
            disableOkButton(nameTextField.getText());
        }
    }

    @FXML
    void cancelButton_OnAction(ActionEvent event) {
        domain = null;
        close();
    }

    @FXML
    void editButton_OnAction(ActionEvent event) {
        int idx = valuesListView.getSelectionModel().getSelectedIndex();
        var editedValue = valuesListView.getItems().get(idx);
        if (showValueDialog(editedValue, resources.getString("newValue"), resources.getString("newValue")))
            valuesListView.getItems().set(idx, editedValue);
        disableOkButton(nameTextField.getText());
    }

    @FXML
    void okButton_OnAction(ActionEvent event) {
        domain.setName(nameTextField.getText());
        domain.getValues().clear();
        domain.getValues().addAll(valuesListView.getItems());
        close();
    }

    @FXML
    void removeButton_OnAction(ActionEvent event) {
        valuesListView.getItems().remove(valuesListView.getSelectionModel().getSelectedIndex());
        disableOkButton(nameTextField.getText());
    }

    @FXML
    void valuesListView_OnMouseClicked(MouseEvent event) {

    }

    @FXML
    void valuesListView_OnMouseDragged(MouseEvent event) {

    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Инициализация">
    @FXML
    void initialize() {
        assert nameLabel != null : "fx:id=\"nameLabel\" was not injected: check your FXML file 'domaindialog.fxml'.";
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'domaindialog.fxml'.";
        assert valuesLabel != null : "fx:id=\"valuesLabel\" was not injected: check your FXML file 'domaindialog.fxml'.";
        assert addButton != null : "fx:id=\"addButton\" was not injected: check your FXML file 'domaindialog.fxml'.";
        assert editButton != null : "fx:id=\"editButton\" was not injected: check your FXML file 'domaindialog.fxml'.";
        assert removeButton != null : "fx:id=\"removeButton\" was not injected: check your FXML file 'domaindialog.fxml'.";
        assert valuesListView != null : "fx:id=\"valuesListView\" was not injected: check your FXML file 'domaindialog.fxml'.";
        assert okButton != null : "fx:id=\"okButton\" was not injected: check your FXML file 'domaindialog.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'domaindialog.fxml'.";

        nameTextField.textProperty().addListener(((observableValue, oldValue, newValue) -> disableOkButton(newValue)));
        valuesListView.getSelectionModel().selectedIndexProperty().addListener(this::valuesSelectedIndexChanged);
        valuesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Value item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else setText(item.getContent());
            }
        });
    }
    //</editor-fold>
}
