package expertshellgui;

import base.domains.Domain;
import base.knowledgebase.KnowledgeBase;
import base.variables.Types;
import base.variables.Variable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.UUID;

public class VariableDialogController {
    private KnowledgeBase kb;
    private Types varClass;
    private Variable variable;
    private Image addImage;

    //<editor-fold defaultstate="collapsed" desc="Вспомогательные методы">
    static Variable showAndWait(Variable oldVariable, String title, Image icon, KnowledgeBase kb,
                                ResourceBundle resources, boolean forbidRequestedVars) throws IOException {
        FXMLLoader loader = new FXMLLoader(DomainDialogController.class.getResource("/fxml/variabledialog.fxml"),
                resources);
        Parent dialogRoot = loader.load();
        VariableDialogController dialog = loader.getController();
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.getIcons().add(icon);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(dialogRoot));
        dialog.setup(oldVariable, kb, forbidRequestedVars);
        dialogStage.showAndWait();
        return dialog.getVariable();
    }
    private void close() { ((Stage)okButton.getScene().getWindow()).close(); }

    private void disableOkButton(String name, String question) {
        okButton.setDisable(name == null ||
                name.trim().isEmpty() || !dedVarRB.isSelected() && (question == null || question.trim().isEmpty()) ||
                        kb.getVariables().stream().anyMatch(var ->
                                !var.getGuid().equals(variable.getGuid()) && var.getName().equals(name)) ||
                        domainComboBox.getSelectionModel().getSelectedIndex() == -1
        );
    }

    private Variable getVariable() { return variable; }

    private void setup(Variable oldVariable, KnowledgeBase kb, boolean forbidRequestedVars) {
        this.variable = oldVariable;
        this.kb = kb;
        reqVarRB.setDisable(forbidRequestedVars);
        nameTextField.setText(oldVariable.getName());
        switch (variable.getVarClass()) {
            case REQUESTED:
                reqVarRB.setSelected(true);
                break;
            case DEDUCTED:
                dedVarRB.setSelected(true);
                break;
            case REQUESTED_DEDUCTED:
                reqDedVarRB.setSelected(true);
                break;
            case DEDUCTED_REQUESTED:
                dedReqVarRB.setSelected(true);
                break;
        }
        labelTextField.setText(oldVariable.getLabel());
        domainComboBox.getItems().addAll(kb.getUsedDomains());
        domainComboBox.getSelectionModel().select(oldVariable.getDomain());
        questionTextArea.setText(oldVariable.getQuestion());
        autoQuestionChkbox.setSelected(false);
        if (varClass != Types.DEDUCTED && variable.getQuestion() == null){
            autoQuestionChkbox.setSelected(true);
        }
    }

    private void varTypeChanged(Types newType) {
        Types oldType = varClass;
        varClass = newType;
        if (newType == Types.DEDUCTED){
            autoQuestionChkbox.setDisable(true);
            questionTextArea.clear();
            questionTextArea.setDisable(true);
            disableOkButton(nameTextField.getText(), questionTextArea.getText());
        }
        else {
            if (oldType == null || oldType == Types.DEDUCTED) {
                autoQuestionChkbox.setDisable(false);
                if (!autoQuestionChkbox.isSelected())
                    autoQuestionChkbox.setSelected(true);
                else questionTextArea.setText(String.format("%s?", nameTextField.getText()));
                questionTextArea.setEditable(false);
            }
            disableOkButton(nameTextField.getText(), questionTextArea.getText());
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Элементы управления">
    @FXML
    private ResourceBundle resources;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField labelTextField;
    @FXML
    public ToggleGroup typesGroup;
    @FXML
    private RadioButton reqVarRB;
    @FXML
    private RadioButton reqDedVarRB;
    @FXML
    private RadioButton dedVarRB;
    @FXML
    private RadioButton dedReqVarRB;
    @FXML
    private Button addButton;
    @FXML
    private ComboBox<Domain> domainComboBox;
    @FXML
    private CheckBox autoQuestionChkbox;
    @FXML
    private TextArea questionTextArea;
    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Обработчики событий">
    @FXML
    void addButton_OnAction(ActionEvent event) {
        Domain newDomain = null;
        try {
            newDomain = DomainDialogController.showAndWait(new Domain(UUID.randomUUID(),
                            nameTextField.getText()), resources.getString("addDomain"),
                    addImage, kb, resources);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (newDomain == null)
            return;
        domainComboBox.getItems().add(newDomain);
        domainComboBox.getSelectionModel().selectLast();
    }

    @FXML
    void cancelButton_OnAction(ActionEvent event) {
        variable = null;
        close();
    }

    @FXML
    void okButton_OnAction(ActionEvent event) {
        variable.setName(nameTextField.getText());
        variable.setLabel(labelTextField.getText());
        variable.setVarClass(varClass);
        variable.setDomain(domainComboBox.getValue());
        variable.setQuestion(questionTextArea.getText());
        kb.getUsedDomains().clear();
        kb.getUsedDomains().addAll(domainComboBox.getItems());
        close();
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Инициализация">
    @FXML
    void initialize() {
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'variabledialog.fxml'.";
        assert labelTextField != null : "fx:id=\"labelTextField\" was not injected: check your FXML file 'variabledialog.fxml'.";
        assert addButton != null : "fx:id=\"addButton\" was not injected: check your FXML file 'variabledialog.fxml'.";
        assert domainComboBox != null : "fx:id=\"domainComboBox\" was not injected: check your FXML file 'variabledialog.fxml'.";
        assert okButton != null : "fx:id=\"okButton\" was not injected: check your FXML file 'variabledialog.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'variabledialog.fxml'.";
        nameTextField.textProperty().addListener((observableValue, oldName, newName) -> {
            if (autoQuestionChkbox.isSelected())
                questionTextArea.setText(String.format("%s?", newName));
            disableOkButton(newName, questionTextArea.getText());
        });
        labelTextField.textProperty().addListener((observableValue, oldLabel, newLabel) -> {
            disableOkButton(nameTextField.getText(), questionTextArea.getText());
        });
        reqVarRB.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue)
                varTypeChanged(Types.REQUESTED);
        });
        dedVarRB.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue)
                varTypeChanged(Types.DEDUCTED);
        });
        reqDedVarRB.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue)
                varTypeChanged(Types.REQUESTED_DEDUCTED);
        });
        dedReqVarRB.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue)
                varTypeChanged(Types.DEDUCTED_REQUESTED);
        });
        domainComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
                    disableOkButton(nameTextField.getText(), questionTextArea.getText());
                });
        domainComboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Domain> call(ListView<Domain> domainListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Domain item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty)
                            setGraphic(null);
                        else
                            setText(item.getName());
                    }
                };
            }
        });
        autoQuestionChkbox.selectedProperty().addListener((observableValue, wasChecked, nowChecked) -> {
            questionTextArea.setDisable(nowChecked);
            questionTextArea.setEditable(!nowChecked);
            if (nowChecked)
                questionTextArea.setText(String.format("%s?", nameTextField.getText()));
        });
        questionTextArea.textProperty().addListener((observableValue, oldValue, newValue) ->
                disableOkButton(nameTextField.getText(), newValue));
        addImage = new Image("/icons/newItem.png");
    }
    //</editor-fold>
}
