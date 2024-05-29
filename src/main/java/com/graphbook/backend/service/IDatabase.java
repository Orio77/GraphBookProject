package com.graphbook.backend.service;

import java.util.List;
import java.util.Map;

import com.graphbook.backend.model.PDFText;
import com.graphbook.backend.model.Pair;

public interface IDatabase {
    
    void connect();
    void save(List<PDFText> texts, String label);
    void disconnect();
    void createEdges(Map<Integer, List<Pair<Integer, Double>>> result, String label);
    void reset();
}
