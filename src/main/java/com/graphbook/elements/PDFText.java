package com.graphbook.elements;

public class PDFText {
    private String text;

    public PDFText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
