package transfer;

import base.knowledgebase.KnowledgeBase;
import transfer.interfaces.KnowledgeBaseImporter;

import java.util.Locale;

public class BinKnowledgeBaseImporter implements KnowledgeBaseImporter {
    @Override
    public String getDescription(Locale usedLocale) {
        return null;
    }

    @Override
    public void setup() {

    }

    @Override
    public KnowledgeBase importRuleSet() {
        return null;
    }
}
