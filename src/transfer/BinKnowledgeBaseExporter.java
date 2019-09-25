package transfer;

import base.knowledgebase.KnowledgeBase;
import transfer.interfaces.KnowledgeBaseExporter;

import java.io.File;
import java.util.Locale;

public class BinKnowledgeBaseExporter implements KnowledgeBaseExporter {
    @Override
    public String getDescription(Locale usedLocale) {
        return null;
    }
    @Override
    public String getFileExtension() { return "bkb"; }
    @Override
    public void exportKnowledgeBase(File outputFile, KnowledgeBase exportedKb) { }
}
