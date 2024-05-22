package com.graphbook.util;

import com.graphbook.util.interfaces.IAIResponseSimilarityScoreExtractor;

public class SimpleScoreExtractor implements IAIResponseSimilarityScoreExtractor {
    
    @Override
    public double extract(String response) {
        char[] digits = new char[5];
        int i = 0;
        boolean decimalPointEncountered = false;

        while (i < 5 && (response.charAt(i) == '.' || Character.isDigit(response.charAt(i)))) {
            if (response.charAt(i) == '.') {
                if (decimalPointEncountered) break;
                else decimalPointEncountered = true;
            }
            digits[i] = response.charAt(i);
            i++;
        }

        if (i == 0) {
            return -1;
        }
        else {
            return Double.parseDouble(new String(digits, 0, i));
        }
    }    
}
