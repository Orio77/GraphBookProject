package com.graphbook.util.interfaces;

import java.util.List;

import com.graphbook.elements.PDFText;

public interface IDataSaver {
    
    public boolean savePDF(Object o);
    public boolean deleteAllSavedPDFs();
    public List<PDFText> loadPDF();

}
