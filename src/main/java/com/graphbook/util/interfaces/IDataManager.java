package com.graphbook.util.interfaces;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import com.graphbook.elements.PDFText;

public interface IDataManager {
    
    boolean savePDF(Object o, String name);
    boolean deleteAllSavedPDFs();
    List<PDFText> loadPDF(File savedPDF);
    Path createDir(Path path);
    Path createFile(String pathAsString, String fileName);

}
