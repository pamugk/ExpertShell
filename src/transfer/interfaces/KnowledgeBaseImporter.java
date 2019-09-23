package transfer.interfaces;

import base.knowledgebase.KnowledgeBase;

import java.util.Locale;

public interface KnowledgeBaseImporter {
    String getDescription(Locale usedLocale);
    void setup();
    KnowledgeBase importRuleSet();
}
