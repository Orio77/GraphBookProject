package com.graphbook.backend.service.impl.initializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.graphbook.backend.service.IGraphBookInitializer;
import com.graphbook.util.CONSTANTS;

public class SimpleGraphBookInitializer implements IGraphBookInitializer {
    public static final Path DOTENV_PATH = Paths.get("py_llm_server/environment/.env");
    private final Logger logger = LogManager.getLogger(this.getClass());
    
    public void setProjectPath(File chosenDir) {
        if (chosenDir == null) {
            IllegalArgumentException e = new IllegalArgumentException("Directory cannot be null");
            logger.error("Directory cannot be null", e);
            throw e;
        }
        Properties properties = new Properties();

        File configDir = chosenDir.toPath().resolve("config").toFile();
        if (!configDir.exists() && !configDir.mkdir()) {
            IllegalStateException e = new IllegalStateException("Failed to create the config directory");
            logger.error("Failed to create the config directory", e);
            throw e;
        }

        File pathsPropertiesFile = configDir.toPath().resolve("paths.properties").toFile();


        if (!pathsPropertiesFile.exists()) {
            try {
                pathsPropertiesFile.createNewFile();
            } catch (IOException e) {
                logger.error("Failed to create the paths properties file", e);
                throw new RuntimeException("Failed to create the paths properties file, check error log for details");
            }
        }

        try (FileInputStream in = new FileInputStream(pathsPropertiesFile)) {
            properties.load(in);
        } catch (IOException e) {
            logger.error("Failed to load existing properties from paths.properties file", e);
            throw new RuntimeException("Failed to load existing properties from paths.properties file", e);
        }

        properties.setProperty("PROJECT_PATH", chosenDir.toPath().toString());
        
        try (FileOutputStream out = new FileOutputStream(pathsPropertiesFile)) {
            properties.store(out, null);
        } catch (IOException e) {
            logger.error("IOException occured while writing file into the properties file", e);
            throw new RuntimeException("IOException occured while storing Project Path property, check logged error for details");
        }


        File dotenvFile = DOTENV_PATH.toFile();

        if (!dotenvFile.exists()) {
            try {
                dotenvFile.createNewFile();
            } catch (IOException e) {
                logger.error("Failed to create the .env file", e);
                throw new RuntimeException("Failed to create the .env file, check error log for details");
            }
        }

        if (!dotenvFile.canRead()) {
            IOException e = new IOException("Cannot read the .env file");
            logger.error("Cannot read the .env file", e);
            throw new RuntimeException("Cannot read the .env file");
        }
        try (FileInputStream in = new FileInputStream(dotenvFile)) {
            properties.load(in);
        } catch (IOException e) {
            logger.error("Failed to load properties from .env file", e);
            throw new RuntimeException("Failed to load properties from .env file, check logged error for details");
        }

        properties.setProperty("PROJECT_PATH", chosenDir.toPath().toString());
        
        try (FileOutputStream out = new FileOutputStream(dotenvFile)) {
            properties.store(out, null);
        } catch (IOException e) {
            LogManager.getLogger(SimpleGraphBookInitializer.class).error("Failed to store properties to .env file", e.getMessage(), e);
            throw new RuntimeException("Failed to store properties to .env file, check error log for details");
        }
    }

    public void createNecessaryDirectories() {
        File savedPdfsDirectory = new File(CONSTANTS.SAVED_PDFS_PATH.toString());
        if (!savedPdfsDirectory.exists()) {
            savedPdfsDirectory.mkdir();
        }
    }

    // TODO add a method to change the project path:
    // - if the user changes the .properties path, check whether the paths in .env are different
    // - update the .env paths
    
}
