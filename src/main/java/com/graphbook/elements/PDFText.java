package com.graphbook.elements;

import java.io.Serializable;

public class PDFText implements Serializable{
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
