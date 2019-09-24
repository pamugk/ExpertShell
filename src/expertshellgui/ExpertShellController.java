package expertshellgui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;

public class ExpertShellController {
    //<editor-fold defaultstate="collapsed" desc="Вспомогательные методы">
    private void addDomain() {

    }

    private void addRule() {

    }

    private void addVariable() {

    }

    private void consult() {

    }

    private void closeKb() {
    }

    private void editDomain() {

    }

    private void editRule() {

    }

    private void editVariable() {

    }

    private void forget() {

    }

    private void newKb() {

    }

    private void openKb() {

    }

    private void quit() {

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

    }

    private void saveKbAs() {

    }

    private void setGoal() {

    }

    private void showAbout() {

    }

    private void showHelp() {

    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Элементы управления">
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
    private MenuItem editDomainMenuItem;

    @FXML
    private MenuItem removeDomainMenuItem;

    @FXML
    private MenuItem addVariableMenuItem;

    @FXML
    private MenuItem editVariableMenuItem;

    @FXML
    private MenuItem removeVariableMenuItem;

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
    private Button newTool;

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
    private TableColumn<?, ?> domainNameColumn;

    @FXML
    private TableColumn<?, ?> domainTypeColumn;

    @FXML
    private TableView<?> domainsTableView;

    @FXML
    private ListView<?> domainValuesListView;

    @FXML
    private Tab variablesTab;

    @FXML
    private TableView<?> variablesTableView;

    @FXML
    private TableColumn<?, ?> variableNameColumn;

    @FXML
    private TableColumn<?, ?> variableAttributionColumn;

    @FXML
    private TableColumn<?, ?> variableDomainColumn;

    @FXML
    private TableColumn<?, ?> variableTypeColumn;

    @FXML
    private TextField variableDomainTextField;

    @FXML
    private TextField variableAttributionTextField;

    @FXML
    private TextArea variableQuestionTextArea;

    @FXML
    private Tab rulesTab;

    @FXML
    private TableView<?> rulesTableView;

    @FXML
    private TableColumn<?, ?> ruleNameColumn;

    @FXML
    private TableColumn<?, ?> ruleContentColumn;

    @FXML
    private ListView<?> ruleConditionsListView;

    @FXML
    private ListView<?> ruleOutcomesListView;

    @FXML
    private TextArea ruleCommentTextArea;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Обработчики событий">
    @FXML
    void aboutMenuItem_OnAction(ActionEvent event) {
        showAbout();
    }

    @FXML
    void addDomainMenuItem_OnAction(ActionEvent event) {
        addDomain();
    }

    @FXML
    void addRuleMenuItem_OnAction(ActionEvent event) {
        addRule();
    }

    @FXML
    void addVariableMenuItem_OnAction(ActionEvent event) {
        addVariable();
    }

    @FXML
    void closeKbMenuItem_OnAction(ActionEvent event) {
        closeKb();
    }

    @FXML
    void closeKbTool_OnAction(ActionEvent event) {
        closeKb();
    }

    @FXML
    void consultMenuItem_OnAction(ActionEvent event) {
        consult();
    }

    @FXML
    void consultTool_OnAction(ActionEvent event) {
        consult();
    }

    void editDomainMenuItem_OnAction(ActionEvent event) {
        editDomain();
    }

    void editRuleMenuItem_OnAction(ActionEvent event) {
        editRule();
    }

    void editVariableMenuItem_OnAction(ActionEvent event) {
        editVariable();
    }

    @FXML
    void forgetMenuItem_OnAction(ActionEvent event) {
        forget();
    }

    @FXML
    void helpMenuItem_OnAction(ActionEvent event) {
        showHelp();
    }

    @FXML
    void helpTool_OnAction(ActionEvent event) {
        showHelp();
    }

    @FXML
    void newKbMenuItem_OnAction(ActionEvent event) {
        newKb();
    }

    @FXML
    void newKbTool_OnAction(ActionEvent event) {
        newKb();
    }

    @FXML
    void openKbMenuItem_OnAction(ActionEvent event) {
        openKb();
    }

    @FXML
    void openKbTool_OnAction(ActionEvent event) {
        openKb();
    }

    @FXML
    void quitMenuItem_OnAction(ActionEvent event) {
        quit();
    }

    @FXML
    void reasoningMenuItem_OnAction(ActionEvent event) {
        reasoning();
    }

    @FXML
    void reasoningTool_OnAction(ActionEvent event) {
        reasoning();
    }

    void removeDomainMenuItem_OnAction(ActionEvent event) {
        removeDomain();
    }

    void removeRuleMenuItem_OnAction(ActionEvent event) {
        removeRule();
    }

    void removeVariableMenuItem_OnAction(ActionEvent event) {
        removeVariable();
    }

    @FXML
    void saveKbAsMenuItem_OnAction(ActionEvent event) {
        saveKbAs();
    }

    @FXML
    void saveKbMenuItem_OnAction(ActionEvent event) {
        saveKb();
    }

    @FXML
    void saveKbTool_OnAction(ActionEvent event) {
        saveKb();
    }

    @FXML
    void setGoalMenuItem_OnAction(ActionEvent event) {
        setGoal();
    }
    //</editor-fold>
}
