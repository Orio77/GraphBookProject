package com.graphbook.frontend;

import java.io.File;
import java.util.List;

import com.graphbook.elements.PDFText;
import com.graphbook.frontend.interfaces.IFileChooser;
import com.graphbook.util.CONSTANTS;
import com.graphbook.util.JDataManager;
import com.graphbook.util.NeoDatabase;
import com.graphbook.util.PDFBoxHandler;
import com.graphbook.util.SimpleGraphBookInitializer;
import com.graphbook.util.interfaces.IDataManager;
import com.graphbook.util.interfaces.IDatabase;
import com.graphbook.util.interfaces.IGraphBookInitializer;
import com.graphbook.util.interfaces.IPdfHandler;

public class GraphBookGUIManager {
    private final IFileChooser fileChooser;
    private final IPdfHandler pdfHandler;
    private final IDatabase database;
    private final IDataManager dataManager;
    private final IGraphBookInitializer initializer;

    // default
    public GraphBookGUIManager() {
        fileChooser = new JFxFileChooser();
        pdfHandler = new PDFBoxHandler();
        database = new NeoDatabase();
        dataManager = new JDataManager();
        initializer = new SimpleGraphBookInitializer();
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
    public void createEdges() {

        database.createAllEdges(null, null, 0);
    }
    // frontend Interface
    // Backend Interface



    // TODO Continue with Edge Creation - automatic checkpoint / chosen checkpoint with info of page recommended to continue from
    // frontend Interface
    // Backend Interface



    // TODO set Project path 
    public void setProjectPath() {
        File projectDir = fileChooser.chooseDir();
        initializer.setProjectPath(projectDir);
    }
    // frontend Interface               ____
    // Backend Interface                    \
//                                           \
                    //                          Merge -> void initializeProject()
//                                           /
    // TODO create necessary directories    /
    public void createNecessaryDirectories() {
        initializer.createNecessaryDirectories();
    }
    // frontend Interface
    // Backend Interface


    // TODO runPythonServer()
    // frontend Interface
    // Backend Interface

}
