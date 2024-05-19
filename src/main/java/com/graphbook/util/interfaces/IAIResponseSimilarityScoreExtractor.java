package com.graphbook.util.interfaces;

/**
 * An interface for classes that extract similarity score out of AI responses.
 * The score is restricted to a range between 0.0 and 100.0.
 */
public interface IAIResponseSimilarityScoreExtractor {
    /**
     * A method that extracts score out of an AI response.
     * A higher score indicates a higher degree of similarity
     * 
     * @param response AI generated text as an answer to a query
     * @return Similarity score as double between 0.0 and 100.0
     */
    double extract(String response);
}
