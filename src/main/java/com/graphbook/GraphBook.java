package com.graphbook;

import java.util.List;

import com.graphbook.elements.PDFText;
import com.graphbook.util.JDataSaver;
import com.graphbook.util.NeoDatabase;
import com.graphbook.util.PDFBoxReader;
import com.graphbook.util.AISimilarityCalculator;
import com.graphbook.util.interfaces.IDataSaver;
import com.graphbook.util.interfaces.IDatabase;
import com.graphbook.util.interfaces.IPdfReader;
import com.graphbook.util.interfaces.ISimilarityCalculator;

public class GraphBook {
    private final IPdfReader reader;
    private final IDatabase db;
    private final IDataSaver saver;
    private final ISimilarityCalculator calculator;

    // default
    public GraphBook() {
        reader = new PDFBoxReader();
        db = new NeoDatabase();
        saver = new JDataSaver();
        calculator = new AISimilarityCalculator();
    }
    
    public void readPDF() {
        List<PDFText> pages = reader.read(); // TODO adjust the window look and change the method to match the interface
        saver.savePDF(pages);
    }

    public void createGraph() {
        List<PDFText> pages = saver.loadPDF();
        db.save(pages);
        db.createAllEdges(pages, calculator, 80.0);
    }
}
