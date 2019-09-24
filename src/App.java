import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {
    private static final String fxmlPath = "fxml/expertshellgui.fxml";
    private static final String localisationPath = "localisation/shellLocalisation";
    private static final String iconPath = "/icons/app.png";

    @Override
    public void start(Stage primaryStage) throws Exception{
        ResourceBundle bundle = ResourceBundle.getBundle(localisationPath);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath), bundle);

        Parent root = loader.load();
        primaryStage.setTitle(bundle.getString("title"));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(iconPath)));
        primaryStage.setScene(new Scene(root));

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
