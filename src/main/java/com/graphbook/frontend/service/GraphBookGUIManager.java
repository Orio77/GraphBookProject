package com.graphbook.frontend.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.graphbook.backend.service.impl.graphTools.PlotManager;
import com.graphbook.backend.service.impl.initializer.SafeGraphBookInitializer;
import com.graphbook.frontend.interfaces.IFileChooser;
import com.graphbook.server.ISimilarityClient;
import com.graphbook.server.impl.ApacheHTTP_SimilarityClient;
import com.graphbook.server.impl.PythonManager;

public class GraphBookGUIManager {
    private final IFileChooser fileChooser;
    private final IPdfHandler pdfHandler;
    private final IDatabase database;
    private final IDataManager dataManager;
    private final ISimilarityClient client;
    private final PlotManager plotManager;
    private GraphBookConfigManager configManager;
    private final PythonManager pythonManager;

    // default
    public GraphBookGUIManager() {
        fileChooser = new JFxFileChooserReworked();
        pdfHandler = new PDFBoxHandler();
        database = new NeoDatabase();
        dataManager = new JDataManager();
        client = new ApacheHTTP_SimilarityClient();
        plotManager = new PlotManager();
        configManager = new GraphBookConfigManager();
        pythonManager = new PythonManager();
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
        File savedDir = configManager.getSavedPdfsPath().toFile();
        if (savedDir.list().length == 0) {
            // Information that no pdfs are saved, please choose a pdf you'd like to save and work with
            return readPDF();
        }
        File savedPDF = fileChooser.chooseTXT(savedDir);
        String label = savedPDF.getName().substring(0, savedPDF.getName().indexOf('.'));
        return new Pair<>(dataManager.loadPDF(savedPDF), label);
    }

    public Pair<File, String> loadSavedScores() {
        

        File savedDir = configManager.getSavedPdfsPath().toFile();
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
        pythonManager.runPythonAIServer();
        
        Map<Integer, List<Pair<Integer, Double>>> res = client.getSimilarityBatchResponse(pdf, label);
        if (res == null) {
            throw new RuntimeException("Response is null");
        }

        Map<Integer, List<Pair<Integer, Double>>> filteredRes = new HashMap<>();

        res.entrySet().stream().forEach(entry -> {
            List<Pair<Integer, Double>> filteredList = entry.getValue().stream().filter(pair -> pair.getEl2() > similarityTreshold).collect(Collectors.toList());
            filteredRes.put(entry.getKey(), filteredList);
        });

        printEdgeValues(res);
        printEdgeValues(filteredRes);
        return filteredRes;
    }

    private List<Pair<Integer, Double>> getConceptScores(String concept, List<PDFText> pdf, String label) {
        pythonManager.runPythonAIServer();

        List<Pair<Integer, Double>> res = client.getConceptScores(pdf, label, concept);

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

    private void createEdges(String concept, List<Pair<Integer, Double>> conceptScores, String label) {
        database.createEdges(concept, conceptScores, label);
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

    public void addConcept() {
        Pair<List<PDFText>, String> pdf = loadSavedPDF();
        String concept = fileChooser.getUserInput("Concept", "Please enter the concept: ");

        List<Pair<Integer, Double>> scores = getConceptScores(concept, pdf.getEl1(), pdf.getEl2());
        createEdges(concept, scores, pdf.getEl2());
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

    public void createChart() {
        pythonManager.runPythonPlotServer();
        database.connect();

        List<String> conceptList = database.getConceptList();
        List<String> chosenConcepts = fileChooser.chooseConcepts(conceptList);
        System.out.println(chosenConcepts);
        Map<String, List<Pair<String, Double>>> res = database.getConceptNodes(chosenConcepts);
        database.disconnect();
        System.out.println(res);

        plotManager.showGraph(res);
        
        String chartLabel = fileChooser.getUserInput("Chart Label", "Please provide a label for the chart: ");
        plotManager.savePlotData(res, chartLabel);
    }

    public void showChart(String label) {
        pythonManager.runPythonPlotServer();

        plotManager.loadPlot(label);
    }

    public void showChart() {
        pythonManager.runPythonPlotServer();

        File chosenChart = fileChooser.chooseTXT(Paths.get(configManager.getProperty("GraphBookProject", "SavedPlotData")).toFile());
        plotManager.loadPlot(chosenChart);
    }

    public void initializeProject() {
        File projectDir = fileChooser.chooseDir();
        new SafeGraphBookInitializer(projectDir);
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
