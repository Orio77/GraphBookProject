package com.graphbook.backend.service.impl.dataManagers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.graphbook.backend.model.PDFText;
import com.graphbook.backend.service.IPdfHandler;

public class PDFBoxHandler implements IPdfHandler{

    public PDFText getContent(File pdf) {
        try {
            PDDocument doc = PDDocument.load(pdf);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);
            return new PDFText(text);
        } catch (IOException e) {
            e.printStackTrace();
            return new PDFText(e.getMessage());
        }
    }

    // TODO Change the Read method to take in a File, and let it be used in the frontend code
    @Override
    public List<PDFText> read(File chosenPDF) {
        // read the doc
        PDDocument pdf = null;
        try {
            pdf = PDDocument.load(chosenPDF);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // split the pages
        Splitter splitter = new Splitter();
        List<PDDocument> pages = null;
        try {
            pages = splitter.split(pdf);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // get the text out of pages
        List<PDFText> stringPages = new ArrayList<>();
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            for (PDDocument page : pages) {
                try {
                    String textOfPage = stripper.getText(page);
                    if (textOfPage.length() < 300) continue; // ommitt the little text pages
                    stringPages.add(new PDFText(textOfPage));
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return stringPages;
    }
}
