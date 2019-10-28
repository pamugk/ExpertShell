package expertsystem;

import base.domains.Value;
import base.knowledgebase.KnowledgeBase;
import base.rules.Fact;
import base.variables.Variable;

import java.util.List;
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
        inferentialMechanism.setReasoningSubsystemConnector(() -> reasoningSubsystem);
        inferentialMechanism.setKnowledgeBaseConnector(() -> knowledgeBase);
        inferentialMechanism.setScratchStorageSupplier(() -> scratchStorage);
    }

    public ReasoningTree getReasoning() {
        return reasoningSubsystem.getReasoning();
    }

    public List<Fact> getUsedFacts() {
        return scratchStorage.getUsedFacts();
    }

    public Value consult(boolean cleanPrevConsult) {
        if (cleanPrevConsult)
            forgetPreviousConsulting();
        return inferentialMechanism.evaluatRootGoal(knowledgeBase.getGoal());
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

    public void forgetPreviousConsulting() {
        scratchStorage.clear();
        reasoningSubsystem.clear();
    }

    public boolean scratchStorageIsEmpty() {
        return scratchStorage.isEmpty();
    }

    public void setQuestioner(Function<Variable, Value> questioner) {
        inferentialMechanism.setQuestioner(questioner);
    }

    public Function<Variable, Value> getQuestioner() {
        return inferentialMechanism.getQuestioner();
    }
}
