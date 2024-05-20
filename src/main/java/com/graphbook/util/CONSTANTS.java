package com.graphbook.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;

public class CONSTANTS {
    public static final Path SERIALIZED_PATH = Paths.get("C:/Users/macie/iCloudDrive/MyProjects/graph-book-core/src/main/java/com/graphbook/files/serialized/");
    public static final Path ERROR_LOG_PATH = Paths.get("C:/Users/macie/iCloudDrive/MyProjects/graph-book-core/src/main/java/com/graphbook/server/logs/errorLog");
    public static final URI MY_URI = URI.create("http://localhost:5000/similarity");
    public static final Path SAVED_OBJECTS_PATH = Paths.get("C:/Users/macie/iCloudDrive/MyProjects/graph-book-core/src/main/java/com/graphbook/files/serialized/object");
    public static final Path CONFIG_PATH = Paths.get("src/main/java/com/graphbook/config/paths.properties");
    public static final Path PYTHON_SERVER_PATH = Paths.get("py_llm_server/server/");
    public static Path STORED_PDFS_PATH = null; // TODO Set this var with Improved Saver
    public static final Path PROJECT_PATH = loadProjectPath();
    public static final Path SAVED_PDFS_PATH = PROJECT_PATH.resolve("saved");

    private static Path loadProjectPath() {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream("src/main/java/com/graphbook/config/paths.properties")) {
            properties.load(in);
            return Paths.get(properties.getProperty("PROJECT_PATH"));
        } catch (IOException e) {
            LogManager.getLogger(CONSTANTS.class).error("IOException occured while setting the config project path.", e.getMessage(), e);
            throw new RuntimeException("IOException occured while loading project path from properties file. Check the logged error for details.");
        }
    }
}
