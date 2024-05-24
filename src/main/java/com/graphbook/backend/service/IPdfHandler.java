package com.graphbook.backend.service;

import java.io.File;
import java.util.List;

import com.graphbook.backend.model.PDFText;

public interface IPdfHandler {
    
    public List<PDFText> read(File pdf);
}
