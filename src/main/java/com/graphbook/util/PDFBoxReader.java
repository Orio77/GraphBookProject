package com.graphbook.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.graphbook.elements.PDFText;
import com.graphbook.frontend.InteractivePathChooser;
import com.graphbook.util.interfaces.IPdfReader;

public class PDFBoxReader implements IPdfReader{

    public PDFText readAsString(URI absolutePath) {
        try {
            PDDocument doc = PDDocument.load(new File(absolutePath));
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);
            return new PDFText(text);
        } catch (IOException e) {
            e.printStackTrace();
            return new PDFText(e.getMessage());
        }
    }

    // method that takes a path as an argument and returns a List of pages in a String form
    // Exceptions are to be handled better
    public List<PDFText> readWithPath(String path) {
        URI uriPath = null;
        String beginning = "file:///";
        String completePath = beginning.concat(path);
        try {
            uriPath = new URI(completePath);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }

        // read the doc
        PDDocument pdf = null;
        try {
            pdf = PDDocument.load(new File(uriPath));
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

    @Override
    public List<PDFText> read() {
        File chosenPDF = new InteractivePathChooser().choosePDF();

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
