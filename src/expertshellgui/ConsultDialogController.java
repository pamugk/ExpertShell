package expertshellgui;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import base.domains.Value;
import base.variables.Variable;
import expertsystem.ExpertSystem;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConsultDialogController {
    private ExpertSystem expertSystem;
    private CompletableFuture<Value> answer;

    private Value askQuestion(Variable variable) {
        Platform.runLater(() -> {
            answersCombobox.getItems().clear();
            answersCombobox.getItems().addAll(variable.getDomain().getValues());
            answersCombobox.getSelectionModel().select(0);
            consultingBox.getChildren().add(createQuestionLabel(variable.getQuestion()));
            interactionBox.setDisable(false);
        });
        answer = new CompletableFuture<>();

        Value awaitedAnswer = null;
        try {
            awaitedAnswer = answer.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Value finalAwaitedAnswer = awaitedAnswer;
        Platform.runLater(
            () -> {
                interactionBox.setDisable(true);
                consultingBox.getChildren().add(createAnswerLabel(finalAwaitedAnswer));
            }
        );

        return awaitedAnswer;
    }

    private Label createAnswerLabel(Value answer) {
        Label answerLabel = new Label();
        answerLabel.setMaxWidth(Double.MAX_VALUE);
        answerLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
        answerLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        answerLabel.setAlignment(Pos.CENTER_RIGHT);
        answerLabel.setPadding(new Insets(20));
        answerLabel.setWrapText(true);
        answerLabel.setText(answer == null ? resources.getString("idk") : answer.getContent());
        return answerLabel;
    }

    private Label createQuestionLabel(String question) {
        Label questionLabel = new Label();
        questionLabel.setMaxWidth(Double.MAX_VALUE);
        questionLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
        questionLabel.setBackground(new Background(new BackgroundFill(Color.GREY, null, null)));
        questionLabel.setAlignment(Pos.CENTER_LEFT);
        questionLabel.setPadding(new Insets(20));
        questionLabel.setWrapText(true);
        questionLabel.setText(question);
        return questionLabel;
    }

    private Label createResultLabel(Value value) {
        Label result = new Label();
        result.setStyle("-fx-text-alignment: left;");
        result.setPadding(new Insets(20));
        result.setText(value == null ?
                resources.getString("badConsulting") :
                String.format("%s: %s", resources.getString("consultingRes"), value));
        result.setWrapText(true);
        return result;
    }

    private void postResult(Value value) {
        Platform.runLater(() -> {
            mainBox.getChildren().remove(interactionBox);
            mainBox.getChildren().add(createResultLabel(value));
        });
    }

    private void setup(ExpertSystem expertSystem) {
        this.expertSystem = expertSystem;
        goalLabel.setText(String.format("%s: %s", goalLabel.getText(), expertSystem.getKnowledgeBase().getGoal()));
        this.expertSystem.setQuestioner(this::askQuestion);
    }

    static void showAndWait(String title, ExpertSystem expertSystem, ResourceBundle resources)
            throws IOException {
        FXMLLoader loader = new FXMLLoader(DomainDialogController.class.getResource("/fxml/consultdialog.fxml"),
                resources);
        Parent dialogRoot = loader.load();
        ConsultDialogController dialog = loader.getController();
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(dialogRoot));
        dialog.setup(expertSystem);
        boolean forget = false;
        if (!expertSystem.scratchStorageIsEmpty())
            forget = dialog.showForgetDialog();
        dialog.start(forget);
        dialogStage.showAndWait();
    }

    private boolean showForgetDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                resources.getString("forgetMsg"), ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(resources.getString("forget"));
        alert.setTitle(resources.getString("forgetTtl"));
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    private void start(boolean forgetPreviousConsulting) {
        if (forgetPreviousConsulting)
            expertSystem.forgetPreviousConsulting();
        new CompletableFuture<Value>().completeAsync(() -> expertSystem.consult()).thenAccept(this::postResult);
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private VBox mainBox;

    @FXML
    private Label goalLabel;

    @FXML
    private VBox consultingBox;

    @FXML
    private HBox interactionBox;

    @FXML
    private ComboBox<Value> answersCombobox;

    @FXML
    private Button answerBtn;

    @FXML
    private Button idkBtn;

    @FXML
    void answerBtn_OnAction(ActionEvent event) {
        answer.complete(answersCombobox.getSelectionModel().getSelectedItem());
    }

    @FXML
    void idkBtn_OnAction(ActionEvent event) {
        answer.complete(null);
    }

    @FXML
    void initialize() {
        assert mainBox != null : "fx:id=\"mainBox\" was not injected: check your FXML file 'consultdialog.fxml'.";
        assert goalLabel != null : "fx:id=\"goalLabel\" was not injected: check your FXML file 'consultdialog.fxml'.";
        assert consultingBox != null : "fx:id=\"consultingBox\" was not injected: check your FXML file 'consultdialog.fxml'.";
        assert interactionBox != null : "fx:id=\"interactionBox\" was not injected: check your FXML file 'consultdialog.fxml'.";
        assert answersCombobox != null : "fx:id=\"answersCombobox\" was not injected: check your FXML file 'consultdialog.fxml'.";
        assert answerBtn != null : "fx:id=\"answerBtn\" was not injected: check your FXML file 'consultdialog.fxml'.";
        assert idkBtn != null : "fx:id=\"idkBtn\" was not injected: check your FXML file 'consultdialog.fxml'.";
    }
}
