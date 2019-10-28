import expertshellgui.ExpertShellController;
import expertsystem.ExpertSystem;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import transfer.BinKnowledgeBaseExporter;
import transfer.BinKnowledgeBaseImporter;

import java.util.ResourceBundle;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        ExpertShellController.show(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
