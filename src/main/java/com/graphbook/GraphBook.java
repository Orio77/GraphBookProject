package com.graphbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;

import com.graphbook.elements.PDFText;
import com.graphbook.frontend.InteractivePathChooser;
import com.graphbook.server.ApacheHTTP_SimilarityClient;
import com.graphbook.util.AISimilarityCalculator;
import com.graphbook.util.CONSTANTS;
import com.graphbook.util.JDataSaver;
import com.graphbook.util.NeoDatabase;
import com.graphbook.util.PDFBoxReader;
import com.graphbook.util.SimpleScoreExtractor;
import com.graphbook.util.interfaces.IAIResponseSimilarityScoreExtractor;
import com.graphbook.util.interfaces.IAISimilarityClient;
import com.graphbook.util.interfaces.IDataSaver;
import com.graphbook.util.interfaces.IDatabase;
import com.graphbook.util.interfaces.IPdfReader;
import com.graphbook.util.interfaces.ISimilarityCalculator;

// TODO Make the user create the path for the Code at the beginning, store all the error log folders and others there
public class GraphBook {
    private final IPdfReader reader;
    private final IDatabase db;
    private final IDataSaver saver;
    private final IAIResponseSimilarityScoreExtractor extractor;
    private final IAISimilarityClient client;
    private final ISimilarityCalculator calculator;

    // default
    public GraphBook() {
        reader = new PDFBoxReader();
        db = new NeoDatabase();
        saver = new JDataSaver();
        extractor = new SimpleScoreExtractor();
        client = new ApacheHTTP_SimilarityClient(extractor);
        calculator = new AISimilarityCalculator(client);
        // setProjectPath();
        // createNecessaryDirectories();
    }

    public GraphBook(IPdfReader reader, IDatabase db, IDataSaver saver, ISimilarityCalculator calculator, IAIResponseSimilarityScoreExtractor extractor, IAISimilarityClient client) {
        this.reader = reader;
        this.db = db;
        this.saver = saver;
        this.calculator = calculator;
        this.extractor = extractor;
        this.client = client;
        // setProjectPath();
        // createNecessaryDirectories();
    }



    public void readPDF() {
        List<PDFText> pages = reader.read(); // TODO adjust the window look and change the method to match the interface
        String pdfName = InteractivePathChooser.getPDFName();
        saver.savePDF(pages, pdfName);
    }

    public void createGraph(int similarityTreshold) {
        List<PDFText> pages = saver.loadPDF();
        runPythonServer();
        db.save(pages);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        db.createAllEdges(pages, calculator, similarityTreshold);
    }

    // Default
    public void createGraph() {
        List<PDFText> pages = saver.loadPDF();
        runPythonServer();
        // db.save(pages); // TODO uncomment
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        db.createAllEdges(pages, calculator, 80.0);
    }

    private void setProjectPath() { 
        JOptionPane.showMessageDialog(null, "Please choose a folder for the project", "Info", JOptionPane.INFORMATION_MESSAGE);
        Path chosenPath = new InteractivePathChooser().chooseDirectory().toPath();
        if (chosenPath == null) {
            throw new RuntimeException("You need to chose a folder for the project for it to run conrrectly");
        }

        Properties properties = new Properties();
        properties.setProperty("PROJECT_PATH", chosenPath.toString());
        File configDir = new File(CONSTANTS.CONFIG_PATH.toString());
        if (!configDir.exists()) {
            configDir.mkdir();
        }
        try (FileOutputStream out = new FileOutputStream(CONSTANTS.CONFIG_PATH.toString())) {
            properties.store(out, null);
        } catch (IOException e) {
            LogManager.getLogger(GraphBook.class).error("IOException occured while writing file into the properties file", e.getMessage(), e);
        }
    }

    private void createNecessaryDirectories() {
        File savedPdfsDirectory = new File(CONSTANTS.SAVED_PDFS_PATH.toString());
        if (!savedPdfsDirectory.exists()) {
            savedPdfsDirectory.mkdir();
        }
    }

    public void runPythonServer() {
        ProcessBuilder processBuilder = new ProcessBuilder("C:/Users/macie/anaconda3/envs/GraphBookProjectPyEnv/python.exe", "python_server.py");
        processBuilder.directory(CONSTANTS.PYTHON_SERVER_PATH.toFile());
    
        // Redirect the process's output to the Java program's output
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
    

        // Start the process and keep it running
        try {
            Process process = processBuilder.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public static void main(String[] args) {
        GraphBook gb = new GraphBook();
        gb.createGraph();
    }
}
