package com.graphbook.util.interfaces;

import java.util.List;

import com.graphbook.elements.PDFText;

public interface IPdfReader {
    
    public List<PDFText> read();
}
