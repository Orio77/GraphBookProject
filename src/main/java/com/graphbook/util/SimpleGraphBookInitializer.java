package com.graphbook.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;

import com.graphbook.GraphBook;
import com.graphbook.util.interfaces.IGraphBookInitializer;

public class SimpleGraphBookInitializer implements IGraphBookInitializer {
    

    public void setProjectPath(File chosenDir) {
        // TODO null handling
        Properties properties = new Properties();
        properties.setProperty("PROJECT_PATH", chosenDir.toPath().toString()); // TODO What if the paths.properties already exists and is empty?
        File configDir = chosenDir.toPath().resolve("config").toFile();
        if (!configDir.exists()) {
            configDir.mkdir();
        }
        try (FileOutputStream out = new FileOutputStream(configDir.toPath().resolve("paths.properties").toFile())) {
            properties.store(out, null);
        } catch (IOException e) {
            LogManager.getLogger(GraphBook.class).error("IOException occured while writing file into the properties file", e.getMessage(), e);
            throw new RuntimeException("IOException occured while storing Project Path property, check logged error for details");
        }
    }

    public void createNecessaryDirectories() {
        File savedPdfsDirectory = new File(CONSTANTS.SAVED_PDFS_PATH.toString());
        if (!savedPdfsDirectory.exists()) {
            savedPdfsDirectory.mkdir();
        }
    }

    
}
