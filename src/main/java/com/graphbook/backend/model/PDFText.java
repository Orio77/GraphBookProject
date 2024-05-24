package com.graphbook.backend.model;

import java.io.Serializable;

/**
 * PDFText is a class representing a text element in a PDF
 */
public class PDFText implements Serializable{
    private final String text;

    /**
     * Constructs a new PDFText object
     * @param text The text content. Must not be null or empty
     */
    public PDFText(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Provided a null or empty text");
        }
        this.text = text;
    }

    /**
     * Returns the text content
     * @return The text content
     */
    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
