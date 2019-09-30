package expertshellgui;

import base.domains.Value;
import base.knowledgebase.KnowledgeBase;
import base.rules.Fact;
import base.variables.Classes;
import base.variables.Variable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class FactDialogController {
    private Fact fact;
    private KnowledgeBase kb;
    private List<Classes> restrictedClasses;
    private Image addImage;
    private Image editImage;

    //<editor-fold defaultstate="collapsed" desc="Вспомогательные методы">
    private void addVariable(ComboBox<Variable> varCombo, List<Classes> restrictedClasses) {
        Variable newVariable;
        try {
            newVariable = VariableDialogController.showAndWait(new Variable(UUID.randomUUID(),
                            resources.getString("newVariable"), null, Classes.REQUESTED),
                    resources.getString("addVariable"), addImage, kb, resources,
                    Arrays.stream(Classes.values()).filter(clss -> !restrictedClasses.contains(clss))
                            .collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        varCombo.getItems().add(newVariable);
        varCombo.getSelectionModel().selectLast();
    }

    private void close() {
        ((Stage) okBtn.getScene().getWindow()).close();
    }

    private void disableOkButton() {
        okBtn.setDisable(leftVarCombobox.getSelectionModel().getSelectedIndex() == -1 ||
                sourceDomainRadiobtn.isSelected() && rightValueCombobox.getSelectionModel().getSelectedIndex() == -1 ||
                !sourceDomainRadiobtn.isSelected() && rightVarCombobox.getSelectionModel().getSelectedIndex() == -1);
    }

    private void editVariable(ComboBox<Variable> varCombo, List<Classes> restrictedClasses) {
        int idx = varCombo.getSelectionModel().getSelectedIndex();
        Variable editedVariable;
        try {
            editedVariable = VariableDialogController.showAndWait(
                    varCombo.getItems().get(idx), resources.getString("editVariable"),
                    editImage, kb, resources,
                    Arrays.stream(Classes.values()).filter(clss -> !restrictedClasses.contains(clss))
                            .collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        varCombo.getItems().set(idx, editedVariable);
        varCombo.getSelectionModel().select(idx);
    }

    private Fact getFact() {
        return fact;
    }

    private void leftVarSelectionChanged(Variable newVar) {
        rightValueCombobox.getItems().clear();
        rightValueCombobox.getItems().addAll(newVar.getDomain().getValues());
        disableOkButton();
    }

    private void rightValSelectionChanged(Value newValue) {
        disableOkButton();
    }

    private void rightVarSelectionChanged(Variable newVar) {
        editRightVarBtn.setDisable(sourceVariableRadiobtn.isSelected() && newVar == null);
        disableOkButton();
    }

    private void setup(Fact oldFact, KnowledgeBase kb, List<Classes> restrictedClasses) {
        this.fact = oldFact;
        this.kb = kb;
        this.restrictedClasses = restrictedClasses;
    }

    static Fact showAndWait(Fact oldFact, String title, Image icon, KnowledgeBase kb, ResourceBundle resources,
                            List<Classes> restrictedClasses) throws IOException {
        FXMLLoader loader = new FXMLLoader(DomainDialogController.class.getResource("/fxml/factdialog.fxml"),
                resources);
        Parent dialogRoot = loader.load();
        FactDialogController dialog = loader.getController();
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.getIcons().add(icon);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(dialogRoot));
        dialog.setup(oldFact, kb, restrictedClasses);
        dialogStage.showAndWait();
        return dialog.getFact();
    }

    private void sourceChanged(boolean isDomain) {
        selectionHbox.getChildren().clear();
        if (isDomain) {
            selectionHbox.getChildren().add(rightValueCombobox);
            addRightVarBtn.getTooltip().setText(resources.getString("addDomain"));
            addRightVarBtn.setDisable(true);
            editRightVarBtn.getTooltip().setText(resources.getString("editDomain"));
            editRightVarBtn.setDisable(true);
        }
        else{
            selectionHbox.getChildren().add(rightVarCombobox);
            addRightVarBtn.getTooltip().setText(resources.getString("addVariable"));
            addRightVarBtn.setDisable(false);
            editRightVarBtn.getTooltip().setText(resources.getString("editVariable"));
            addRightVarBtn.setDisable(rightVarCombobox.getSelectionModel().getSelectedIndex() != -1);
        }
        disableOkButton();
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Элементы управления">
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label leftVarLabel;

    @FXML
    private Button addLeftVarBtn;

    @FXML
    private ComboBox<Variable> leftVarCombobox;

    @FXML
    private Button editLeftVarBtn;

    @FXML
    private Label rightVarLabel;

    @FXML
    private Button addRightVarBtn;

    @FXML
    private HBox selectionHbox;

    private ComboBox<Variable> rightVarCombobox;
    private ComboBox<Value> rightValueCombobox;

    @FXML
    private Button editRightVarBtn;

    @FXML
    private RadioButton sourceDomainRadiobtn;

    @FXML
    private RadioButton sourceVariableRadiobtn;

    @FXML
    private ToggleGroup source;

    @FXML
    private Button okBtn;

    @FXML
    private Button cancelBtn;

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Обработчики событий">
    @FXML
    void addLeftVarBtn_OnAction(ActionEvent event) {
        addVariable(leftVarCombobox, restrictedClasses);
    }

    @FXML
    void addRightVarBtn_OnAction(ActionEvent event) {
        if (sourceVariableRadiobtn.isSelected())
            addVariable(rightVarCombobox, Collections.emptyList());
    }

    @FXML
    void cancelBtn_OnAction(ActionEvent event) {
        close();
    }

    @FXML
    void editRightVarBtn_OnAction(ActionEvent event) {
        if (sourceVariableRadiobtn.isSelected())
            editVariable(rightVarCombobox, Collections.emptyList());
    }

    @FXML
    void okBtn_OnAction(ActionEvent event) {
        fact.setVariable(leftVarCombobox.getSelectionModel().getSelectedItem());
        leftVarCombobox.getItems().forEach(variable -> {
            if(!kb.getVariables().contains(variable))
                kb.getVariables().add(variable);
        });
        if (sourceDomainRadiobtn.isSelected()) {
            fact.setAssignedValue(rightValueCombobox.getSelectionModel().getSelectedItem());
            fact.setAssignedVariable(null);
        } else {
            fact.setAssignedValue(null);
            fact.setAssignedVariable(rightVarCombobox.getSelectionModel().getSelectedItem());
            rightVarCombobox.getItems().forEach(variable -> {
                if(!kb.getVariables().contains(variable))
                    kb.getVariables().add(variable);
            });
        }
        close();
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Инициализация">
    @FXML
    void initialize() {
        assert leftVarLabel != null : "fx:id=\"leftVarLabel\" was not injected: check your FXML file 'factdialog.fxml'.";
        assert addLeftVarBtn != null : "fx:id=\"addLeftVarBtn\" was not injected: check your FXML file 'factdialog.fxml'.";
        assert leftVarCombobox != null : "fx:id=\"leftVarCombobox\" was not injected: check your FXML file 'factdialog.fxml'.";
        assert editLeftVarBtn != null : "fx:id=\"editLeftVarBtn\" was not injected: check your FXML file 'factdialog.fxml'.";
        assert rightVarLabel != null : "fx:id=\"rightVarLabel\" was not injected: check your FXML file 'factdialog.fxml'.";
        assert addRightVarBtn != null : "fx:id=\"addRightVarBtn\" was not injected: check your FXML file 'factdialog.fxml'.";
        assert rightVarCombobox != null : "fx:id=\"rightVarCombobox\" was not injected: check your FXML file 'factdialog.fxml'.";
        assert editRightVarBtn != null : "fx:id=\"editRightVarBtn\" was not injected: check your FXML file 'factdialog.fxml'.";
        assert sourceDomainRadiobtn != null : "fx:id=\"sourceDomainChckbx\" was not injected: check your FXML file 'factdialog.fxml'.";
        assert source != null : "fx:id=\"source\" was not injected: check your FXML file 'factdialog.fxml'.";
        assert okBtn != null : "fx:id=\"okBtn\" was not injected: check your FXML file 'factdialog.fxml'.";
        assert cancelBtn != null : "fx:id=\"cancelBtn\" was not injected: check your FXML file 'factdialog.fxml'.";

        addImage = new Image("/icons/newItem.png");
        editImage = new Image("/icons/editItem.png");

        leftVarCombobox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVar, newVar) ->
                leftVarSelectionChanged(newVar));

        rightValueCombobox = new ComboBox<>();
        rightValueCombobox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) ->
                rightValSelectionChanged(newVal));

        rightVarCombobox = new ComboBox<>();
        rightVarCombobox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVar, newVar) ->
                rightVarSelectionChanged(newVar));

        sourceDomainRadiobtn.selectedProperty().addListener((observableValue, oldVal, newVal) -> sourceChanged(newVal));
        //</editor-fold>
    }
}
