package transfer;

import base.knowledgebase.KnowledgeBase;
import transfer.interfaces.KnowledgeBaseExporter;

import java.io.*;
import java.util.Locale;

public class BinKnowledgeBaseExporter implements KnowledgeBaseExporter {
    @Override
    public String getDescription(Locale usedLocale) {
        return "Сериализует базу знаний в бинарный файл";
    }
    @Override
    public String getFileExtension() { return "bkb"; }
    @Override
    public void exportKnowledgeBase(File outputFile, KnowledgeBase exportedKb) throws IOException {
        var outputStream = new ObjectOutputStream(new FileOutputStream(outputFile));
        outputStream.writeObject(exportedKb);
        outputStream.close();
    }
}
