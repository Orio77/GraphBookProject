package com.graphbook.util.interfaces;

import com.graphbook.element.PDFText;

public interface ISimilarityCalculator {
    
    double calculate(PDFText text1, PDFText text2);
}
