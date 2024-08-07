package com.graphbook.backend.service.impl.plotTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphbook.backend.model.Pair;
import com.graphbook.backend.service.IDataManager;
import com.graphbook.backend.service.impl.dataManagers.GraphBookConfigManager;
import com.graphbook.backend.service.impl.dataManagers.JDataManager;
import com.graphbook.util.PropertiesHandler;

/**
 * Manages plot-related operations, including sending plot data to a server,
 * saving plot data, and loading saved plot data.
 */
public class PlotManager {
    private final ObjectMapper MAPPER;
    private final Logger logger;

    /**
     * Constructs a new PlotManager with initialized ObjectMapper and Logger.
     */
    public PlotManager() {
        MAPPER = new ObjectMapper();
        logger = LogManager.getLogger(getClass());
    }

    /**
     * Displays a graph using the provided scores.
     * 
     * @param scores the scores to display on the graph.
     */
    public void showGraph(Map<String, List<Pair<String, Double>>> scores) {
        sendPlotData(scores);
    }

    /**
     * Sends the plot data to a remote server and returns the server's response.
     * 
     * @param scores the scores to send.
     * @return the server's response as a String.
     */
    private String sendPlotData(Map<String, List<Pair<String, Double>>> scores) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(30000) // connection timeout
                .build();
        try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(requestConfig).build()) {
            HttpPost post = new HttpPost(PropertiesHandler.getURI("PlotURI"));

            // Convert the scores to JSON
            String json = MAPPER.writeValueAsString(scores);

            // Set the entity and headers
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            post.setEntity(entity);
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");

            HttpResponse response = client.execute(post);

            if (response == null) {
                throw new RuntimeException("Response is null. Look for logged error for the cause");
            }

            // Check if the response' status code is OK
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                logger.error("Error Status Code received. Status Code: {}", statusCode);
                throw new RuntimeException("Error Status Code received. Status Code: " + statusCode);
            }

            HttpEntity receivedEntity = response.getEntity();
            String jsonResponse = EntityUtils.toString(receivedEntity);
            EntityUtils.consume(receivedEntity);

            return jsonResponse;
        }
        // Catch the errors and log the exceptions
        catch (JsonProcessingException e) {
            logger.error("Error formatting JSON.", e.getMessage(), e);
            return null;
        } catch (ClientProtocolException e) {
            logger.error("Client Protocol Exception occurred. Exception: {}", e.getMessage(), e);
            return null;
        } catch (IOException e) {
            logger.error("IO exception occurred. Exception: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Saves the plot data with a specified label.
     * 
     * @param scores the scores to save.
     * @param label  the label to associate with the saved plot.
     */
    public void savePlotData(Map<String, List<Pair<String, Double>>> scores, String label) {
        IDataManager dManager = new JDataManager();
        dManager.savePlot(scores, label);
    }

    /**
     * Loads and displays a saved plot using the specified label.
     * 
     * @param label the label of the saved plot to load.
     */
    @SuppressWarnings("unchecked")
    public void loadPlot(String label) {
        IDataManager dManager = new JDataManager();

        File file = Paths.get(new GraphBookConfigManager().getProperty("GraphBookProject", "SavedPlots")).toFile();
        if (!file.exists()) {
            throw new RuntimeException("No saved plot with such label found. Provided label was: " + label);
        }

        Object savedPlot = dManager.readSavedPlot(file);
        Map<String, List<Pair<String, Double>>> castedPlot = null;
        if (savedPlot instanceof Map) {
            castedPlot = (Map<String, List<Pair<String, Double>>>) savedPlot;
        } else {
            throw new RuntimeException("Saved Object was not a map, it was: " + savedPlot.getClass());
        }

        showGraph(castedPlot);
    }

    /**
     * Loads and displays a saved plot from the specified file.
     * 
     * @param file the file containing the saved plot.
     */
    @SuppressWarnings("unchecked")
    public void loadPlot(File file) {
        if (file == null) {
            return;
        }
        IDataManager dManager = new JDataManager();
        Object savedPlot = dManager.readSavedPlot(file);
        Map<String, List<Pair<String, Double>>> castedPlot = null;
        if (savedPlot instanceof Map) {
            castedPlot = (Map<String, List<Pair<String, Double>>>) savedPlot;
        } else {
            throw new RuntimeException("Saved Object was not a map, it was: " + savedPlot.getClass());
        }

        showGraph(castedPlot);
    }
}
