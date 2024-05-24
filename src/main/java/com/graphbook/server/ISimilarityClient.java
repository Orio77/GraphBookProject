package com.graphbook.server;

import java.util.List;

import com.graphbook.backend.model.PDFText;

public interface ISimilarityClient {
    
    Object getSimilarityResponse(String text1, String text2);
    Object getSimilarityBatchResponse(List<PDFText> pdf, String label);
}
