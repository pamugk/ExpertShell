package expertshellgui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeView;

public class ReasoningDialogController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TreeView<?> activatedRulesTreeView;

    @FXML
    private ListView<?> scrattchStorageLV;

    @FXML
    void initialize() {
        assert activatedRulesTreeView != null : "fx:id=\"activatedRulesTreeView\" was not injected: check your FXML file 'reasoningdialog.fxml'.";
        assert scrattchStorageLV != null : "fx:id=\"scrattchStorageLV\" was not injected: check your FXML file 'reasoningdialog.fxml'.";

    }
}
