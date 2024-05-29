package com.graphbook.backend.service.impl.dataManagers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.graphbook.backend.model.PDFText;
import com.graphbook.backend.service.IPdfHandler;

public class PDFBoxHandler implements IPdfHandler{
    private Logger logger = LogManager.getLogger(getClass());

    public PDFText getContent(File pdf) {
        if (pdf == null) {
            throw new IllegalArgumentException("PDF file cannot be null");
        }
        PDDocument doc = null;
        try {
            doc = PDDocument.load(pdf);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);
            return new PDFText(text);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException("IOException occured while getting content of a pdf. Check the error log for details.");
        }
        finally {
            if (doc != null) {
                try {
                    doc.close();
                } catch (IOException e) {
                    logger.error(e);
                    throw new RuntimeException("IOException occured while closing a pdf. Check the error log for details.");
                }
            }
        }
    }

    // TODO Change the Read method to take in a File, and let it be used in the frontend code
    @Override
    public List<PDFText> read(File chosenPDF) {
        if (chosenPDF == null) {
            throw new IllegalArgumentException("Chosen PDF file cannot be null");
        }
        // read the doc
        PDDocument pdf = null;
        try {
            pdf = PDDocument.load(chosenPDF);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException("IOException occured while loading a pdf. Check the error log for details.");
        }

        // split the pages
        Splitter splitter = new Splitter();
        List<PDDocument> pages = null;
        try {
            pages = splitter.split(pdf);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException("IOException occured while splitting the pdf. Check the error log for details.");
        }

        // get the text out of pages
        List<PDFText> stringPages = new ArrayList<>();
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            for (PDDocument page : pages) {
                String textOfPage = stripper.getText(page);
                if (textOfPage.length() < 300) continue; // ommitt the little text pages
                stringPages.add(new PDFText(textOfPage));
            }
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException("IOException occured while stripping the text doc state may ve invalid or encrypted. Check the error log for details.");
        }
        finally {
            if (pdf != null) {
                try {
                    pdf.close();
                } catch (IOException e) {
                    logger.error(e);
                    throw new RuntimeException("IOException occured while closing a pdf. Check the error log for details.");
                }
            }
        }
        return stringPages;
    }
}
