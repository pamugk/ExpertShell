package transfer.interfaces;

import base.knowledgebase.KnowledgeBase;

import java.util.Locale;

public interface KnowledgeBaseExporter {
    String getDescription(Locale usedLocale);
    void setup();
    KnowledgeBase exportRuleSet();
}
