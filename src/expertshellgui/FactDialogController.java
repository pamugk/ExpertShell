package expertshellgui;

import base.knowledgebase.KnowledgeBase;
import base.rules.Fact;
import base.rules.Assignable;
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

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class FactDialogController {
    private Fact fact;
    private KnowledgeBase kb;
    private List<Types> restrictedClasses;
    private Image addImage;
    private Image editImage;
    private boolean isPremise;

    //<editor-fold defaultstate="collapsed" desc="Вспомогательные методы">
    private void addVariable(ComboBox<? super Variable> varCombo, List<Types> restrictedClasses) {
        Variable newVariable;
        try {
            newVariable = VariableDialogController.showAndWait(new Variable(UUID.randomUUID(),
                            resources.getString("newVariable"), null, Types.REQUESTED),
                    resources.getString("addVariable"), addImage, kb, resources,
                    Arrays.stream(Types.values()).filter(clss -> !restrictedClasses.contains(clss))
                            .collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (newVariable == null)
            return;
        varCombo.getItems().add(newVariable);
        varCombo.getSelectionModel().selectLast();
    }

    private void close() {
        ((Stage) okBtn.getScene().getWindow()).close();
    }

    private void disableOkButton() {
        okBtn.setDisable(leftVarCombobox.getSelectionModel().getSelectedIndex() == -1 ||
                sourceDomainRadiobtn.isSelected() && rightFactableCombobox.getSelectionModel().getSelectedIndex() == -1 ||
                !sourceDomainRadiobtn.isSelected() && rightFactableCombobox.getSelectionModel().getSelectedIndex() == -1);
    }

    private void editVariable(ComboBox<? super Variable> varCombo, List<Types> restrictedClasses) {
        int idx = varCombo.getSelectionModel().getSelectedIndex();
        Variable editedVariable;
        try {
            editedVariable = VariableDialogController.showAndWait(
                    (Variable) varCombo.getItems().get(idx), resources.getString("editVariable"),
                    editImage, kb, resources,
                    Arrays.stream(Types.values()).filter(clss -> !restrictedClasses.contains(clss))
                            .collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (editedVariable == null)
            return;
        varCombo.getItems().set(idx, editedVariable);
        varCombo.getSelectionModel().select(idx);
    }

    private Fact getFact() {
        return fact;
    }

    private void leftVarSelectionChanged(Variable newVar) {
        if (sourceVariableRadiobtn.isSelected())
            sourceDomainRadiobtn.setSelected(true);
        else sourceChanged(true);
        disableOkButton();
    }

    private void rightFactableSelectionChanged(Assignable newAssignable) {
        editRightVarBtn.setDisable(sourceDomainRadiobtn.isSelected() || newAssignable == null);
        disableOkButton();
    }

    private void setup(Fact oldFact, KnowledgeBase kb, List<Types> restrictedClasses) {
        this.fact = oldFact;
        this.kb = kb;
        this.restrictedClasses = restrictedClasses;
        leftVarCombobox.getItems().setAll(kb.getVariables().stream().filter(
                variable -> !restrictedClasses.contains(variable.getVarClass())).collect(Collectors.toList()));
        leftVarCombobox.getSelectionModel().select(oldFact.getVariable());
        rightFactableCombobox.getSelectionModel().select(oldFact.getAssignable());
    }

    static Fact showAndWait(Fact oldFact, String title, Image icon, KnowledgeBase kb, ResourceBundle resources,
                            List<Types> restrictedClasses, boolean isPremise) throws IOException {
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
        if (isPremise) {
            dialog.leftVarLabel.setText(resources.getString("comparedVar"));
            dialog.rightVarLabel.setText(resources.getString("comparedVal"));
        }
        else {
            dialog.leftVarLabel.setText(resources.getString("changedVar"));
            dialog.rightVarLabel.setText(resources.getString("assignedVal"));
        }
        dialogStage.showAndWait();
        return dialog.getFact();
    }

    private void sourceChanged(boolean isDomain) {
        rightFactableCombobox.getItems().clear();
        editRightVarBtn.setDisable(true);
        addRightVarBtn.setDisable(isDomain);
        Variable variable = leftVarCombobox.getSelectionModel().getSelectedItem();
        if (isDomain) {
            addRightVarBtn.getTooltip().setText(resources.getString("addDomain"));
            editRightVarBtn.getTooltip().setText(resources.getString("editDomain"));
            if (variable != null)
                rightFactableCombobox.getItems().addAll(variable.getDomain().getValues());
        }
        else{
            addRightVarBtn.getTooltip().setText(resources.getString("addVariable"));
            editRightVarBtn.getTooltip().setText(resources.getString("editVariable"));
            if (variable != null) {
                rightFactableCombobox.getItems().addAll(kb.getVariables());
                rightFactableCombobox.getItems().remove(variable);
            }
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
    protected Label leftVarLabel;
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
    private Button editRightVarBtn;
    @FXML
    private RadioButton sourceDomainRadiobtn;
    @FXML
    private RadioButton sourceVariableRadiobtn;
    @FXML
    private ComboBox<Assignable> rightFactableCombobox;
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
            addVariable(rightFactableCombobox, Collections.emptyList());
    }

    @FXML
    void cancelBtn_OnAction(ActionEvent event) {
        fact = null;
        close();
    }

    @FXML
    void editRightVarBtn_OnAction(ActionEvent event) {
        if (sourceVariableRadiobtn.isSelected())
            editVariable(rightFactableCombobox, Collections.emptyList());
    }

    @FXML
    void okBtn_OnAction(ActionEvent event) {
        fact.setVariable(leftVarCombobox.getSelectionModel().getSelectedItem());
        leftVarCombobox.getItems().forEach(variable -> {
            if(!kb.getVariables().contains(variable))
                kb.getVariables().add(variable);
        });
        fact.setAssignable(rightFactableCombobox.getSelectionModel().getSelectedItem());
        if (sourceVariableRadiobtn.isSelected()) {
            rightFactableCombobox.getItems().forEach(variable -> {
                if (!kb.getVariables().contains(variable))
                    kb.getVariables().add((Variable) variable);
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
        assert rightFactableCombobox != null : "fx:id=\"rightVarCombobox\" was not injected: check your FXML file 'factdialog.fxml'.";
        assert editRightVarBtn != null : "fx:id=\"editRightVarBtn\" was not injected: check your FXML file 'factdialog.fxml'.";
        assert sourceDomainRadiobtn != null : "fx:id=\"sourceDomainChckbx\" was not injected: check your FXML file 'factdialog.fxml'.";
        assert source != null : "fx:id=\"source\" was not injected: check your FXML file 'factdialog.fxml'.";
        assert okBtn != null : "fx:id=\"okBtn\" was not injected: check your FXML file 'factdialog.fxml'.";
        assert cancelBtn != null : "fx:id=\"cancelBtn\" was not injected: check your FXML file 'factdialog.fxml'.";

        addImage = new Image("/icons/newItem.png");
        editImage = new Image("/icons/editItem.png");

        leftVarCombobox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVar, newVar) ->
                leftVarSelectionChanged(newVar));

        rightFactableCombobox.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldAssignable, newAssignable) -> rightFactableSelectionChanged(newAssignable));

        sourceDomainRadiobtn.selectedProperty().addListener((observableValue, oldVal, newVal) -> sourceChanged(newVal));
        //</editor-fold>
    }
}
