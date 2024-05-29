package com.graphbook.frontend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphbook.backend.model.PDFText;
import com.graphbook.backend.model.Pair;
import com.graphbook.backend.service.IDataManager;
import com.graphbook.backend.service.IDatabase;
import com.graphbook.backend.service.IPdfHandler;
import com.graphbook.backend.service.impl.dataManagers.GraphBookConfigManager;
import com.graphbook.backend.service.impl.dataManagers.JDataManager;
import com.graphbook.backend.service.impl.dataManagers.PDFBoxHandler;
import com.graphbook.backend.service.impl.database.NeoDatabase;
import com.graphbook.backend.service.impl.initializer.SafeGraphBookInitializer;
import com.graphbook.frontend.interfaces.IFileChooser;
import com.graphbook.server.ISimilarityClient;
import com.graphbook.server.impl.ApacheHTTP_SimilarityClient;

public class GraphBookGUIManager {
    private final IFileChooser fileChooser;
    private final IPdfHandler pdfHandler;
    private final IDatabase database;
    private final IDataManager dataManager;
    private final ISimilarityClient client;

    // default
    public GraphBookGUIManager() {
        fileChooser = new JFxFileChooserReworked();
        pdfHandler = new PDFBoxHandler();
        database = new NeoDatabase();
        dataManager = new JDataManager();
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
        File savedDir = GraphBookConfigManager.getSavedPdfsPath().toFile();
        if (savedDir.list().length == 0) {
            // Information that no pdfs are saved, please choose a pdf you'd like to save and work with
            return readPDF();
        }
        File savedPDF = fileChooser.chooseTXT(savedDir);
        String label = savedPDF.getName().substring(0, savedPDF.getName().indexOf('.'));
        return new Pair<>(dataManager.loadPDF(savedPDF), label);
    }

    public Pair<File, String> loadSavedScores() {
        

        File savedDir = GraphBookConfigManager.getSavedPdfsPath().toFile();
        if (savedDir.list().length == 0) {
            // Information that no pdfs are saved, please choose a pdf you'd like to save and work with
            throw new RuntimeException("No saved scores");
        }
        File savedScores = fileChooser.chooseJSON(savedDir);
        String label = savedScores.getParentFile().getName();
        System.out.println(label); // TODO DELETE
        return new Pair<>(savedScores, label);
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

    public void createEdgesScoresCalculated() {

        Pair<File, String> savedScores = loadSavedScores();

        try {
            Map<Integer, List<Pair<Integer, Double>>> scores = new ObjectMapper().readValue(savedScores.getEl1(), new TypeReference<Map<Integer, List<Pair<Integer, Double>>>>(){});
            
            scores.entrySet().stream().forEach(entry -> {
                System.out.println("key: " + entry.getKey());
                System.out.println("val: " + entry.getValue());
            });

            createEdges(scores, savedScores.getEl2());
        } catch (IOException e) {
            throw new RuntimeException("Error reading scores");
        }   
    }

    public void initializeProject() {
        File projectDir = fileChooser.chooseDir();
        new SafeGraphBookInitializer(projectDir);
    }


    public void runPythonServer() {
        String pythonExecutableName = GraphBookConfigManager.getProperty("Python", "PythonExecutable");
        String pythonServerDir = GraphBookConfigManager.getProperty("Python", "PythonServerPath");
        String pythonFileName = GraphBookConfigManager.getProperty("Python", "PythonServerFileName");

        if (pythonExecutableName == null || pythonExecutableName.isEmpty()) {
            throw new RuntimeException("Retrieved Executable Name was null or empty");
        }
        if (pythonServerDir == null || pythonServerDir.isEmpty()) {
            throw new RuntimeException("Retrieved Python Server Directory was null or empty");
        }
        if (pythonFileName == null || pythonFileName.isEmpty()) {
            throw new RuntimeException("Retrieved Python File Name was null or empty");
        }


        ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutableName, pythonFileName); // TODO Handle null paths
        processBuilder.directory(Paths.get(pythonServerDir).toFile());
    
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
