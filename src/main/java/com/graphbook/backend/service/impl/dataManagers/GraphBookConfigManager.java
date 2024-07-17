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

/**
 * Manages the configuration settings for the GraphBook application.
 * This class provides methods to read, add, and save properties from a
 * configuration file.
 * It utilizes JSON format for storing configuration data.
 */
public class GraphBookConfigManager {
    private ObjectNode rootNode;
    private final Logger logger = LogManager.getLogger();
    private ObjectMapper mapper;
    private Path configFilePath;

    /**
     * Constructs a {@code GraphBookConfigManager} with a default configuration file
     * path read from properties.
     */
    public GraphBookConfigManager() {
        mapper = new ObjectMapper();
        configFilePath = Paths.get("./src/main/resources/config.json");
        initialize();
    }

    /**
     * Initializes the configuration by reading from the specified configuration
     * file.
     * If the file is empty or not found, a new configuration node is created.
     *
     * @throws RuntimeException if there is an error during initialization.
     */
    private void initialize() {
        try {
            File configFile = configFilePath.toFile();
            if (configFile.length() < 2) {
                rootNode = mapper.createObjectNode();
            } else {
                rootNode = (ObjectNode) mapper.readTree(configFile);
            }
        } catch (FileNotFoundException e) {
            logger.error(e);
            throw new RuntimeException("Error occurred while initializing JSON config from " + configFilePath
                    + ". Check error log for details.", e);
        } catch (JsonProcessingException e) {
            logger.error(e);
            throw new RuntimeException("Error occurred while initializing JSON config from " + configFilePath
                    + ". Check error log for details.", e);
        } catch (IOException e) {
            logger.error("IO Error", e);
            throw new RuntimeException("Error occurred while initializing JSON config from " + configFilePath
                    + ". Check error log for details.", e);
        }
    }

    /**
     * Retrieves the value of a specified property from the configuration.
     *
     * @param parentKey The parent key of the property.
     * @param childKey  The child key of the property.
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
     * Saves the current configuration to the configuration file.
     *
     * @throws RuntimeException if there is an error during saving.
     */
    public void saveToConfigFile() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(configFilePath.toFile(), rootNode);
        } catch (DatabindException e) {
            logger.error("IO Error", e);
            throw new RuntimeException(
                    "Error occurred while saving JSON config to " + configFilePath + ". Check error log for details.",
                    e);
        } catch (StreamWriteException e) {
            logger.error("IO Error", e);
            throw new RuntimeException(
                    "Error occurred while saving JSON config to " + configFilePath + ". Check error log for details.",
                    e);
        } catch (IOException e) {
            logger.error("IO Error", e);
            throw new RuntimeException(
                    "Error occurred while saving JSON config to " + configFilePath + ". Check error log for details.",
                    e);
        }
    }

    /**
     * Retrieves the path where PDFs are saved from the configuration.
     *
     * @return The path where PDFs are saved.
     * @throws RuntimeException if the saved PDFs path property is not set.
     */
    public Path getSavedPdfsPath() {
        String res = getProperty("GraphBookProject", "SavedPDFs");
        if (res == null) {
            throw new RuntimeException("There is no saved pdfs path property just yet");
        } else
            return Paths.get(res);
    }

    /**
     * Retrieves the path where results are saved from the configuration.
     *
     * @return The path where results are saved.
     * @throws RuntimeException if the results path property is not set.
     */
    public Path getResultsPath() {
        String res = getProperty("GraphBookProject", "Scores");
        if (res == null) {
            throw new RuntimeException("There is no scores path property just yet");
        } else
            return Paths.get(res);
    }
}
