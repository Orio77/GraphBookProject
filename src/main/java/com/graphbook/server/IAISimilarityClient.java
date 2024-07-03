package com.graphbook.server;

import java.util.List;
import java.util.Map;

import com.graphbook.backend.model.PDFText;
import com.graphbook.backend.model.Pair;

/**
 * An interface that defines a contract for classes that serve as Java server part.
 * These classes use AI for similarity score calculation.
 */
public interface IAISimilarityClient extends ISimilarityClient { 
    
    Map<Integer, List<Pair<Integer, Double>>> getSimilarityBatchResponse(List<PDFText> pdf, String label);
    List<Pair<Integer, Double>> getConceptScores(List<PDFText> pdf, String label, String concept);
}
