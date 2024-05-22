package com.graphbook.util.interfaces;

import java.io.File;
import java.util.List;

import com.graphbook.elements.PDFText;

public interface IPdfHandler {
    
    public List<PDFText> read(File pdf);
}
