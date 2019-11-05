import expertshellgui.ExpertShellController;
import javafx.application.Application;
import javafx.stage.Stage;

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
