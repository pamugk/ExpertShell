package transfer.interfaces;

import base.knowledgebase.KnowledgeBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

public interface KnowledgeBaseImporter {
    String getDescription(Locale usedLocale);
    String getFileExtension();
    KnowledgeBase importKnowledgeBase(File inputFile) throws IOException, ClassNotFoundException;
}
