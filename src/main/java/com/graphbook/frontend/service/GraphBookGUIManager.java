package com.graphbook.frontend.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import com.graphbook.backend.service.impl.plotTools.PlotManager;
import com.graphbook.frontend.interfaces.IFileChooser;
import com.graphbook.server.ISimilarityClient;
import com.graphbook.server.impl.ApacheHTTP_SimilarityClient;
import com.graphbook.server.impl.PythonManager;
import com.graphbook.util.InputParser;

/**
 * Manages the GraphBook GUI interactions and operations.
 * Provides methods to handle PDF files, create graphs, manage databases, and
 * generate charts.
 */
public class GraphBookGUIManager {
    private final IFileChooser fileChooser;
    private final IPdfHandler pdfHandler;
    private final IDatabase database;
    private final IDataManager dataManager;
    private final ISimilarityClient client;
    private final PlotManager plotManager;
    private GraphBookConfigManager configManager;
    private final PythonManager pythonManager;

    /**
     * Default constructor initializing all dependencies with their default
     * implementations.
     */
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

    /**
     * Constructor allowing custom implementations of dependencies.
     *
     * @param fileChooser   the file chooser implementation
     * @param pdfHandler    the PDF handler implementation
     * @param database      the database implementation
     * @param dataManager   the data manager implementation
     * @param client        the similarity client implementation
     * @param plotManager   the plot manager implementation
     * @param configManager the configuration manager implementation
     * @param pythonManager the Python manager implementation
     */
    public GraphBookGUIManager(IFileChooser fileChooser, IPdfHandler pdfHandler, IDatabase database,
            IDataManager dataManager, ISimilarityClient client, PlotManager plotManager,
            GraphBookConfigManager configManager, PythonManager pythonManager) {
        this.fileChooser = fileChooser;
        this.pdfHandler = pdfHandler;
        this.database = database;
        this.dataManager = dataManager;
        this.client = client;
        this.plotManager = plotManager;
        this.configManager = configManager;
        this.pythonManager = pythonManager;
    }

    /**
     * Constructor for minimal initialization.
     *
     * @param fileChooser   the file chooser implementation
     * @param pdfHandler    the PDF handler implementation
     * @param dataManager   the data manager implementation
     * @param configManager the configuration manager implementation
     */
    public GraphBookGUIManager(IFileChooser fileChooser, IPdfHandler pdfHandler, IDataManager dataManager,
            GraphBookConfigManager configManager) {
        this.fileChooser = fileChooser;
        this.pdfHandler = pdfHandler;
        this.dataManager = dataManager;
        this.configManager = configManager;
        database = new NeoDatabase();
        client = new ApacheHTTP_SimilarityClient();
        plotManager = new PlotManager();
        pythonManager = new PythonManager();
    }

    /**
     * Reads a PDF file chosen by the user and saves it.
     *
     * @return a pair containing the list of PDF texts and the name of the PDF
     */
    public Pair<List<PDFText>, String> readPDF() {
        File pdf = fileChooser.choosePDF();
        if (pdf == null) {
            return null;
        }
        String pdfName = fileChooser.getUserInput("Naming PDF", "Please enter a name for your pdf: ");
        List<PDFText> cleanPDF = pdfHandler.read(pdf);
        dataManager.savePDF(cleanPDF, pdfName);
        return new Pair<>(cleanPDF, pdfName);
    }

    /**
     * Loads a saved PDF file chosen by the user.
     *
     * @return a pair containing the list of PDF texts and the label of the PDF
     */
    public Pair<List<PDFText>, String> loadSavedPDF() {
        File savedDir = configManager.getSavedPdfsPath().toFile();
        if (savedDir.list().length == 0) {
            return readPDF();
        }
        File savedPDF = fileChooser.chooseTXT(savedDir);
        if (savedPDF == null) {
            return null;
        }
        String label = savedPDF.getName().substring(0, savedPDF.getName().indexOf('.'));
        return new Pair<>(dataManager.loadPDF(savedPDF), label);
    }

    /**
     * Loads saved scores from a JSON file chosen by the user.
     *
     * @return a pair containing the scores file and the label
     */
    public Pair<File, String> loadSavedScores() {
        File savedDir = configManager.getSavedPdfsPath().toFile();
        if (savedDir.list().length == 0) {
            throw new RuntimeException("No saved scores");
        }
        File savedScores = fileChooser.chooseJSON(savedDir);
        String label = savedScores.getParentFile().getName();
        return new Pair<>(savedScores, label);
    }

    /**
     * Retrieves edge values based on similarity threshold.
     *
     * @param pdf                 the list of PDF texts
     * @param label               the label of the PDF
     * @param similarityThreshold the similarity threshold
     * @return a map of edge values
     */
    private Map<Integer, List<Pair<Integer, Double>>> getEdgeValues(List<PDFText> pdf, String label,
            Double similarityThreshold) {
        Map<Integer, List<Pair<Integer, Double>>> res = client.getSimilarityBatchResponse(pdf, label);
        if (res == null) {
            throw new RuntimeException("Response is null");
        }
        Map<Integer, List<Pair<Integer, Double>>> filteredRes = new HashMap<>();
        res.entrySet().stream().forEach(entry -> {
            List<Pair<Integer, Double>> filteredList = entry.getValue().stream()
                    .filter(pair -> pair.getEl2() > similarityThreshold).collect(Collectors.toList());
            filteredRes.put(entry.getKey(), filteredList);
        });
        printEdgeValues(res);
        printEdgeValues(filteredRes);
        return filteredRes;
    }

    /**
     * Retrieves concept scores.
     *
     * @param concept the concept to score
     * @param pdf     the list of PDF texts
     * @param label   the label of the PDF
     * @return a list of concept scores
     */
    private List<Pair<Integer, Double>> getConceptScores(String concept, List<PDFText> pdf, String label) {
        pythonManager.runPythonAIServer();
        return client.getConceptScores(pdf, label, concept);
    }

    /**
     * Prints edge values.
     *
     * @param edges the map of edge values
     */
    private void printEdgeValues(Map<Integer, List<Pair<Integer, Double>>> edges) {
        edges.entrySet().stream().forEach(entry -> {
            System.out.println("Key: " + entry.getKey());
            System.out.println("Value: " + entry.getValue().toString());
        });
    }

    /**
     * Creates edges in the database.
     *
     * @param res   the map of edge values
     * @param label the label of the PDF
     */
    private void createEdges(Map<Integer, List<Pair<Integer, Double>>> res, String label) {
        database.createEdges(res, label);
    }

    /**
     * Creates edges for a specific concept in the database.
     *
     * @param concept       the concept to score
     * @param conceptScores the list of concept scores
     * @param label         the label of the PDF
     */
    private void createEdges(String concept, List<Pair<Integer, Double>> conceptScores, String label) {
        database.createEdges(concept, conceptScores, label);
    }

    /**
     * Creates a graph from a saved PDF.
     */
    public Process createGraph() {
        Pair<List<PDFText>, String> pdf = loadSavedPDF();
        if (pdf == null) {
            return null;
        }
        Process server = pythonManager.runPythonAIServer();
        database.save(pdf.getEl1(), pdf.getEl2());
        Map<Integer, List<Pair<Integer, Double>>> edges = getEdgeValues(pdf.getEl1(), pdf.getEl2(), 80.0);
        createEdges(edges, pdf.getEl2());
        return server;
    }

    /**
     * Creates a graph from a saved PDF with a specified similarity threshold.
     *
     * @param similarityThreshold the similarity threshold
     */
    public Process createGraph(double similarityThreshold) {
        Process server = pythonManager.runPythonAIServer();
        Pair<List<PDFText>, String> pdf = loadSavedPDF();
        database.save(pdf.getEl1(), pdf.getEl2());
        Map<Integer, List<Pair<Integer, Double>>> edges = getEdgeValues(pdf.getEl1(), pdf.getEl2(),
                similarityThreshold);
        createEdges(edges, pdf.getEl2());
        return server;
    }

    /**
     * Adds a concept to the graph.
     */
    public void addConcept() {
        Pair<List<PDFText>, String> pdf = loadSavedPDF();
        String concept = fileChooser.getUserInput("Concept", "Please enter the concept: ");
        List<Pair<Integer, Double>> scores = getConceptScores(concept, pdf.getEl1(), pdf.getEl2());
        createEdges(concept, scores, pdf.getEl2());
    }

    public void addConceptToAll() {
        String concept = fileChooser.getUserInput("Concept", "Please enter the concept: ");
        if (concept == null || concept.isEmpty()) {
            return;
        }
        Pair<List<List<PDFText>>, List<String>> pdfsWithLabels = getPdfsForConcept();
        for (int i = 0; i < pdfsWithLabels.getEl1().size(); i++) {
            List<Pair<Integer, Double>> scores = getConceptScores(concept, pdfsWithLabels.getEl1().get(i),
                    pdfsWithLabels.getEl2().get(i));
            createEdges(concept, scores, pdfsWithLabels.getEl2().get(i));
        }
    }

    private Pair<List<List<PDFText>>, List<String>> getPdfsForConcept() {
        String input = fileChooser.getUserInput("How many pdfs do you wish to be connected to the concept",
                "Provide a number");
        if (input == null || input.isEmpty()) {
            return null;
        }
        int digit = new InputParser().getDigit(input);
        if (digit == -1) {
            return null;
        }
        List<List<PDFText>> pdfs = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < digit; i++) {
            Pair<List<PDFText>, String> pdfWithLabel = loadSavedPDF();
            pdfs.add(pdfWithLabel.getEl1());
            labels.add(pdfWithLabel.getEl2());
        }

        return new Pair<List<List<PDFText>>, List<String>>(pdfs, labels);
    }

    public Process connectTheGraph() {
        Pair<List<PDFText>, String> pdfWithLabel = loadSavedPDF();
        List<PDFText> pdf = pdfWithLabel.getEl1();
        String label = pdfWithLabel.getEl2();

        Process server = pythonManager.runPythonAIServer();
        Map<Integer, List<Pair<Integer, Double>>> res = getEdgeValues(pdf, label, 80.0);

        database.createEdges(res, label);
        return server;
    }

    /**
     * Continues edge creation from a given PDF.
     *
     * @param pdf the list of PDF texts
     * @return a map of edge values
     */
    public Map<Integer, List<Pair<Integer, Double>>> continueWithEdgeCreation(List<PDFText> pdf) { // TODO
        return null;
    }

    /**
     * Creates edges from previously calculated scores.
     */
    public void createEdgesScoresCalculated() {
        Pair<File, String> savedScores = loadSavedScores();
        try {
            Map<Integer, List<Pair<Integer, Double>>> scores = new ObjectMapper().readValue(savedScores.getEl1(),
                    new TypeReference<Map<Integer, List<Pair<Integer, Double>>>>() {
                    });
            scores.entrySet().stream().forEach(entry -> {
                System.out.println("key: " + entry.getKey());
                System.out.println("val: " + entry.getValue());
            });
            createEdges(scores, savedScores.getEl2());
        } catch (IOException e) {
            throw new RuntimeException("Error reading scores", e);
        }
    }

    /**
     * Creates a chart based on the graph data.
     */
    public Process createChart() {
        Process server = pythonManager.runPythonPlotServer();
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
        return server;
    }

    /**
     * Displays a chart with the specified label.
     *
     * @param label the label of the chart
     */
    public Process showChart(String label) {
        Process server = pythonManager.runPythonPlotServer();
        plotManager.loadPlot(label);
        return server;
    }

    /**
     * Displays a chart chosen by the user.
     */
    public Process showChart() {
        Process server = pythonManager.runPythonPlotServer();
        File chosenChart = fileChooser
                .chooseTXT(Paths.get(configManager.getProperty("GraphBookProject", "SavedPlotData")).toFile());
        plotManager.loadPlot(chosenChart);
        return server;
    }

    /**
     * Exits the GUI.
     */
    public void exitGUI() {
        fileChooser.exit();
    }

    /**
     * Clears the database.
     */
    public void clearDatabase() {
        database.reset();
    }
}
