package com.graphbook.backend.service;

import java.util.List;

import com.graphbook.backend.model.PDFText;

public interface IDatabase {
    
    void connect();
    void save(List<PDFText> texts, String label);
    void disconnect();
    void createAllEdges(List<PDFText> texts, double similarityTreshold);
}
