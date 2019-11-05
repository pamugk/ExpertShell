package transfer.interfaces;

import base.knowledgebase.KnowledgeBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

public interface KnowledgeBaseExporter {
    String getDescription(Locale usedLocale);
    String getFileExtension();
    void exportKnowledgeBase(File outputFile, KnowledgeBase exportedKb) throws IOException;
}
