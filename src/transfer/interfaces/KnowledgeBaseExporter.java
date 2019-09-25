package transfer.interfaces;

import base.knowledgebase.KnowledgeBase;

import java.io.File;
import java.util.Locale;

public interface KnowledgeBaseExporter {
    String getDescription(Locale usedLocale);
    String getFileExtension();
    void exportKnowledgeBase(File outputFile, KnowledgeBase exportedKb);
}
