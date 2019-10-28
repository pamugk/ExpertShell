package expertshellgui;

import base.knowledgebase.KnowledgeBase;
import base.rules.Fact;
import base.rules.Rule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class RuleDialogController {
    private KnowledgeBase kb;
    private Rule rule;
    private Image addImage;
    private Image editImage;
    private int draggedIdx;

    //<editor-fold defaultstate="collapsed" desc="Вспомогательные методы">
    private void addFact(ListView<Fact> factView, boolean isConclusion) {
        Fact newFact;
        try {
            newFact = FactDialogController.showAndWait(new Fact(), resources.getString("addFact"),
                    addImage, kb, resources, isConclusion);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (newFact == null)
            return;
        var currentIdx = factView.getSelectionModel().getSelectedIndex();
        int newIdx;
        if (currentIdx == -1) {
            factView.getItems().add(newFact);
            newIdx = factView.getItems().size() - 1;
        }
        else {
            newIdx = currentIdx + 1;
            factView.getItems().add(newIdx, newFact);
        }
        factView.getSelectionModel().select(newIdx);
        disableOkButton(nameTextField.getText());
    }

    private void close() { ((Stage)okButton.getScene().getWindow()).close(); }

    private void disableOkButton(String name) {
        okButton.setDisable(name == null || name.trim().isEmpty() || kb.getRules().stream().anyMatch(
                anyRule -> !anyRule.getGuid().equals(rule.getGuid()) && anyRule.getName().equals(name)) ||
                premisesListView.getItems().isEmpty() || conclusionsListView.getItems().isEmpty());
    }

    private void editFact(ListView<Fact> factView, boolean isConclusion) {
        int idx = factView.getSelectionModel().getSelectedIndex();
        Fact editedFact;
        try {
            editedFact = FactDialogController.showAndWait(
                    factView.getItems().get(idx), resources.getString("editFact"),
                    editImage, kb, resources, isConclusion);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (editedFact == null)
            return;
        factView.getItems().set(idx, editedFact);
    }

    private <T> ListCell<T> generateNewCell(ListView<T> listView) {
        ListCell<T> newCell = new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        };
        newCell.setOnMouseClicked(mouseEvent -> {
            if (newCell.isEmpty())
                listView.getSelectionModel().clearSelection();
        });
        implementDragAndDropForCell(newCell, listView);
        return newCell;
    }

    private Rule getRule() { return rule; }

    private <T> void implementDragAndDropForCell(ListCell<T> cell, ListView<T> listView) {
        cell.setOnDragDetected(mouseEvent -> {
            if (cell.getItem() == null) {
                return;
            }

            draggedIdx = cell.getIndex();
            Dragboard dragboard = cell.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(cell.getItem().toString());
            dragboard.setContent(content);

            mouseEvent.consume();
        });
        cell.setOnDragOver(dragEvent -> {
            if (dragEvent.getGestureSource() != cell &&
                    dragEvent.getDragboard().hasString()) {
                dragEvent.acceptTransferModes(TransferMode.MOVE);
            }
            dragEvent.consume();
        });
        cell.setOnDragEntered(dragEvent -> {
            if (dragEvent.getGestureSource() != cell &&
                    dragEvent.getDragboard().hasString()) {
                cell.setOpacity(0.3);
            }
        });
        cell.setOnDragExited(dragEvent -> {
            if (dragEvent.getGestureSource() != cell &&
                    dragEvent.getDragboard().hasString()) {
                cell.setOpacity(1);
            }
        });
        cell.setOnDragDropped(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                T draggedVal = listView.getItems().get(draggedIdx);
                int thisIdx =
                        cell.getIndex() < listView.getItems().size() ?
                                cell.getIndex() : listView.getItems().size()-1;
                for (int i = draggedIdx; i > thisIdx; i--)
                    listView.getItems().set(i, listView.getItems().get(i - 1));
                for (int i = draggedIdx; i < thisIdx; i++)
                    listView.getItems().set(i, listView.getItems().get(i + 1));
                listView.getItems().set(thisIdx, draggedVal);
                success = true;
                disableOkButton(nameTextField.getText());
            }
            dragEvent.setDropCompleted(success);

            dragEvent.consume();
        });
        cell.setOnDragDone(DragEvent::consume);
    }

    private void setup(Rule oldRule, KnowledgeBase kb) {
        this.rule = oldRule;
        this.kb = kb;
        nameTextField.setText(oldRule.getName());
        reasonTextArea.setText(oldRule.getReason());
        commentTextArea.setText(oldRule.getComment());
        premisesListView.getItems().addAll(oldRule.getPremises());
        conclusionsListView.getItems().addAll(oldRule.getConclusions());
    }

    private void selectedFactChanged(Number newIdx, Button editBtn, Button removeBtn) {
        boolean disable = newIdx.intValue() == -1;
        editBtn.setDisable(disable);
        removeBtn.setDisable(disable);
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
        disableOkButton(nameTextField.getText());
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Элементы управления">
    @FXML
    private ResourceBundle resources;
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
        addFact(conclusionsListView, true);
    }

    @FXML
    void addPremiseBtn_OnAction(ActionEvent event) {
        addFact(premisesListView, false);
    }

    @FXML
    void cancelButton_OnAction(ActionEvent event) {
        rule = null;
        close();
    }

    @FXML
    void editConclusionBtn_OnAction(ActionEvent event) {
        editFact(conclusionsListView, true);
    }

    @FXML
    void editPremiseBtn_OnAction(ActionEvent event) {
        editFact(premisesListView, false);
    }

    @FXML
    void okButton_OnAction(ActionEvent event) {
        rule.setName(nameTextField.getText());
        rule.setReason(reasonTextArea.getText());
        rule.setComment(commentTextArea.getText());
        rule.getPremises().clear();
        rule.getPremises().addAll(premisesListView.getItems());
        rule.getConclusions().clear();
        rule.getConclusions().addAll(conclusionsListView.getItems());
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
        commentTextArea.textProperty()
                .addListener((observableValue, oldText, newText) -> disableOkButton(nameTextField.getText()));
        reasonTextArea.textProperty().addListener((observableValue, oldText, newText) -> disableOkButton(nameTextField.getText()));

        premisesListView.setCellFactory(this::generateNewCell);
        premisesListView.getSelectionModel().selectedIndexProperty().addListener(
                (observableValue, oldIdx, newIdx) -> selectedFactChanged(newIdx, editPremiseBtn, removePremiseBtn));

        conclusionsListView.setCellFactory(this::generateNewCell);
        conclusionsListView.getSelectionModel().selectedIndexProperty().addListener(
                (observableValue, oldIdx, newIdx) -> selectedFactChanged(newIdx, editConclusionBtn, removeConclusionBtn));

        addImage = new Image("/icons/newItem.png");
        editImage = new Image("/icons/editItem.png");
    }
    //</editor-fold>
}
