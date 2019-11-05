package expertshellgui;

import base.knowledgebase.KnowledgeBase;
import base.variables.Types;
import base.variables.Variable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GoalDialogController {
    private Variable goal;

    //<editor-fold defaultstate="collapsed" desc="Вспомогательные методы">
    private void close() { ((Stage)okBtn.getScene().getWindow()).close(); }

    private Variable getGoal() { return goal; }

    private void setup(Variable goal, KnowledgeBase kb) {
        this.goal = goal;
        varsCombobox.getItems().addAll(kb.getVariables().stream()
                .filter(var -> var.getVarClass() != Types.REQUESTED).collect(Collectors.toList()));
        varsCombobox.getSelectionModel().select(goal);
    }

    static Variable showAndWait(Variable oldGoal, String title,
                              KnowledgeBase kb, ResourceBundle resources) throws IOException {
        FXMLLoader loader = new FXMLLoader(DomainDialogController.class.getResource("/fxml/goaldialog.fxml"),
                resources);
        Parent dialogRoot = loader.load();
        GoalDialogController dialog = loader.getController();
        Stage dialogStage = new Stage();
        dialogStage.setResizable(false);
        dialogStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                dialogStage.setMaximized(false);
        });
        dialogStage.setTitle(title);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(dialogRoot));
        dialog.setup(oldGoal, kb);
        dialogStage.showAndWait();
        return dialog.getGoal();
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Элементы управления">
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private ComboBox<Variable> varsCombobox;
    @FXML
    private Button resetBtn;
    @FXML
    private Button okBtn;
    @FXML
    private Button cancelBtn;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Обработчики событий">
    @FXML
    void cancelBtn_OnAction(ActionEvent event) {
        close();
    }

    @FXML
    void okBtn_OnAction(ActionEvent event) {
        goal = varsCombobox.getSelectionModel().getSelectedItem();
        close();
    }

    @FXML
    void resetBtn_OnAction(ActionEvent event) {
        varsCombobox.getSelectionModel().select(null);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Инициализация">
    @FXML
    void initialize() {
        assert varsCombobox != null : "fx:id=\"varsCombobox\" was not injected: check your FXML file 'goaldialog.fxml'.";
        assert resetBtn != null : "fx:id=\"resetBtn\" was not injected: check your FXML file 'goaldialog.fxml'.";
        assert okBtn != null : "fx:id=\"okBtn\" was not injected: check your FXML file 'goaldialog.fxml'.";
        assert cancelBtn != null : "fx:id=\"cancelBtn\" was not injected: check your FXML file 'goaldialog.fxml'.";
    }
    //</editor-fold>
}
