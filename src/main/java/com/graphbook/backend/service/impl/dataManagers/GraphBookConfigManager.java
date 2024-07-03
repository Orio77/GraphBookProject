package com.graphbook.backend.service.impl.dataManagers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphbook.backend.service.impl.initializer.SafeGraphBookInitializer;

/**
 * Manages the configuration settings for the GraphBook application.
 * This class provides methods to read, add, and save properties from a configuration file.
 * It utilizes JSON format for storing configuration data.
 */
public class GraphBookConfigManager {
    private ObjectNode rootNode;
    private final Logger logger = LogManager.getLogger();
    private ObjectMapper mapper;
    private Path configFilePath;

    /**
     * Constructs a {@code GraphBookConfigManager} with a specified configuration file path.
     * This constructor initializes the configuration and adds initial properties before saving to the file.
     *
     * @param configFilePath The path to the configuration file.
     */
    public GraphBookConfigManager(Path configFilePath) {
        mapper = new ObjectMapper();
        this.configFilePath = configFilePath;
        initialize();
        addInitialProperties();
        saveToConfigFile();
    }

    /**
     * Constructs a {@code GraphBookConfigManager} with a default configuration file path read from properties.
     */
    public GraphBookConfigManager() {
        mapper = new ObjectMapper();
        initialize();
        configFilePath = readConfigFilePath();
    }

    /**
     * Reads the configuration file path from properties.
     *
     * @return The path to the configuration file.
     * @throws RuntimeException if the configuration path is not set.
     */
    private Path readConfigFilePath() {
        String pathAsString = getProperty("GraphBookProject", "ConfigFilePath");
        if (pathAsString == null) {
            logger.warn("Config path is not set yet, run the other constructor first.");
            throw new RuntimeException("Config path is not set yet, run the other constructor first.");
        }
        return Paths.get(pathAsString);
    }

    /**
     * Initializes the configuration by reading from the specified configuration file.
     * If the file is empty or not found, a new configuration node is created.
     *
     * @throws RuntimeException if there is an error during initialization.
     */
    private void initialize() {
        try {
            File configFile = SafeGraphBookInitializer.getConfigFilePath().toFile();
            if (configFile.length() < 2) {
                rootNode = mapper.createObjectNode();
            } else {
                rootNode = (ObjectNode) mapper.readTree(configFile);
            }
        } catch (FileNotFoundException e) {
            logger.error(e);
            throw new RuntimeException("Error occurred while initializing JSON config from " + configFilePath + ". Check error log for details.", e);
        } catch (JsonProcessingException e) {
            logger.error(e);
            throw new RuntimeException("Error occurred while initializing JSON config from " + configFilePath + ". Check error log for details.", e);
        } catch (IOException e) {
            logger.error("IO Error", e);
            throw new RuntimeException("Error occurred while initializing JSON config from " + configFilePath + ". Check error log for details.", e);
        }
    }

    /**
     * Adds initial properties to the configuration.
     */
    private void addInitialProperties() {
        addProperty("URIs", "SimilarityBatchUri", "http://192.168.1.46:5000/similarity_batch");
        addProperty("URIs", "PlotURI", "http://192.168.1.46:5001/generatePlot");
        addProperty("URIs", "ConceptURI", "http://192.168.1.46:5000/concept");
        addProperty("Python", "PythonEnvPath", "C:/Users/macie/anaconda3/envs/GraphBookProjectPyEnv/python.exe");
        addProperty("Python", "PythonExecutable", "python.exe");
        addProperty("Python", "PythonServerPath", "C:/Users/macie/Desktop/GBP/graph-book-core/py_llm_server/server");
        addProperty("Python", "PythonServerFileName", "python_server.py");
    }

    /**
     * Retrieves the value of a specified property from the configuration.
     *
     * @param parentKey The parent key of the property.
     * @param childKey The child key of the property.
     * @return The value of the specified property, or null if not found.
     */
    public String getProperty(String parentKey, String childKey) {
        if (rootNode.has(parentKey) && rootNode.get(parentKey).has(childKey)) {
            return rootNode.get(parentKey).get(childKey).asText();
        } else {
            logger.warn("Property {}.{} not found", parentKey, childKey);
            return null;
        }
    }

    /**
     * Adds a property to the configuration under the specified parent and child keys.
     *
     * @param parentKey The parent key under which the property is added.
     * @param childKey The child key of the property.
     * @param value The value of the property.
     */
    public void addProperty(String parentKey, String childKey, String value) {
        if (rootNode.has(parentKey)) {
            ((ObjectNode) rootNode.get(parentKey)).put(childKey, value);
        } else {
            logger.warn("Parent key {} not found", parentKey);
            ObjectNode parentNode = mapper.createObjectNode();
            parentNode.put(childKey, value);
            rootNode.set(parentKey, parentNode);
        }
    }

    /**
     * Retrieves all properties in the configuration as a formatted string.
     *
     * @return A string representation of all properties in the configuration.
     */
    public String getProperties() {
        StringBuilder sb = new StringBuilder();
        rootNode.fieldNames().forEachRemaining(name -> {
            sb.append(name).append(": ").append(rootNode.get(name)).append("\n");
        });
        return sb.toString();
    }

    /**
     * Saves the current configuration to the configuration file.
     *
     * @throws RuntimeException if there is an error during saving.
     */
    public void saveToConfigFile() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(configFilePath.toFile(), rootNode);
        } 
        catch (DatabindException e) {
            logger.error("IO Error", e);
            throw new RuntimeException("Error occurred while saving JSON config to " + configFilePath + ". Check error log for details.", e);
        }
        catch (StreamWriteException e) {
            logger.error("IO Error", e);
            throw new RuntimeException("Error occurred while saving JSON config to " + configFilePath + ". Check error log for details.", e);
        }
        catch (IOException e) {
            logger.error("IO Error", e);
            throw new RuntimeException("Error occurred while saving JSON config to " + configFilePath + ". Check error log for details.", e);
        }
    }

    /**
     * Retrieves the path where PDFs are saved from the configuration.
     *
     * @return The path where PDFs are saved.
     * @throws RuntimeException if the saved PDFs path property is not set.
     */
    public Path getSavedPdfsPath() {
        initialize();
        String res = getProperty("GraphBookProject", "SavedPDFs");
        if (res == null) {
            throw new RuntimeException("There is no saved pdfs path property just yet");
        }
        else return Paths.get(res);
    }

    /**
     * Retrieves the path where results are saved from the configuration.
     *
     * @return The path where results are saved.
     * @throws RuntimeException if the results path property is not set.
     */
    public Path getResultsPath() {
        initialize();
        String res = getProperty("GraphBookProject", "Scores");
        if (res == null) {
            throw new RuntimeException("There is no scores path property just yet");
        }
        else return Paths.get(res);
    }
}
