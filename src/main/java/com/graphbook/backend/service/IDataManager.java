package com.graphbook.backend.service;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import com.graphbook.backend.model.PDFText;

public interface IDataManager {
    
    boolean savePDF(Object o, String name);
    boolean savePlot(Object o, String label);
    boolean deleteAllSavedPDFs();
    List<PDFText> loadPDF(File savedPDF);
    Path createDir(Path path);
    Path createFile(String pathAsString, String fileName);
    Object readSavedPDF(File file);
    Object readSavedPlot(File file);

}
