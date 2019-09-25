package expertshellgui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class RuleDialogController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField commentTextField;

    @FXML
    private TextField reasonTextField;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @FXML
    void initialize() {
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'ruledialog.fxml'.";
        assert commentTextField != null : "fx:id=\"commentTextField\" was not injected: check your FXML file 'ruledialog.fxml'.";
        assert reasonTextField != null : "fx:id=\"reasonTextField\" was not injected: check your FXML file 'ruledialog.fxml'.";
        assert okButton != null : "fx:id=\"okButton\" was not injected: check your FXML file 'ruledialog.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'ruledialog.fxml'.";

    }
}
