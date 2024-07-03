package com.graphbook.server;

import java.util.List;
import java.util.Map;

import com.graphbook.backend.model.PDFText;
import com.graphbook.backend.model.Pair;

public interface ISimilarityClient {
    
    Map<Integer, List<Pair<Integer, Double>>> getSimilarityBatchResponse(List<PDFText> pdf, String label);
    List<Pair<Integer, Double>> getConceptScores(List<PDFText> pdf, String label, String concept);
}
