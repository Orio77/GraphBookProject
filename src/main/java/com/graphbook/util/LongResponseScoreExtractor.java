package com.graphbook.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.graphbook.util.interfaces.IAIResponseSimilarityScoreExtractor;

public class LongResponseScoreExtractor implements IAIResponseSimilarityScoreExtractor {
    
    @Override
    public double extract(String response) {
        Pattern pattern = Pattern.compile("Score: ([0-9]*\\.?[0-9]+)");
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String scoreTextString = matcher.group();
            String scoreString = scoreTextString.substring(scoreTextString.indexOf(" ") + 1);
            double score = Double.parseDouble(scoreString);
            return score;
        }
        else return -1;
    }
}
