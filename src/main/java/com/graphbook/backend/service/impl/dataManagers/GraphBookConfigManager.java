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

public class GraphBookConfigManager {
    private ObjectNode rootNode;
    private final Logger logger = LogManager.getLogger();
    private ObjectMapper mapper;
    private Path configFilePath;

    public GraphBookConfigManager(Path configFilePath) {
        mapper = new ObjectMapper();
        this.configFilePath = configFilePath;
        initialize();
        addInitialProperties();
        saveToConfigFile();
    }

    public GraphBookConfigManager() {
        mapper = new ObjectMapper();
        initialize();
        configFilePath = readConfigFilePath();
    }

    private Path readConfigFilePath() {
        String pathAsString = getProperty("GraphBookProject", "ConfigFilePath");
        if (pathAsString == null) {
            logger.warn("Config path is not set yet, run the other constructor first.");
            throw new RuntimeException("Config path is not set yet, run the other constructor first.");
        }
        return Paths.get(pathAsString);
    }

    private void initialize() {
        try {
            File configFile = SafeGraphBookInitializer.getConfigFilePath().toFile();
            if (configFile.length() < 2) {
                rootNode = mapper.createObjectNode();
            } else {
                rootNode = (ObjectNode) (mapper.readTree(configFile));
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

    private void addInitialProperties() {
        addProperty("URIs", "SimilarityBatchUri", "http://192.168.1.46:5000/similarity_batch");
        addProperty("URIs", "PlotURI", "http://192.168.1.46:5001/generatePlot");
        addProperty("URIs", "ConceptURI", "http://192.168.1.46:5000/concept");
        addProperty("Python", "PythonEnvPath", "C:/Users/macie/anaconda3/envs/GraphBookProjectPyEnv/python.exe");
        addProperty("Python", "PythonExecutable", "python.exe");
        addProperty("Python", "PythonServerPath", "C:/Users/macie/Desktop/GBP/graph-book-core/py_llm_server/server");
        addProperty("Python", "PythonServerFileName", "python_server.py");
    }

    public String getProperty(String parentKey, String childKey) {
        if (rootNode.has(parentKey) && rootNode.get(parentKey).has(childKey)) {
            return rootNode.get(parentKey).get(childKey).asText();
        } else {
            logger.warn("Property {}.{} not found", parentKey, childKey);
            return null;
        }
    }

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

    public String getProperties() {
        StringBuilder sb = new StringBuilder();
        rootNode.fieldNames().forEachRemaining(name -> {
            sb.append(name).append(": ").append(rootNode.get(name)).append("\n");
        });
        return sb.toString();
    }

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

    public Path getSavedPdfsPath() {
        initialize();
        String res = getProperty("GraphBookProject", "SavedPDFs");
        if (res == null) {
            throw new RuntimeException("There is no saved pdfs path property just yet");
        }
        else return Paths.get(res);
    }

    public Path getResultsPath() {
        initialize();
        String res = getProperty("GraphBookProject", "Scores");
        if (res == null) {
            throw new RuntimeException("There is no scores path property just yet");
        }
        else return Paths.get(res);
    }
}
