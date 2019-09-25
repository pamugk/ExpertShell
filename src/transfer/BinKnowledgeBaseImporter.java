package transfer;

import base.knowledgebase.KnowledgeBase;
import transfer.interfaces.KnowledgeBaseImporter;

import java.io.File;
import java.util.Locale;

public class BinKnowledgeBaseImporter implements KnowledgeBaseImporter {
    @Override
    public String getDescription(Locale usedLocale) {
        return null;
    }
    @Override
    public String getFileExtension() { return "bkb"; }
    @Override
    public KnowledgeBase importKnowledgeBase(File inputFile) {
        return null;
    }
}
