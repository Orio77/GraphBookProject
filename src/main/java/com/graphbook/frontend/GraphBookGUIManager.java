package com.graphbook.frontend;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.graphbook.backend.model.PDFText;
import com.graphbook.backend.model.Pair;
import com.graphbook.backend.service.IDataManager;
import com.graphbook.backend.service.IDatabase;
import com.graphbook.backend.service.IGraphBookInitializer;
import com.graphbook.backend.service.IPdfHandler;
import com.graphbook.backend.service.impl.dataManagers.JDataManager;
import com.graphbook.backend.service.impl.dataManagers.PDFBoxHandler;
import com.graphbook.backend.service.impl.database.NeoDatabase;
import com.graphbook.backend.service.impl.initializer.SimpleGraphBookInitializer;
import com.graphbook.frontend.interfaces.IFileChooser;
import com.graphbook.server.ISimilarityClient;
import com.graphbook.server.impl.ApacheHTTP_SimilarityClient;
import com.graphbook.util.CONSTANTS;

public class GraphBookGUIManager {
    private final IFileChooser fileChooser;
    private final IPdfHandler pdfHandler;
    private final IDatabase database;
    private final IDataManager dataManager;
    private final IGraphBookInitializer initializer;
    private final ISimilarityClient client;

    // default
    public GraphBookGUIManager() {
        fileChooser = new JFxFileChooserReworked();
        pdfHandler = new PDFBoxHandler();
        database = new NeoDatabase();
        dataManager = new JDataManager();
        initializer = new SimpleGraphBookInitializer();
        client = new ApacheHTTP_SimilarityClient(null);
    }

    

    // TODO Read pdf
    public Pair<List<PDFText>, String> readPDF() {
        File pdf = fileChooser.choosePDF();
        String pdfName = fileChooser.getUserInput("Naming PDF", "Please enter a name for your pdf: ");
        List<PDFText> cleanPDF = pdfHandler.read(pdf);
        dataManager.savePDF(cleanPDF, pdfName);
        return new Pair<>(cleanPDF, pdfName);
    }



    public Pair<List<PDFText>, String> loadSavedPDF() {
        File savedDir = CONSTANTS.SAVED_PDFS_PATH.toFile();
        if (savedDir.list().length == 0) {
            // Information that no pdfs are saved, please choose a pdf you'd like to save and work with
            return readPDF();
        }
        File savedPDF = fileChooser.chooseTXT(CONSTANTS.SAVED_PDFS_PATH.toFile());
        String label = savedPDF.getName().substring(0, savedPDF.getName().indexOf('.'));
        return new Pair<>(dataManager.loadPDF(savedPDF), label);
    }

    // TODO Start Process of Edge Creation
    private Map<Integer, List<Pair<Integer, Double>>> getEdgeValues(List<PDFText> pdf, String label, Double similarityTreshold) {
        runPythonServer();
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Map<Integer, List<Pair<Integer, Double>>> res = client.getSimilarityBatchResponse(pdf, label);
        if (res == null) {
            throw new RuntimeException("Response is null");
        }
        System.out.println(res.getClass().getName());
        System.out.println(res.getClass());
        System.out.println(res.getClass().getCanonicalName());
        System.out.println(res.getClass().getTypeName());
        
        // HashMap<Integer, List<List<Double>>> castedRes = responseHandler.handle(res);
        // HashMap<Integer, List<List<Double>>> filteredRes = new HashMap<>();

        

        // castedRes.entrySet().stream().forEach(entry -> {
        //     System.out.println("entry key: " + entry.getKey().getClass().getName());
        //     System.out.println("entry val: " + entry.getValue().getClass().getName());
        //     List<List<Double>> filteredPairs = entry.getValue().stream().filter(pair -> {
        //         System.out.println("Name of pair.get(0): " + pair.get(0).getClass().getName());
        //         System.out.println("Name of pair.get(1): " + pair.get(1).getClass().getName());
        //         return pair.get(1) >= similarityTreshold;
        //     }).collect(Collectors.toList());
        //     filteredRes.put(entry.getKey(), filteredPairs);
        // });

        printEdgeValues(res);
        return res;
    }

    private void printEdgeValues(Map<Integer, List<Pair<Integer, Double>>> edges) {
        edges.entrySet().stream().forEach(entry -> {
            System.out.println("Key: " + entry.getKey());
            System.out.println("Value: " + entry.getValue().toString());
        });
    }

    private void createEdges(Map<Integer, List<Pair<Integer, Double>>> res, String label) {
        database.createEdges(res, label);
    }

    // default
    public void createGraph() {
        Pair<List<PDFText>, String> pdf = loadSavedPDF();

        database.save(pdf.getEl1(), pdf.getEl2());
        Map<Integer, List<Pair<Integer, Double>>> edges = getEdgeValues(pdf.getEl1(), pdf.getEl2(), 80.0);
        createEdges(edges, pdf.getEl2());
    }

    public void createGraph(double similarityTreshold) {
        Pair<List<PDFText>, String> pdf = loadSavedPDF();

        database.save(pdf.getEl1(), pdf.getEl2());
        Map<Integer, List<Pair<Integer, Double>>> edges = getEdgeValues(pdf.getEl1(), pdf.getEl2(), similarityTreshold);
        createEdges(edges, pdf.getEl2());
    }


    // TODO Continue with Edge Creation - automatic checkpoint / chosen checkpoint with info of page recommended to continue from
    public Map<Integer, List<Pair<Integer, Double>>> continueWithEdgeCreation(List<PDFText> pdf) {
        return null;
    }



    public void setProjectPath() {
        File projectDir = fileChooser.chooseDir();
        initializer.setProjectPath(projectDir);
    }

    public void createNecessaryDirectories() {
        initializer.createNecessaryDirectories();
    }


    public void initializeProject() {
        setProjectPath();
        createNecessaryDirectories();
    }


    public void runPythonServer() {
        ProcessBuilder processBuilder = new ProcessBuilder(CONSTANTS.PYTHON_EXECUTABLE.toString(), CONSTANTS.PYTHON_SERVER_FILENAME); // TODO Handle null paths
        processBuilder.directory(CONSTANTS.PYTHON_SERVER_DIR.toFile());
    
        // Redirect the process's output to the Java program's output
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
    

        // Start the process and keep it running
        try {
            processBuilder.start(); // Possible to keep the Process object to control the running server | return it?
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exitGUI() {
        fileChooser.exit();
    }
    // frontend Interface
    // Backend Interface

    public void clearDatabase() {
        database.reset();
    }
}
