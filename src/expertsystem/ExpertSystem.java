package expertsystem;

import base.domains.Value;
import base.knowledgebase.KnowledgeBase;
import base.variables.Variable;

import java.util.function.Function;

public class ExpertSystem {
    private InferentialMechanism inferentialMechanism;
    private KnowledgeBase knowledgeBase;
    private ReasoningSubsystem reasoningSubsystem;
    private ScratchStorage scratchStorage;

    public ExpertSystem() {
        scratchStorage = new ScratchStorage();
        reasoningSubsystem = new ReasoningSubsystem();
        inferentialMechanism = new InferentialMechanism();
        inferentialMechanism.setKnowledgeBaseConnector(() -> knowledgeBase);
        inferentialMechanism.setScratchStorageSupplier(() -> scratchStorage);
    }

    public Value consult() {
        return inferentialMechanism.evaluateGoal(knowledgeBase.getGoal());
    }

    public KnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    public boolean kbIsLoaded() {
        return knowledgeBase != null;
    }

    public void setQuestioner(Function<Variable, Value> questioner) {
        inferentialMechanism.setQuestioner(questioner);
    }

    public Function<Variable, Value> getQuestioner() {
        return inferentialMechanism.getQuestioner();
    }
}
