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
    /**
     * A method that Handles the HTTP connection and sends a POST request with two texts in JSON format
     * Expected JSON format:
     * 
     * 
     * 
     * @param text1 Text1 for similarity score calculation
     * @param text2 Text2 for similarity score calculation
     * @return AI response containing the similarity score between two given texts
     */
    Object getSimilarityResponse(String text1, String text2);   
    
    Map<Integer, List<Pair<Integer, Double>>> getSimilarityBatchResponse(List<PDFText> pdf, String label);
}
