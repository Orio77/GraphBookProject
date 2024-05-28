package com.graphbook.server;

import java.util.List;
import java.util.Map;

import com.graphbook.backend.model.PDFText;
import com.graphbook.backend.model.Pair;

public interface ISimilarityClient {
    
    Object getSimilarityResponse(String text1, String text2);
    Map<Integer, List<Pair<Integer, Double>>> getSimilarityBatchResponse(List<PDFText> pdf, String label);
}
