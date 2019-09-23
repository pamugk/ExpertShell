package transfer;

import base.knowledgebase.KnowledgeBase;
import transfer.interfaces.KnowledgeBaseExporter;

import java.util.Locale;

public class BinKnowledgeBaseExporter implements KnowledgeBaseExporter {
    @Override
    public String getDescription(Locale usedLocale) {
        return null;
    }

    @Override
    public void setup() {

    }

    @Override
    public KnowledgeBase exportRuleSet() {
        return null;
    }
}
