package com.graphbook.frontend;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.graphbook.element.PDFText;
import com.graphbook.frontend.interfaces.IFileChooser;
import com.graphbook.server.ApacheHTTP_SimilarityClient;
import com.graphbook.server.SimpleResponseHandler;
import com.graphbook.util.CONSTANTS;
import com.graphbook.util.JDataManager;
import com.graphbook.util.NeoDatabase;
import com.graphbook.util.PDFBoxHandler;
import com.graphbook.util.SimpleGraphBookInitializer;
import com.graphbook.util.interfaces.IDataManager;
import com.graphbook.util.interfaces.IDatabase;
import com.graphbook.util.interfaces.IGraphBookInitializer;
import com.graphbook.util.interfaces.IPdfHandler;
import com.graphbook.util.interfaces.IResponseHandler;
import com.graphbook.util.interfaces.ISimilarityClient;

import javafx.stage.Stage;

public class GraphBookGUIManager {
    private final IFileChooser fileChooser;
    private final IPdfHandler pdfHandler;
    private final IDatabase database;
    private final IDataManager dataManager;
    private final IGraphBookInitializer initializer;
    private final ISimilarityClient client;
    private final IResponseHandler responseHandler;

    // default
    public GraphBookGUIManager(Stage primaryStage) {
        fileChooser = new JFxFileChooserReworked(primaryStage);
        pdfHandler = new PDFBoxHandler();
        database = new NeoDatabase();
        dataManager = new JDataManager();
        initializer = new SimpleGraphBookInitializer();
        client = new ApacheHTTP_SimilarityClient(null);
        responseHandler = new SimpleResponseHandler();
    }

    

    // TODO Read pdf
    public List<PDFText> readPDF() {
        File pdf = fileChooser.choosePDF();
        String pdfName = fileChooser.getUserInput("Naming PDF", "Please enter a name for your pdf: ");
        dataManager.savePDF(pdf, pdfName);
        return pdfHandler.read(pdf);
    }
    // frontend Interface
    // Backend Interface


    // TODO load saved pdf
    public List<PDFText> loadSavedPDF() {
        File savedDir = CONSTANTS.SAVED_PDFS_PATH.toFile();
        if (savedDir.list().length == 0) {
            // Information that no pdfs are saved, please choose a pdf you'd like to save and work with
            return readPDF();
        }
        File savedPDF = fileChooser.chooseTXT(CONSTANTS.SAVED_PDFS_PATH.toFile());
        return dataManager.loadPDF(savedPDF);
    }
    // frontend Interface
    // Backend Interface



    // TODO Save Nodes in DB
    public void savePdfToDatabse() {
        List<PDFText> loadedPDF = loadSavedPDF();
        String label = fileChooser.getUserInput("PDF Tag", "Please provide a pdf tag/name for you to recognize it later in the graph: ");
        database.save(loadedPDF, label);
    }
    // frontend Interface
    // Backend Interface



    // TODO Start Process of Edge Creation
    public HashMap<Integer, List<List<Double>>> getEdgeValues(List<PDFText> pdf) {
        // runPythonServer();
        // try {
        //     Thread.sleep(8000);
        // } catch (InterruptedException e) {
        //     Thread.currentThread().interrupt();
        // }
        String label = fileChooser.getUserInput("PDF ID", "Please provide a shortcut for your pdf, it will serve as an unique ID (3-8) letters long: ");
        Object res = client.getSimilarityBatchResponse(pdf, label);
        return responseHandler.handle(res);
    }
    // frontend Interface
    // Backend Interface



    // TODO Continue with Edge Creation - automatic checkpoint / chosen checkpoint with info of page recommended to continue from
    public HashMap<Integer, List<List<Double>>> continueWithEdgeCreation(List<PDFText> pdf) {
        return null;
    }
    // frontend Interface
    // Backend Interface



    // TODO set Project path 
    public void setProjectPath() {
        File projectDir = fileChooser.chooseDir();
        initializer.setProjectPath(projectDir);
    }
    // frontend Interface               ____
    // Backend Interface                    /
//                                           /
                    //                          Merge -> void initializeProject()
//                                           /
    // TODO create necessary directories    /
    public void createNecessaryDirectories() {
        initializer.createNecessaryDirectories();
    }
    // frontend Interface
    // Backend Interface

    // TODO Initialize Project
    public void initializeProject() {
        setProjectPath();
        createNecessaryDirectories();
    }


    // TODO runPythonServer()
    public void runPythonServer() {
        ProcessBuilder processBuilder = new ProcessBuilder(CONSTANTS.PYTHON_SERVER_PATH.toString(), CONSTANTS.PYTHON_SERVER_FILENAME); // TODO Handle null paths
        processBuilder.directory(CONSTANTS.PYTHON_SERVER_PATH.toFile());
    
        // Redirect the process's output to the Java program's output
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
    

        // Start the process and keep it running
        try {
            processBuilder.start(); // Possible to keep the Process object to control the running server | return it?
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    // frontend Interface
    // Backend Interface
}
