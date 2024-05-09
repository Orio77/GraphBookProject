package com.graphbook.util.interfaces;

import java.net.URI;

import com.graphbook.elements.PDFText;

public interface IPdfReader {
    
    public PDFText read(URI absolutePath);
}
