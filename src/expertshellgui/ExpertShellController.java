package expertshellgui;

import base.domains.Domain;
import base.knowledgebase.KnowledgeBase;
import base.rules.Fact;
import base.rules.Rule;
import base.variables.Classes;
import base.variables.Variable;
import expertsystem.ExpertSystem;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import transfer.interfaces.KnowledgeBaseExporter;
import transfer.interfaces.KnowledgeBaseImporter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ExpertShellController {
    //<editor-fold defaultstate="collapsed" desc="Вспомогательные методы">
    private ExpertSystem expertSystem;
    private KnowledgeBaseExporter kbExporter;
    private File kbFile;
    private KnowledgeBaseImporter kbImporter;
    private Stage stage;

    public ExpertSystem getExpertSystem() { return  expertSystem; }
    public void setExpertSystem(ExpertSystem expertSystem) { this.expertSystem = expertSystem; }

    public KnowledgeBaseExporter getKbExporter() { return kbExporter; }
    public void setKbExporter(KnowledgeBaseExporter kbExporter) { this.kbExporter = kbExporter; }

    public KnowledgeBaseImporter getKbImporter() { return kbImporter; }
    public void setKbImporter(KnowledgeBaseImporter kbImporter) { this.kbImporter = kbImporter; }

    public Stage getStage() { return stage; }
    public void setStage(Stage stage) { this.stage = stage; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Вспомогательные методы">
    private void addDomain() {

    }

    private void addRule() {

    }

    private void addVariable() {

    }

    private Optional<String> askKbName(String title, String header, String currentName) {
        TextInputDialog kbNameDialog = new TextInputDialog(currentName);
        TextField inputField = kbNameDialog.getEditor();
        BooleanBinding isInvalid = Bindings.createBooleanBinding(() ->
                inputField.getText().trim().equals(""), inputField.textProperty());
        kbNameDialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(isInvalid);
        kbNameDialog.setTitle(title);
        kbNameDialog.setHeaderText(header);
        return kbNameDialog.showAndWait();
    }

    private void changeInterfaceState() {
        boolean disable = !expertSystem.kbIsLoaded();

        saveKbMenuItem.setDisable(disable);
        saveKbTool.setDisable(disable);
        saveKbAsMenuItem.setDisable(disable);
        closeKbMenuItem.setDisable(disable);
        closeKbTool.setDisable(disable);

        addDomainMenuItem.setDisable(disable);
        addRuleMenuItem.setDisable(disable);
        addVariableMenuItem.setDisable(disable);
        addTool.setDisable(disable);

        domainsTableView.setDisable(disable);
        rulesTableView.setDisable(disable);
        variablesTableView.setDisable(disable);
    }

    private void consult() {

    }

    private void closeKb() {
        if (showDialog("closeKbTitle", "closeKbHeader", "closeKbMessage", Alert.AlertType.NONE))
            saveKb();
        expertSystem.setKnowledgeBase(null);
        changeInterfaceState();
    }

    private void editDomain() {

    }

    private void editRule() {

    }

    private void editVariable() {

    }

    private void forget() {

    }

    private FileChooser generateFileChooser(String title, List<String> fileExtDescrs, List<String> fileExts) {
        FileChooser kbChooser = new FileChooser();
        for (int i = 0; i < fileExtDescrs.size(); i++)
            kbChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileExtDescrs.get(i),
                    String.format("*.%s", fileExts.get(i))));
        kbChooser.setTitle(title);
        return kbChooser;
    }

    @NotNull
    private EventHandler<ActionEvent> generateHandler(String handlerName) throws NoSuchMethodException {
        Method handlerMethod = this.getClass().getDeclaredMethod(handlerName);
        return actionEvent -> {
            try {
                handlerMethod.invoke(this);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        };
    }

    public void initialise() {
        mainTabPane.getSelectionModel().selectedItemProperty().addListener(this::tabChanged);
        domainsTableView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldIdx, Number newIdx) {

            }
        });
        tabChanged(null, null, mainTabPane.getSelectionModel().getSelectedItem());
        updateTitle();
    }

    private void newKb() {
        askKbName(resources.getString("newKbTitle"),
                resources.getString("newKbHeader"),
                resources.getString("defaultKbName")
        ).ifPresent(kbName -> expertSystem.setKnowledgeBase(new KnowledgeBase(kbName)));
        updateTitle();
        changeInterfaceState();
    }

    private void openKb() {
        File newKbFile = generateFileChooser(resources.getString("openKb"),
                Arrays.asList(resources.getString("fileExtDescr")),
                        Arrays.asList(kbImporter.getFileExtension()))
                        .showOpenDialog(stage);
        if (newKbFile != null) {
            kbFile = newKbFile;
            KnowledgeBase loadedBase = kbImporter.importKnowledgeBase(kbFile);
            if (loadedBase != null)
                expertSystem.setKnowledgeBase(loadedBase);
        }
        changeInterfaceState();
        updateTitle();
    }

    private void quit() {
        if (expertSystem.kbIsLoaded())
            closeKb();
        stage.close();
    }

    private void reasoning() {

    }

    private void removeDomain() {

    }

    private void removeRule() {

    }

    private void removeVariable() {

    }

    private void saveKb() {
        if (kbFile == null) {
            saveKbAs();
            return;
        }
        kbExporter.exportKnowledgeBase(kbFile, expertSystem.getKnowledgeBase());
    }

    private void saveKbAs() {
        File newKbFile = generateFileChooser(resources.getString("saveKbAs"),
                Arrays.asList(resources.getString("fileExtDescr")),
                Arrays.asList(kbExporter.getFileExtension()))
                .showSaveDialog(stage);
        if (newKbFile != null) {
            kbFile = newKbFile;
            saveKb();
        }
    }

    private void setActions(String involvedEntity) {
        EventHandler<ActionEvent> addHandler;
        EventHandler<ActionEvent> editHandler;
        EventHandler<ActionEvent> removeHandler;

        try {
            addHandler = generateHandler(String.format("add%s", involvedEntity));
            editHandler = generateHandler(String.format("edit%s", involvedEntity));
            removeHandler = generateHandler(String.format("remove%s", involvedEntity));
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }

        addTool.setOnAction(addHandler);
        editMenuItem.setOnAction(editHandler);
        editTool.setOnAction(editHandler);
        removeMenuItem.setOnAction(removeHandler);
        removeTool.setOnAction(removeHandler);
    }

    private void setGoal() {

    }

    private void showAbout() {
        showMessage(resources.getString("about"), resources.getString("aboutText"), Alert.AlertType.INFORMATION);
    }

    private boolean showDialog(String title, String header, String message, Alert.AlertType type){
        Alert alert = new Alert(type, message, ButtonType.OK, ButtonType.CANCEL);
        alert.setHeaderText(header);
        alert.setTitle(title);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void showHelp() {
        showMessage(resources.getString("help"), resources.getString("helpText"), Alert.AlertType.INFORMATION);
    }

    private void showMessage(String title, String message, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(title);
        TextArea content = new TextArea(message);
        content.setWrapText(true);
        content.setEditable(false);
        alert.getDialogPane().setContent(content);
        alert.setResizable(true);
        alert.showAndWait();
    }

    private void updateTitle() {
        stage.setTitle(String.format("%s. %s", resources.getString("title"),
                expertSystem.kbIsLoaded() ?
                String.format("%s: %s.", resources.getString("kb"), expertSystem.getKnowledgeBase().getName()) :
                        resources.getString("noKb")));
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Элементы управления">
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private MenuBar mainMenuBar;
    @FXML
    private Menu knowledgeBaseMenu;
    @FXML
    private MenuItem newKbMenuItem;
    @FXML
    private MenuItem openKbMenuItem;
    @FXML
    private MenuItem saveKbMenuItem;
    @FXML
    private MenuItem saveKbAsMenuItem;
    @FXML
    private MenuItem closeKbMenuItem;
    @FXML
    private MenuItem quitMenuItem;
    @FXML
    private Menu editMenu;
    @FXML
    private MenuItem addDomainMenuItem;
    @FXML
    private MenuItem addVariableMenuItem;
    @FXML
    private MenuItem addRuleMenuItem;
    @FXML
    private MenuItem editMenuItem;
    @FXML
    private MenuItem removeMenuItem;
    @FXML
    private Menu consultMenu;
    @FXML
    private MenuItem setGoalMenuItem;
    @FXML
    private MenuItem consultMenuItem;
    @FXML
    private MenuItem forgetMenuItem;
    @FXML
    private Menu reasoningMenu;
    @FXML
    private MenuItem reasoningMenuItem;
    @FXML
    private Menu helpMenu;
    @FXML
    private MenuItem helpMenuItem;
    @FXML
    private MenuItem aboutMenuItem;
    @FXML
    private ToolBar mainToolBar;
    @FXML
    private Button newKbTool;
    @FXML
    private Button openKbTool;
    @FXML
    private Button saveKbTool;
    @FXML
    private Button closeKbTool;
    @FXML
    private Button addTool;
    @FXML
    private Button editTool;
    @FXML
    private Button removeTool;
    @FXML
    private Button consultTool;
    @FXML
    private Button reasoningTool;
    @FXML
    private Button helpTool;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab domainsTab;
    @FXML
    private TableColumn<Domain, String> domainNameColumn;
    @FXML
    private TableColumn<Domain, String> domainTypeColumn;
    @FXML
    private TableView<Domain> domainsTableView;
    @FXML
    private ListView<?> domainValuesListView;
    @FXML
    private Tab variablesTab;
    @FXML
    private TableView<Variable> variablesTableView;
    @FXML
    private TableColumn<Variable, String> variableNameColumn;
    @FXML
    private TableColumn<Variable, Classes> variableAttributionColumn;
    @FXML
    private TableColumn<Variable, Domain> variableDomainColumn;
    @FXML
    private TableColumn<Variable, String> variableTypeColumn;
    @FXML
    private TextField variableDomainTextField;
    @FXML
    private TextField variableAttributionTextField;
    @FXML
    private TextArea variableQuestionTextArea;
    @FXML
    private Tab rulesTab;
    @FXML
    private TableView<Rule> rulesTableView;
    @FXML
    private TableColumn<Rule, String> ruleNameColumn;
    @FXML
    private TableColumn<Rule, String> ruleContentColumn;
    @FXML
    private ListView<Fact> rulePremisesListView;
    @FXML
    private ListView<Fact> ruleConclusionsListView;
    @FXML
    private TextArea ruleCommentTextArea;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Обработчики событий">
    @FXML
    void aboutMenuItem_OnAction(ActionEvent event) { showAbout(); }
    @FXML
    void addDomain_OnAction(ActionEvent event) { addDomain(); }
    @FXML
    void addRule_OnAction(ActionEvent event) { addRule(); }
    @FXML
    void addVariable_OnAction(ActionEvent event) { addVariable(); }
    @FXML
    void closeKbMenuItem_OnAction(ActionEvent event) { closeKb(); }
    @FXML
    void closeKbTool_OnAction(ActionEvent event) { closeKb(); }
    @FXML
    void consultMenuItem_OnAction(ActionEvent event) { consult(); }
    @FXML
    void consultTool_OnAction(ActionEvent event) { consult(); }
    @FXML
    void forgetMenuItem_OnAction(ActionEvent event) { forget(); }
    @FXML
    void helpMenuItem_OnAction(ActionEvent event) { showHelp(); }
    @FXML
    void helpTool_OnAction(ActionEvent event) { showHelp(); }
    @FXML
    void newKbMenuItem_OnAction(ActionEvent event) { newKb(); }
    @FXML
    void newKbTool_OnAction(ActionEvent event) { newKb(); }
    @FXML
    void openKbMenuItem_OnAction(ActionEvent event) { openKb(); }
    @FXML
    void openKbTool_OnAction(ActionEvent event) { openKb(); }
    @FXML
    void quitMenuItem_OnAction(ActionEvent event) { quit(); }
    @FXML
    void reasoningMenuItem_OnAction(ActionEvent event) { reasoning(); }
    @FXML
    void reasoningTool_OnAction(ActionEvent event) { reasoning(); }
    @FXML
    void saveKbAsMenuItem_OnAction(ActionEvent event) { saveKbAs(); }
    @FXML
    void saveKbMenuItem_OnAction(ActionEvent event) { saveKb(); }
    @FXML
    void saveKbTool_OnAction(ActionEvent event) { saveKb(); }
    @FXML
    void setGoalMenuItem_OnAction(ActionEvent event) { setGoal(); }

    private void tabChanged(ObservableValue<? extends Tab> observableValue, Tab oldTab, Tab newTab) {
        String viewedEntity = "";
        if (newTab == domainsTab)
            viewedEntity = "Domain";
        else
        if (newTab == rulesTab)
            viewedEntity = "Rule";
        else
            viewedEntity = "Variable";
        setActions(viewedEntity);
        addTool.getTooltip().setText(resources.getString(String.format("add%s", viewedEntity)));
        editMenuItem.setText(resources.getString(String.format("edit%s", viewedEntity)));
        editTool.getTooltip().setText(resources.getString(String.format("edit%s", viewedEntity)));
        removeMenuItem.setText(resources.getString(String.format("remove%s", viewedEntity)));
        removeTool.getTooltip().setText(resources.getString(String.format("remove%s", viewedEntity)));
    }
    //</editor-fold>
}
