package expertshellgui;

import base.domains.Domain;
import base.domains.Value;
import base.knowledgebase.KnowledgeBase;
import base.rules.Fact;
import base.rules.Rule;
import base.variables.Types;
import base.variables.Variable;
import expertsystem.ExpertSystem;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import transfer.BinKnowledgeBaseExporter;
import transfer.BinKnowledgeBaseImporter;
import transfer.interfaces.KnowledgeBaseExporter;
import transfer.interfaces.KnowledgeBaseImporter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ExpertShellController {
    private Image addImage;
    private Image editImage;
    private Map<Types, String> types;
    private int draggedIdx;

    //<editor-fold defaultstate="collapsed" desc="Вспомогательные методы">
    private ExpertSystem expertSystem;
    private KnowledgeBaseExporter kbExporter;
    private File kbFile;
    private KnowledgeBaseImporter kbImporter;
    private Stage stage;

    public ExpertSystem getExpertSystem() { return  expertSystem; }
    private void setExpertSystem(ExpertSystem expertSystem) { this.expertSystem = expertSystem; }

    public KnowledgeBaseExporter getKbExporter() { return kbExporter; }
    private void setKbExporter(KnowledgeBaseExporter kbExporter) { this.kbExporter = kbExporter; }

    public KnowledgeBaseImporter getKbImporter() { return kbImporter; }
    private void setKbImporter(KnowledgeBaseImporter kbImporter) { this.kbImporter = kbImporter; }

    public Stage getStage() { return stage; }
    private void setStage(Stage stage) { this.stage = stage; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Вспомогательные методы">
    private Domain baseDomain;
    private Variable baseVariable;
    private void addDomain() {
        Domain newDomain = baseDomain == null ? new Domain(UUID.randomUUID(),
                resources.getString("newDomain")) : baseDomain;
        try {
            newDomain = DomainDialogController.showAndWait(newDomain, resources.getString("addDomain"),
                    addImage, expertSystem.getKnowledgeBase(), resources);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (newDomain == null)
            return;
        if (!expertSystem.scratchStorageIsEmpty())
            forget();
        tableViewSmartInsert(domainsTableView, expertSystem.getKnowledgeBase().getUsedDomains(), newDomain);
    }

    private void addRule() {
        Rule newRule;
        try {
            newRule = RuleDialogController.showAndWait(new Rule(UUID.randomUUID(),
                            resources.getString("newRule")), resources.getString("addRule"),
                    addImage, expertSystem.getKnowledgeBase(), resources);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (newRule == null)
            return;
        if (!expertSystem.scratchStorageIsEmpty())
            forget();
        tableViewSmartInsert(rulesTableView, expertSystem.getKnowledgeBase().getRules(), newRule);
        updateTablesAfterRule();
    }

    private void addVariable() {
        Variable newVariable = baseVariable == null ?
                new Variable(UUID.randomUUID(),resources.getString("newVariable"), null, Types.REQUESTED) :
                baseVariable;
        try {
            newVariable = VariableDialogController.showAndWait(newVariable,
                    resources.getString("addVariable"), addImage, expertSystem.getKnowledgeBase(), resources,
                    false);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (newVariable == null)
            return;
        if (!expertSystem.scratchStorageIsEmpty())
            forget();
        expertSystem.getKnowledgeBase().getUsedDomains().forEach(domain -> {
            if (!domainsTableView.getItems().contains(domain))
                domainsTableView.getItems().add(domain);
        });
        tableViewSmartInsert(variablesTableView, expertSystem.getKnowledgeBase().getVariables(), newVariable);
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

    private void cascadeRemoveRules(Set<Rule> rulesForRemove){
        rulesForRemove.forEach(rule -> {
            rulesTableView.getItems().remove(rule);
            expertSystem.getKnowledgeBase().getRules().remove(rule);
        });
    }

    private void cascadeRemoveRulesOnDomainValue(UUID valUuid){
        List<Rule> rulesForRemove =
            expertSystem.getKnowledgeBase().getRules().stream().filter(rule ->
                rule.getPremises().stream().anyMatch(fact ->
                    fact.getAssignable() instanceof Value && valUuid.equals(fact.getAssignable().getGuid())) ||
                rule.getConclusions().stream().anyMatch(fact ->
                    fact.getAssignable() instanceof Value && valUuid.equals(fact.getAssignable().getGuid()))
            ).collect(Collectors.toList());
        rulesForRemove.forEach(rule -> {
            rulesTableView.getItems().remove(rule);
            expertSystem.getKnowledgeBase().getRules().remove(rule);
        });
    }

    private void cascadeRemoveVariables(List<Variable> varsForRemove, Set<Rule> rulesToRemove) {
        varsForRemove.forEach(variable-> {
                    expertSystem.getKnowledgeBase().getVariables().remove(variable);
                    variablesTableView.getItems().remove(variable);
        });
        cascadeRemoveRules(rulesToRemove);
    }

    private void changeInterfaceBlock(boolean blocked) {
        mainMenuBar.setDisable(blocked);
        mainToolBar.setDisable(blocked);
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

        setGoalMenuItem.setDisable(disable);
        changeInterfaceStateAccordingToGoal(expertSystem.kbIsLoaded()
                ? expertSystem.getKnowledgeBase().getGoal() : null);
        changeInterfaceStateAccordingToMemoryState(expertSystem.scratchStorageIsEmpty());

        domainsTableView.setDisable(disable);
        rulesTableView.setDisable(disable);
        variablesTableView.setDisable(disable);
    }

    private void changeInterfaceStateAccordingToGoal(Variable goal) {
        boolean disable = goal == null;
        consultMenuItem.setDisable(disable);
        consultTool.setDisable(disable);
    }

    private void changeInterfaceStateAccordingToMemoryState(boolean memoryIsEmpty) {
        reasoningMenuItem.setDisable(memoryIsEmpty);
        reasoningTool.setDisable(memoryIsEmpty);
    }

    private void changeSpecificActionsState(boolean disable) {
        editMenuItem.setDisable(disable);
        editTool.setDisable(disable);
        removeMenuItem.setDisable(disable);
        removeTool.setDisable(disable);
    }

    private Domain cloneDomain(Domain baseDomain) {
        Domain clone = new Domain(UUID.randomUUID(), baseDomain.getName());
        clone.setType(baseDomain.getType());
        baseDomain.getValues().forEach(val -> clone.getValues().add(new Value(UUID.randomUUID(), val.getContent())));
        return clone;
    }

    private Variable cloneVariable(Variable baseVariable)
    {
        Variable clone = new Variable(UUID.randomUUID(), baseVariable.getName(),
                baseVariable.getDomain(), baseVariable.getVarClass());
        clone.setQuestion(baseVariable.getQuestion());
        clone.setLabel(baseVariable.getLabel());
        clone.setLimit(baseVariable.getLimit());
        clone.setRigor(baseVariable.getRigor());
        clone.setType(baseVariable.getType());
        clone.setWhen(baseVariable.getWhen());
        return clone;
    }

    private List<Variable> collectAssociatedToDomainVars(UUID domainUuid) {
        return expertSystem.getKnowledgeBase().getVariables().stream().filter(variable ->
                variable.getDomain().getGuid().equals(domainUuid)).collect(Collectors.toList());
    }

    private List<Rule> collectAssociatedToVarRules(UUID varUuid) {
        return expertSystem.getKnowledgeBase().getRules().stream().filter(rule ->
                rule.getPremises().stream().anyMatch(fact -> fact.getVariable().getGuid().equals(varUuid)
                        || fact.getAssignable() instanceof Variable && varUuid.equals(fact.getAssignable().getGuid())) ||
                        rule.getConclusions().stream().anyMatch(fact ->
                                fact.getVariable().getGuid().equals(varUuid) || fact.getAssignable() instanceof Variable &&
                                        varUuid.equals(fact.getAssignable().getGuid()))
        ).collect(Collectors.toList());
    }

    private void consult() {
        try {
            changeInterfaceBlock(true);
            ConsultDialogController.show(resources.getString("consulting"), expertSystem, resources, this::onDialogClose);
            changeInterfaceStateAccordingToMemoryState(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean closeKb() {
        ButtonType response = showDialog(resources.getString("closeKbTitle"), resources.getString("closeKbHeader"),
                resources.getString("closeKbMessage"), Alert.AlertType.NONE,
                Arrays.asList(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL));
        if (response == ButtonType.OK)
            saveKb();
        else
            if (response == ButtonType.CANCEL)
                return false;
        expertSystem.setKnowledgeBase(null);
        expertSystem.forgetPreviousConsulting();
        domainsTableView.getItems().clear();
        variablesTableView.getItems().clear();
        rulesTableView.getItems().clear();
        changeInterfaceState();
        return true;
    }

    private void domainTableViewSelectionChanged(ObservableValue<? extends Number> observableValue, Number oldIndex,
                                                 Number newIndex) {
        boolean disable = newIndex.intValue() == -1;
        changeSpecificActionsState(disable);
        domainValuesListView.getItems().clear();
        if (!disable)
            domainValuesListView.getItems().addAll(domainsTableView.getSelectionModel().getSelectedItem().getValues());
    }

    private void editDomain() {
        int idx = domainsTableView.getSelectionModel().getSelectedIndex();
        Domain editedDomain = domainsTableView.getItems().get(idx);
        List<Value> removedVals = new ArrayList<>(editedDomain.getValues());
        List<Variable> associatedVars = collectAssociatedToDomainVars(editedDomain.getGuid());
        Set<Rule> associatedRules;
        if (associatedVars.size() > 0) {
            associatedRules = new HashSet<>();
            for (Variable variable : associatedVars)
                associatedRules.addAll(collectAssociatedToVarRules(variable.getGuid()));
            String message = String.format("%s\n", resources.getString("domainChanging"));
            message += String.format("%s:\n%s\n", resources.getString("variables"),
                    associatedVars.stream().map(Variable::toString).collect(Collectors.joining("; ")));
            if (associatedRules.size() > 0)
                message += String.format("%s:\n%s\n", resources.getString("rules"),
                        associatedRules.stream().map(Rule::toString).collect(Collectors.joining("; ")));
            if (showDialog(resources.getString("editingDomain"),
                    resources.getString("cloning"), message, Alert.AlertType.WARNING,
                    Arrays.asList(ButtonType.YES, ButtonType.NO)) == ButtonType.YES) {
                baseDomain = cloneDomain(editedDomain);
                addDomain();
                return;
            }
        }
        try {
            editedDomain = DomainDialogController.showAndWait(editedDomain, resources.getString("editDomain"),
                    editImage, expertSystem.getKnowledgeBase(), resources
            );
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (editedDomain == null)
            return;
        if (!expertSystem.scratchStorageIsEmpty())
            forget();
        removedVals.removeAll(editedDomain.getValues());
        for (Value val: removedVals)
            cascadeRemoveRulesOnDomainValue(val.getGuid());
        domainsTableView.getItems().set(idx, editedDomain);
        domainTableViewSelectionChanged(null, null, idx);
    }

    private void editRule() {
        int idx = rulesTableView.getSelectionModel().getSelectedIndex();
        Rule editedRule;
        try {
            editedRule = RuleDialogController.showAndWait(
                    rulesTableView.getItems().get(idx), resources.getString("editRule"),
                    editImage, expertSystem.getKnowledgeBase(), resources
            );
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (editedRule == null)
            return;
        if (!expertSystem.scratchStorageIsEmpty())
            forget();
        rulesTableView.getItems().set(idx, editedRule);
        updateTablesAfterRule();
        ruleTableViewSelectionChanged(null, null, idx);
    }

    private void editVariable() {
        int idx = variablesTableView.getSelectionModel().getSelectedIndex();
        Variable editedVariable = variablesTableView.getItems().get(idx);
        List<Rule> associatedRules = collectAssociatedToVarRules(editedVariable.getGuid());
        if (associatedRules.size() > 0) {
            String message = String.format("%s\n", resources.getString("variableChanging"));
            message += String.format("%s:\n%s\n", resources.getString("rules"),
                associatedRules.stream().map(Rule::toString).collect(Collectors.joining("; ")));
            if (showDialog(resources.getString("editingVariable"),
                    resources.getString("cloning"), message, Alert.AlertType.WARNING,
                    Arrays.asList(ButtonType.YES, ButtonType.NO)) == ButtonType.YES) {
                baseVariable = cloneVariable(editedVariable);
                addVariable();
                return;
            }
        }
        try {
            editedVariable = VariableDialogController.showAndWait(
                    editedVariable, resources.getString("editVariable"),
                    editImage, expertSystem.getKnowledgeBase(), resources,
                    editedVariable.getVarClass() == Types.DEDUCTED
            );
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (editedVariable == null)
            return;
        if (!expertSystem.scratchStorageIsEmpty())
            forget();
        expertSystem.getKnowledgeBase().getUsedDomains().forEach(domain -> {
            if (!domainsTableView.getItems().contains(domain))
                domainsTableView.getItems().add(domain);
        });
        variablesTableView.getItems().set(idx, editedVariable);
        variableTableViewSelectionChanged(null, null, editedVariable);
    }

    private void forget() {
        expertSystem.forgetPreviousConsulting();
        changeInterfaceStateAccordingToMemoryState(true);
    }

    private <T> void generateListViewCellFactory(ListView<T> listView) {
        listView.setCellFactory(valueListView -> {
            ListCell<T> newCell = new ListCell<T>() {
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
            return newCell;
        });
    }

    private FileChooser generateFileChooser(String title, List<String> fileExtDescrs, List<String> fileExts) {
        FileChooser kbChooser = new FileChooser();
        for (int i = 0; i < fileExtDescrs.size(); i++)
            kbChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileExtDescrs.get(i),
                    String.format("*.%s", fileExts.get(i))));
        kbChooser.setTitle(title);
        return kbChooser;
    }

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

    private void fillGUI() {
        if (!expertSystem.kbIsLoaded())
            return;
        domainsTableView.getItems().clear();
        domainsTableView.getItems().addAll(expertSystem.getKnowledgeBase().getUsedDomains());
        variablesTableView.getItems().clear();
        variablesTableView.getItems().addAll(expertSystem.getKnowledgeBase().getVariables());
        rulesTableView.getItems().clear();
        rulesTableView.getItems().addAll(expertSystem.getKnowledgeBase().getRules());
    }

    private void initialise() {
        tabChanged(null, null, mainTabPane.getSelectionModel().getSelectedItem());
        updateTitle();
        types = new HashMap<>();
        types.put(Types.REQUESTED, resources.getString("varReq"));
        types.put(Types.DEDUCTED, resources.getString("varDed"));
        types.put(Types.REQUESTED_DEDUCTED, resources.getString("varRD"));
        types.put(Types.DEDUCTED_REQUESTED, resources.getString("varDR"));
    }

    private void newKb() {
        askKbName(resources.getString("newKbTitle"),
                resources.getString("newKbHeader"),
                resources.getString("defaultKbName")
        ).ifPresent(kbName -> expertSystem.setKnowledgeBase(new KnowledgeBase(kbName)));
        updateTitle();
        changeInterfaceState();
    }

    private void onDialogClose(WindowEvent event) {
        changeInterfaceBlock(false);
    }

    private void openKb() {
        File newKbFile = generateFileChooser(resources.getString("openKb"),
                Collections.singletonList(resources.getString("fileExtDescr")),
                Collections.singletonList(kbImporter.getFileExtension()))
                        .showOpenDialog(stage);
        if (newKbFile != null) {
            kbFile = newKbFile;
            KnowledgeBase loadedBase;
            try {
                loadedBase = kbImporter.importKnowledgeBase(kbFile);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
            expertSystem.setKnowledgeBase(loadedBase);
            fillGUI();
        }
        changeInterfaceState();
        updateTitle();
    }

    private void quit() {
        if (expertSystem.kbIsLoaded())
            if (!closeKb())
                return;
        stage.close();
    }

    private void reasoning() {
        try {
            changeInterfaceBlock(true);
            ReasoningDialogController.show(resources.getString("reasoning"), expertSystem, resources,
                    this::onDialogClose, Modality.NONE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeDomain() {
        int idx = domainsTableView.getSelectionModel().getSelectedIndex();
        KnowledgeBase kb = expertSystem.getKnowledgeBase();
        UUID uuid = kb.getUsedDomains().get(idx).getGuid();
        List<Variable> associatedVars = collectAssociatedToDomainVars(uuid);
        Set<Rule> associatedRules = new HashSet<>();
        if (associatedVars.size() > 0) {
            associatedRules = new HashSet<>();
            for (Variable variable : associatedVars)
                associatedRules.addAll(collectAssociatedToVarRules(variable.getGuid()));
            String message = resources.getString("domainRemoval");
            message += String.format("%s:\n%s\n", resources.getString("variables"),
                    associatedVars.stream().map(Variable::toString).collect(Collectors.joining("; ")));
            if (associatedRules.size() > 0)
                message += String.format("%s:\n%s\n", resources.getString("rules"),
                        associatedRules.stream().map(Rule::toString).collect(Collectors.joining("; ")));
            if (showDialog(resources.getString("removingDomain"),
                    resources.getString("sure"), message, Alert.AlertType.WARNING,
                    Arrays.asList(ButtonType.YES, ButtonType.NO)) == ButtonType.NO)
                return;
        }
        kb.getUsedDomains().remove(idx);
        domainsTableView.getItems().remove(idx);
        if (!expertSystem.scratchStorageIsEmpty())
            forget();
        cascadeRemoveVariables(associatedVars, associatedRules);
    }

    private void removeRule() {
        int idx = rulesTableView.getSelectionModel().getSelectedIndex();
        rulesTableView.getItems().remove(idx);
        expertSystem.getKnowledgeBase().getRules().remove(idx);
        if (!expertSystem.scratchStorageIsEmpty())
            forget();
    }

    private void removeVariable() {
        int idx = variablesTableView.getSelectionModel().getSelectedIndex();
        UUID uuid = expertSystem.getKnowledgeBase().getVariables().get(idx).getGuid();
        Set<Rule> associatedRules = new HashSet<>(collectAssociatedToVarRules(uuid));
        if (associatedRules.size() > 0) {
            String message = resources.getString("variableRemoval");
            message += String.format("%s:\n%s\n", resources.getString("rules"),
                        associatedRules.stream().map(Rule::toString).collect(Collectors.joining("; ")));
            if (showDialog(resources.getString("removingVariable"),
                    resources.getString("sure"), message, Alert.AlertType.WARNING,
                    Arrays.asList(ButtonType.YES, ButtonType.NO)) == ButtonType.NO)
                return;
        }
        expertSystem.getKnowledgeBase().getVariables().remove(idx);
        variablesTableView.getItems().remove(idx);
        if (!expertSystem.scratchStorageIsEmpty())
            forget();
        if (expertSystem.getKnowledgeBase().getGoal() != null
                && expertSystem.getKnowledgeBase().getGoal().getGuid().equals(uuid))
            expertSystem.getKnowledgeBase().setGoal(null);
        cascadeRemoveRules(associatedRules);
    }

    private void ruleTableViewSelectionChanged(ObservableValue<? extends Number> observableValue, Number oldIndex,
        Number newIndex) {
        boolean disable = newIndex.intValue() == -1;
        rulePremisesListView.getItems().clear();
        ruleConclusionsListView.getItems().clear();
        ruleCommentTextArea.clear();
        ruleReasonTextArea.clear();

        if (!disable) {
            Rule selectedRule = rulesTableView.getItems().get(newIndex.intValue());
            rulePremisesListView.getItems().addAll(selectedRule.getPremises());
            ruleConclusionsListView.getItems().addAll(selectedRule.getConclusions());
            ruleCommentTextArea.setText(selectedRule.getComment());
            ruleReasonTextArea.setText(selectedRule.getReason());
        }

        changeSpecificActionsState(disable);
    }

    private void saveKb() {
        if (kbFile == null) {
            saveKbAs();
            return;
        }
        try {
            kbExporter.exportKnowledgeBase(kbFile, expertSystem.getKnowledgeBase());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveKbAs() {
        File newKbFile = generateFileChooser(resources.getString("saveKbAs"),
                Collections.singletonList(resources.getString("fileExtDescr")),
                Collections.singletonList(kbExporter.getFileExtension()))
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
        Variable newGoal = expertSystem.getKnowledgeBase().getGoal();
        try {
            newGoal = GoalDialogController.showAndWait(newGoal,resources.getString("setGoal"),
                    expertSystem.getKnowledgeBase(), resources);
        } catch (IOException e) {
            e.printStackTrace();
        }
        expertSystem.getKnowledgeBase().setGoal(newGoal);
        changeInterfaceStateAccordingToGoal(newGoal);
    }

    private <T> void setupDragAndDrop(TableView<T> table, Supplier<List<T>> collectionSupplier)
    {
        table.setRowFactory(tableView -> {
            TableRow<T> newRow = new TableRow<>();
            newRow.setOnMouseClicked(mouseEvent -> {
                if (newRow.isEmpty())
                    table.getSelectionModel().clearSelection();
            });
            newRow.setOnDragDetected(mouseEvent -> {
                if (newRow.getItem() == null) {
                    return;
                }

                draggedIdx = newRow.getIndex();
                Dragboard dragboard = newRow.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(newRow.getItem().toString());
                dragboard.setContent(content);

                mouseEvent.consume();
            });
            newRow.setOnDragOver(dragEvent -> {
                if (dragEvent.getGestureSource() != newRow &&
                        dragEvent.getDragboard().hasString()) {
                    dragEvent.acceptTransferModes(TransferMode.MOVE);
                }
                dragEvent.consume();
            });
            newRow.setOnDragEntered(dragEvent -> {
                if (dragEvent.getGestureSource() != newRow &&
                        dragEvent.getDragboard().hasString()) {
                    newRow.setOpacity(0.3);
                }
            });
            newRow.setOnDragExited(dragEvent -> {
                if (dragEvent.getGestureSource() != newRow &&
                        dragEvent.getDragboard().hasString()) {
                    newRow.setOpacity(1);
                }
            });
            newRow.setOnDragDropped(dragEvent -> {
                Dragboard db = dragEvent.getDragboard();
                boolean success = false;

                if (db.hasString()) {
                    T draggedObj = tableView.getItems().get(draggedIdx);
                    List<T> collection = collectionSupplier.get();
                    int thisIdx =
                            newRow.getIndex() < tableView.getItems().size() ?
                                    newRow.getIndex() : tableView.getItems().size()-1;
                    for (int i = draggedIdx; i > thisIdx; i--) {
                        tableView.getItems().set(i, tableView.getItems().get(i - 1));
                        collection.set(i, collection.get(i - 1));
                    }
                    for (int i = draggedIdx; i < thisIdx; i++) {
                        tableView.getItems().set(i, tableView.getItems().get(i + 1));
                        collection.set(i, collection.get(i + 1));
                    }
                    tableView.getItems().set(thisIdx, draggedObj);
                    collection.set(thisIdx, draggedObj);
                    success = true;
                }
                dragEvent.setDropCompleted(success);
                dragEvent.consume();
            });
            newRow.setOnDragDone(DragEvent::consume);
            return newRow;
        });
    }

    public static void show(Stage primaryStage) throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("localisation/shellLocalisation");
        FXMLLoader loader = new FXMLLoader(ExpertShellController.class.getResource("/fxml/expertshellgui.fxml"), bundle);
        Parent root = loader.load();
        primaryStage.setTitle(bundle.getString("title"));
        primaryStage.getIcons().add(new Image(ExpertShellController.class.getResourceAsStream("/icons/app.png")));
        primaryStage.setScene(new Scene(root));
        ExpertShellController controller = loader.getController();
        controller.setStage(primaryStage);
        controller.setExpertSystem(new ExpertSystem());
        controller.setKbImporter(new BinKnowledgeBaseImporter());
        controller.setKbExporter(new BinKnowledgeBaseExporter());
        controller.initialise();
        primaryStage.setOnCloseRequest(event -> {
            if (controller.expertSystem.kbIsLoaded())
                if (!controller.closeKb())
                    event.consume();
        });
        primaryStage.show();
    }

    private void showAbout() {
        showMessage(resources.getString("about"), resources.getString("aboutText"), Alert.AlertType.INFORMATION);
    }

    private ButtonType showDialog(String title, String header, String message, Alert.AlertType type,
                                  List<ButtonType> btns){
        Alert alert = new Alert(type, message);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(btns);
        alert.setHeaderText(header);
        alert.setTitle(title);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        Optional<ButtonType> result = alert.showAndWait();
        return result.orElse(ButtonType.CANCEL);
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

    private void tabChanged(ObservableValue<? extends Tab> observableValue, Tab oldTab, Tab newTab) {
        domainsTableView.getSelectionModel().clearSelection();

        variablesTableView.getSelectionModel().clearSelection();

        rulesTableView.getSelectionModel().clearSelection();

        String viewedEntity;
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

    private <T> void tableViewSmartInsert(TableView<T> tableView, List<T> kbCollection, T newItem) {
        int currentIdx = tableView.getSelectionModel().getSelectedIndex();
        int newIdx;
        if (currentIdx == -1) {
            kbCollection.add(newItem);
            tableView.getItems().add(newItem);
            newIdx = kbCollection.size() - 1;
        }
        else{
            newIdx = currentIdx+1;
            kbCollection.add(newIdx, newItem);
            tableView.getItems().add(newIdx, newItem);
        }
        tableView.getSelectionModel().select(newIdx);
    }

    private void updateTablesAfterRule() {
        domainsTableView.getItems().clear();
        domainsTableView.getItems().addAll(expertSystem.getKnowledgeBase().getUsedDomains());
        variablesTableView.getItems().clear();
        variablesTableView.getItems().addAll(expertSystem.getKnowledgeBase().getVariables());
    }

    private void updateTitle() {
        stage.setTitle(String.format("%s. %s", resources.getString("title"),
                expertSystem.kbIsLoaded() ?
                String.format("%s: %s.", resources.getString("kb"), expertSystem.getKnowledgeBase().getName()) :
                        resources.getString("noKb")));
    }

    private void variableTableViewSelectionChanged(ObservableValue<? extends Variable> observableValue, Variable oldVal,
                                                   Variable newVal) {
        boolean disable = newVal == null;
        changeSpecificActionsState(disable);
        varDomainValuesListView.getItems().clear();
        if (disable){
            variableLabelTextField.setText("");
            variableDomainTextField.setText("");
            variableAttributionTextField.setText("");
            variableQuestionTextArea.setText("");
        }
        else {
            variableLabelTextField.setText(newVal.getLabel());
            variableDomainTextField.setText(newVal.getDomain().getName());
            variableAttributionTextField.setText(types.get(newVal.getVarClass()));
            variableQuestionTextArea.setText(newVal.getQuestion());
            varDomainValuesListView.getItems().addAll(newVal.getDomain().getValues());
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Элементы управления">
    @FXML
    private ResourceBundle resources;
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
    private ListView<Value> domainValuesListView;
    @FXML
    private Tab variablesTab;
    @FXML
    private TableView<Variable> variablesTableView;
    @FXML
    private TableColumn<Variable, String> variableNameColumn;
    @FXML
    private TableColumn<Variable, Types> variableAttributionColumn;
    @FXML
    private TableColumn<Variable, Domain> variableDomainColumn;
    @FXML
    private TextField variableLabelTextField;
    @FXML
    private TextField variableDomainTextField;
    @FXML
    private ListView<Value> varDomainValuesListView;
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
    @FXML
    private TextArea ruleReasonTextArea;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Обработчики событий">
    @FXML
    void aboutMenuItem_OnAction(ActionEvent event) { showAbout(); }
    @FXML
    void addDomain_OnAction(ActionEvent event) {
        baseDomain = null;
        addDomain();
    }
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
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Инициализация">
    @FXML
    void initialize() {
        assert mainMenuBar != null : "fx:id=\"mainMenuBar\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert knowledgeBaseMenu != null : "fx:id=\"knowledgeBaseMenu\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert newKbMenuItem != null : "fx:id=\"newKbMenuItem\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert openKbMenuItem != null : "fx:id=\"openKbMenuItem\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert saveKbMenuItem != null : "fx:id=\"saveKbMenuItem\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert saveKbAsMenuItem != null : "fx:id=\"saveKbAsMenuItem\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert closeKbMenuItem != null : "fx:id=\"closeKbMenuItem\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert quitMenuItem != null : "fx:id=\"quitMenuItem\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert editMenu != null : "fx:id=\"editMenu\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert addDomainMenuItem != null : "fx:id=\"addDomainMenuItem\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert addVariableMenuItem != null : "fx:id=\"addVariableMenuItem\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert addRuleMenuItem != null : "fx:id=\"addRuleMenuItem\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert editMenuItem != null : "fx:id=\"editMenuItem\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert removeMenuItem != null : "fx:id=\"removeMenuItem\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert consultMenu != null : "fx:id=\"consultMenu\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert setGoalMenuItem != null : "fx:id=\"setGoalMenuItem\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert consultMenuItem != null : "fx:id=\"consultMenuItem\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert reasoningMenu != null : "fx:id=\"reasoningMenu\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert reasoningMenuItem != null : "fx:id=\"reasoningMenuItem\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert helpMenu != null : "fx:id=\"helpMenu\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert helpMenuItem != null : "fx:id=\"helpMenuItem\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert aboutMenuItem != null : "fx:id=\"aboutMenuItem\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert mainToolBar != null : "fx:id=\"mainToolBar\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert newKbTool != null : "fx:id=\"newKbTool\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert openKbTool != null : "fx:id=\"openKbTool\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert saveKbTool != null : "fx:id=\"saveKbTool\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert closeKbTool != null : "fx:id=\"closeKbTool\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert addTool != null : "fx:id=\"addTool\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert editTool != null : "fx:id=\"editTool\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert removeTool != null : "fx:id=\"removeTool\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert consultTool != null : "fx:id=\"consultTool\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert reasoningTool != null : "fx:id=\"reasoningTool\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert helpTool != null : "fx:id=\"helpTool\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert mainTabPane != null : "fx:id=\"mainTabPane\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert domainsTab != null : "fx:id=\"domainsTab\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert domainsTableView != null : "fx:id=\"domainsTableView\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert domainNameColumn != null : "fx:id=\"domainNameColumn\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert domainTypeColumn != null : "fx:id=\"domainTypeColumn\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert domainValuesListView != null : "fx:id=\"domainValuesListView\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert variablesTab != null : "fx:id=\"variablesTab\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert variablesTableView != null : "fx:id=\"variablesTableView\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert variableNameColumn != null : "fx:id=\"variableNameColumn\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert variableAttributionColumn != null : "fx:id=\"variableAttributionColumn\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert variableDomainColumn != null : "fx:id=\"variableDomainColumn\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert variableDomainTextField != null : "fx:id=\"variableDomainTextField\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert variableAttributionTextField != null : "fx:id=\"variableAttributionTextField\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert variableQuestionTextArea != null : "fx:id=\"variableQuestionTextArea\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert rulesTab != null : "fx:id=\"rulesTab\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert rulesTableView != null : "fx:id=\"rulesTableView\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert ruleNameColumn != null : "fx:id=\"ruleNameColumn\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert ruleContentColumn != null : "fx:id=\"ruleContentColumn\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert rulePremisesListView != null : "fx:id=\"rulePremisesListView\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert ruleConclusionsListView != null : "fx:id=\"ruleConclusionsListView\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert ruleCommentTextArea != null : "fx:id=\"ruleCommentTextArea\" was not injected: check your FXML file 'expertshellgui.fxml'.";
        assert ruleReasonTextArea != null : "fx:id=\"ruleReasonTextArea\" was not injected: check your FXML file 'expertshellgui.fxml'.";

        mainTabPane.getSelectionModel().selectedItemProperty().addListener(this::tabChanged);

        domainsTableView.getSelectionModel().selectedIndexProperty().addListener(this::domainTableViewSelectionChanged);
        setupDragAndDrop(domainsTableView, () -> expertSystem.getKnowledgeBase().getUsedDomains());
        domainNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        domainTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        generateListViewCellFactory(domainValuesListView);

        rulesTableView.getSelectionModel().selectedIndexProperty().addListener(this::ruleTableViewSelectionChanged);
        setupDragAndDrop(rulesTableView, () -> expertSystem.getKnowledgeBase().getRules());
        ruleNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ruleContentColumn.setCellFactory(new Callback<TableColumn<Rule, String>, TableCell<Rule, String>>() {
            @Override
            public TableCell<Rule, String> call(TableColumn<Rule, String> ruleStringTableColumn) {
                return new TableCell<Rule, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null)
                            setText(null);
                        else setText(getTableRow().getItem().getContent());
                    }
                };
            }
        });
        generateListViewCellFactory(rulePremisesListView);
        generateListViewCellFactory(ruleConclusionsListView);

        variablesTableView.getSelectionModel().selectedItemProperty().addListener(this::variableTableViewSelectionChanged);
        variableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        variableAttributionColumn.setCellValueFactory(new PropertyValueFactory<>("varClass"));
        variableAttributionColumn.setCellFactory(new Callback<TableColumn<Variable, Types>, TableCell<Variable, Types>>() {
            @Override
            public TableCell<Variable, Types> call(TableColumn<Variable, Types> variableClassesTableColumn) {
                return new TableCell<Variable, Types>() {
                    @Override
                    protected void updateItem(Types item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            super.setText(null);
                        } else {
                            super.setText(types.get(item));
                        }
                    }
                };
            }
        });

        variableDomainColumn.setCellValueFactory(new PropertyValueFactory<>("domain"));
        variableDomainColumn.setCellFactory(new Callback<TableColumn<Variable, Domain>, TableCell<Variable, Domain>>() {
            @Override
            public TableCell<Variable, Domain> call(TableColumn<Variable, Domain> variableDomainTableColumn) {
                return new TableCell<Variable, Domain>() {
                    @Override
                    protected void updateItem(Domain item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null)
                            super.setText(null);
                        else
                            super.setText(item.getName());
                    }
                };
            }
        });

        setupDragAndDrop(variablesTableView, () -> expertSystem.getKnowledgeBase().getVariables());
        generateListViewCellFactory(varDomainValuesListView);

        addImage = new Image("/icons/newItem.png");
        editImage = new Image("/icons/editItem.png");
    }
    //</editor-fold>
}
