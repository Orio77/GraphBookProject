package com.graphbook.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;

import com.graphbook.backend.service.impl.initializer.SimpleGraphBookInitializer;

public class CONSTANTS { // TODO create a class environment handler instead and put all the paths there
    public static final Path PROJECT_PATH = loadProjectPath();
    public static final URI MY_URI = URI.create("http://localhost:5000/similarity");
    public static final URI MY_URI_BATCH = URI.create("http://192.168.1.46:5000/similarity_batch");
    public static final Path PYTHON_ENV_PATH = Paths.get("C:/Users/macie/anaconda3/envs/GraphBookProjectPyEnv/python.exe");
    public static final String PYTHON_EXECUTABLE = "python.exe";
    public static final Path PYTHON_SERVER_DIR = Paths.get("C:/Users/macie/Desktop/GBP/graph-book-core/py_llm_server/server");
    public static final String PYTHON_SERVER_FILENAME = "python_server.py";
    public static final Path SAVED_PDFS_PATH = PROJECT_PATH.resolve("saved");

    private static Path loadProjectPath() {
        Properties property = new Properties();
        
        try (FileInputStream in = new FileInputStream(SimpleGraphBookInitializer.DOTENV_PATH.toFile())) {
            property.load(in);
            String projectPath = property.getProperty("PROJECT_PATH");
            return Paths.get(projectPath);
        } catch (IOException e) {
            LogManager.getLogger(CONSTANTS.class).error("IOException occured while setting the config project path.", e.getMessage(), e);
            throw new RuntimeException("IOException occured while loading project path from properties file. Check the logged error for details.");
        }
    }
}
