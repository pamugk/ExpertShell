package transfer;

import base.knowledgebase.KnowledgeBase;
import transfer.interfaces.KnowledgeBaseImporter;

import java.io.*;
import java.util.Locale;

public class BinKnowledgeBaseImporter implements KnowledgeBaseImporter {
    @Override
    public String getDescription(Locale usedLocale) {
        return "Сериализует базу знаний из бинарного файла";
    }
    @Override
    public String getFileExtension() { return "bkb"; }
    @Override
    public KnowledgeBase importKnowledgeBase(File inputFile) throws IOException, ClassNotFoundException {
        var inputStream = new ObjectInputStream(new FileInputStream(inputFile));
        KnowledgeBase kb = (KnowledgeBase) inputStream.readObject();
        inputStream.close();
        return kb;
    }
}
