package com.graphbook.util;

import com.graphbook.element.PDFText;
import com.graphbook.util.interfaces.ISimilarityCalculator;
import com.graphbook.util.interfaces.ISimilarityClient;



public class AISimilarityCalculator implements ISimilarityCalculator {
    private final ISimilarityClient client;

    public AISimilarityCalculator(ISimilarityClient client) {
        this.client = client;
    }


    @Override
    public double calculate(PDFText text1, PDFText text2) {
        Object potentialScore = client.getSimilarityResponse(text1.getText(), text2.getText());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
        if (potentialScore instanceof Double) {
            return (Double) potentialScore;
        }
        else return -1;
    }
}
