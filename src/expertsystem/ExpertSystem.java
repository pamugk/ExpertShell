package expertsystem;

import base.knowledgebase.KnowledgeBase;

public class ExpertSystem {
    private InferentialMechanism inferentialMechanism;
    private KnowledgeBase knowledgeBase;
    private ReasoningSubsystem reasoningSubsystem;
    private ScratchStorage scratchStorage;

    public ExpertSystem() {
        inferentialMechanism = new InferentialMechanism();
        reasoningSubsystem = new ReasoningSubsystem();
        scratchStorage = new ScratchStorage();
    }

    public KnowledgeBase getKnowledgeBase() { return knowledgeBase; }
    public void setKnowledgeBase(KnowledgeBase knowledgeBase) { this.knowledgeBase = knowledgeBase; }
    public boolean kbIsLoaded() { return knowledgeBase != null; }
}
