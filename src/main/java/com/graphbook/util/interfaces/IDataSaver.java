package com.graphbook.util.interfaces;

import java.nio.file.Path;
import java.util.List;

import com.graphbook.elements.PDFText;

public interface IDataSaver {
    
    boolean savePDF(Object o, String name);
    boolean deleteAllSavedPDFs();
    List<PDFText> loadPDF();
    Path createDir(Path path);
    Path createFile(String pathAsString, String fileName);

}
