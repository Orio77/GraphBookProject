package com.graphbook.util.interfaces;

import com.graphbook.elements.PDFText;

public interface ISimilarityCalculator {
    
    double calculate(PDFText text1, PDFText text2);
}
