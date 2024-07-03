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

/**
 * The {@code PDFBoxHandler} class provides implementations for handling PDF files
 * using the Apache PDFBox library. It includes methods to extract text content
 * from PDF files and to split PDF files into individual pages, extracting text from each page.
 * <p>
 * This class implements the {@code IPdfHandler} interface.
 * </p>
 */
public class PDFBoxHandler implements IPdfHandler {
    private Logger logger = LogManager.getLogger(getClass());

    /**
     * Extracts the text content from a PDF file.
     *
     * @param pdf the PDF file from which to extract text.
     * @return a {@code PDFText} object containing the text extracted from the PDF.
     * @throws IllegalArgumentException if the PDF file is {@code null}.
     * @throws RuntimeException if an IOException occurs while processing the PDF.
     */
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
            throw new RuntimeException("IOException occurred while getting content of a PDF. Check the error log for details.", e);
        } finally {
            if (doc != null) {
                try {
                    doc.close();
                } catch (IOException e) {
                    logger.error(e);
                    throw new RuntimeException("IOException occurred while closing a PDF. Check the error log for details.", e);
                }
            }
        }
    }

    /**
     * Reads a PDF file, splits it into individual pages, and extracts text from each page.
     *
     * @param chosenPDF the PDF file to be read.
     * @return a list of {@code PDFText} objects, each containing text extracted from a page of the PDF.
     * @throws IllegalArgumentException if the chosen PDF file is {@code null}.
     * @throws RuntimeException if an IOException occurs while processing the PDF.
     */
    @Override
    public List<PDFText> read(File chosenPDF) {
        if (chosenPDF == null) {
            throw new IllegalArgumentException("Chosen PDF file cannot be null");
        }
        PDDocument pdf = null;
        try {
            pdf = PDDocument.load(chosenPDF);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException("IOException occurred while loading a PDF. Check the error log for details.", e);
        }

        Splitter splitter = new Splitter();
        List<PDDocument> pages;
        try {
            pages = splitter.split(pdf);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException("IOException occurred while splitting the PDF. Check the error log for details.", e);
        }

        List<PDFText> stringPages = new ArrayList<>();
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            for (PDDocument page : pages) {
                String textOfPage = stripper.getText(page);
                if (textOfPage.length() < 300) continue; // omit the little text pages
                stringPages.add(new PDFText(textOfPage));
            }
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException("IOException occurred while stripping the text. Document state may be invalid or encrypted. Check the error log for details.", e);
        } finally {
            if (pdf != null) {
                try {
                    pdf.close();
                } catch (IOException e) {
                    logger.error(e);
                    throw new RuntimeException("IOException occurred while closing a PDF. Check the error log for details.", e);
                }
            }
        }
        return stringPages;
    }
}
