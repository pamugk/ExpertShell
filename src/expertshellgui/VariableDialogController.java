package expertshellgui;

import base.domains.Domain;
import base.knowledgebase.KnowledgeBase;
import base.variables.Classes;
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
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

import static java.util.Map.entry;

public class VariableDialogController {
    private KnowledgeBase kb;
    private Variable variable;
    private Map<Classes, String> types;
    private Image addImage;

    //<editor-fold defaultstate="collapsed" desc="Вспомогательные методы">
    static Variable showAndWait(Variable oldVariable, String title, Image icon, KnowledgeBase kb,
                                ResourceBundle resources, List<Classes> allowedClasses) throws IOException {
        FXMLLoader loader = new FXMLLoader(DomainDialogController.class.getResource("/fxml/variabledialog.fxml"),
                resources);
        Parent dialogRoot = loader.load();
        VariableDialogController dialog = loader.getController();
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.getIcons().add(icon);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(dialogRoot));
        dialog.setup(oldVariable, kb, allowedClasses);
        dialogStage.showAndWait();
        return dialog.getVariable();
    }
    private void close() { ((Stage)okButton.getScene().getWindow()).close(); }

    private void disableOkButton(String name, String question) {
        okButton.setDisable(name == null ||
                name.trim().isEmpty() || typeComboBox.getSelectionModel().getSelectedItem() != Classes.DEDUCTED
                        && (question == null || question.trim().isEmpty()) ||
                        kb.getVariables().stream().anyMatch(var ->
                        !var.getGuid().equals(variable.getGuid()) && var.getName().equals(name)) ||
                        domainComboBox.getSelectionModel().getSelectedIndex() == -1
        );
    }

    private Variable getVariable() { return variable; }

    private void setup(Variable oldVariable, KnowledgeBase kb, List<Classes> allowedClasses) {
        this.variable = oldVariable;
        this.kb = kb;
        typeComboBox.getItems().addAll(allowedClasses);
        nameTextField.setText(oldVariable.getName());
        labelTextField.setText(oldVariable.getLabel());
        typeComboBox.getSelectionModel().select(oldVariable.getVarClass());
        domainComboBox.getItems().addAll(kb.getUsedDomains());
        domainComboBox.getSelectionModel().select(oldVariable.getDomain());
        questionTextArea.setText(oldVariable.getQuestion());
        types = Map.ofEntries(
                entry(Classes.REQUESTED, resources.getString("varReq")),
                entry(Classes.DEDUCTED, resources.getString("varDed")),
                entry(Classes.REQUESTED_DEDUCTED, resources.getString("varRD")),
                entry(Classes.DEDUCTED_REQUESTED, resources.getString("varDR"))
        );
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
    private ComboBox<Classes> typeComboBox;
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
        variable.setVarClass(typeComboBox.getValue());
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
        assert typeComboBox != null : "fx:id=\"typeComboBox\" was not injected: check your FXML file 'variabledialog.fxml'.";
        assert addButton != null : "fx:id=\"addButton\" was not injected: check your FXML file 'variabledialog.fxml'.";
        assert domainComboBox != null : "fx:id=\"domainComboBox\" was not injected: check your FXML file 'variabledialog.fxml'.";
        assert okButton != null : "fx:id=\"okButton\" was not injected: check your FXML file 'variabledialog.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'variabledialog.fxml'.";
        nameTextField.textProperty().addListener((observableValue, oldName, newName) -> disableOkButton(newName,
                questionTextArea.getText()));
        typeComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
                disableOkButton(nameTextField.getText(), questionTextArea.getText());
                boolean disableQuestion = newValue == Classes.DEDUCTED;
                autoQuestionChkbox.setDisable(disableQuestion);
                if (disableQuestion)
                    questionTextArea.clear();
                else
                    if (oldValue == Classes.DEDUCTED)
                        autoQuestionChkbox.setSelected(true);
                questionTextArea.setDisable(disableQuestion);
        });
        typeComboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Classes> call(ListView<Classes> classesListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Classes item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty)
                            setGraphic(null);
                        else
                            setText(types.get(item));
                    }
                };
            }
        });
        domainComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) ->
                disableOkButton(nameTextField.getText(), questionTextArea.getText()));
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
            if (questionTextArea.getText().isEmpty())
                questionTextArea.setText(String.format("%s?", nameTextField.getText()));
        });
        questionTextArea.textProperty().addListener((observableValue, oldValue, newValue) ->
                disableOkButton(nameTextField.getText(), newValue));
        addImage = new Image("/icons/newItem.png");
    }
    //</editor-fold>
}
