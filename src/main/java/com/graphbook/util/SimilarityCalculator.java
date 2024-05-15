package com.graphbook.util;

import com.graphbook.elements.PDFText;
import com.graphbook.server.SimilarityClient;
import com.graphbook.util.interfaces.ISimilarityCalculator;



public class SimilarityCalculator implements ISimilarityCalculator {

    @Override
    public double calculate(PDFText text1, PDFText text2) {
        Object potentialScore = SimilarityClient.getSimilarityResponse(text1.getText(), text2.getText());
        if (potentialScore instanceof Double) {
            return (Double) potentialScore;
        }
        else return -1;
    }
    
    
}
