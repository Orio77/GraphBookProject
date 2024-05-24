package com.graphbook.util.interfaces;

import java.util.List;

import com.graphbook.element.PDFText;

public interface ISimilarityClient {
    
    Object getSimilarityResponse(String text1, String text2);
    Object getSimilarityBatchResponse(List<PDFText> pdf, String label);
}
