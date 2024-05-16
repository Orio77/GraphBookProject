package com.graphbook.util.interfaces;

import java.util.List;

import com.graphbook.elements.PDFText;

public interface IDatabase {
    
    void connect();
    void save(List<PDFText> texts);
    void disconnect();
    void createAllEdges(List<PDFText> texts, ISimilarityCalculator calculator, double similarityTreshold);
}
