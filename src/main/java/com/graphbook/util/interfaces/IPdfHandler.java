package com.graphbook.util.interfaces;

import java.io.File;
import java.util.List;

import com.graphbook.element.PDFText;

public interface IPdfHandler {
    
    public List<PDFText> read(File pdf);
}
