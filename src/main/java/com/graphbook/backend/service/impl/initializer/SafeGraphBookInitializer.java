package com.graphbook.backend.service.impl.initializer;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.graphbook.backend.service.IGraphBookInitializer;
import com.graphbook.backend.service.impl.dataManagers.GraphBookConfigManager;

/**
 * Implementation of {@link IGraphBookInitializer} that initializes the GraphBook project
 * in a safe manner, ensuring necessary directories are created and configurations are saved.
 */
public class SafeGraphBookInitializer implements IGraphBookInitializer {
    private GraphBookConfigManager configManager;
    private Path configPath;
    private final Logger logger = LogManager.getLogger(getClass());

    /**
     * Constructs a new SafeGraphBookInitializer with the specified initial directory.
     * 
     * @param initialDir the initial directory where the project will be initialized.
     */
    public SafeGraphBookInitializer(File initialDir) {
        setProjectPath(initialDir);
        createNecessaryDirectories();
        configManager.saveToConfigFile();
    }

    /**
     * Sets the project path and initializes the configuration manager.
     * 
     * @param chosenDir the directory chosen by the user for the project.
     */
    @Override
    public void setProjectPath(File chosenDir) {
        this.configPath = getConfigFilePath("config.json");
        this.configManager = new GraphBookConfigManager(configPath);

        Path pathToChosenDir = chosenDir.toPath();
        Path savedPath = pathToChosenDir.resolve("saved\\pdfs");
        Path scoresPath = pathToChosenDir.resolve("scores");
        
        configManager.addProperty("GraphBookProject", "ProjectPath", pathToChosenDir.toString());
        configManager.addProperty("GraphBookProject", "ConfigFilePath", configPath.toString());
        configManager.addProperty("GraphBookProject", "SavedPDFs", savedPath.toString());
        configManager.addProperty("GraphBookProject", "Scores", scoresPath.toString());
    }

    /**
     * Retrieves the configuration file path for the specified file name.
     * 
     * @param fileName the name of the configuration file.
     * @return the path to the configuration file.
     */
    private Path getConfigFilePath(String fileName) {
        try {
            Path classFileDir = Paths.get(Objects.requireNonNull(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())).getParent();
            Path projectRoot = classFileDir;
            while (projectRoot != null && !projectRoot.endsWith(Paths.get("graph-book-core"))) {
                projectRoot = projectRoot.getParent();
            }
            if (projectRoot == null) {
                throw new RuntimeException("Failed to locate the Project Root. File must be outside of the project.");
            }
            return projectRoot.resolve(Paths.get("src/main/resources")).resolve(fileName);
        } catch (URISyntaxException e) {
            logger.error("Failed to locate the project root.", e);
            throw new RuntimeException("Failed to locate the project root. Check error log for details.", e);
        }
    }

    /**
     * Creates the necessary directories for the project if they do not exist.
     */
    @Override
    public void createNecessaryDirectories() {
        Path projectDirPath = Paths.get(new GraphBookConfigManager().getProperty("GraphBookProject", "ProjectPath"));
        Path savedPdfsDirPath = Paths.get(new GraphBookConfigManager().getProperty("GraphBookProject", "SavedPDFs"));
        File savedPdfsDirectory = projectDirPath.resolve(savedPdfsDirPath).toFile();
        if (!savedPdfsDirectory.exists()) {
            if (!savedPdfsDirectory.mkdirs()) {
                throw new RuntimeException("Failed to create necessary directories (savedPdfsDirectory)");
            }
        }
    }

    /**
     * Retrieves the configuration file path.
     * 
     * @return the path to the configuration file.
     */
    public static Path getConfigFilePath() {
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource("");
            Path classFileDir = Paths.get(Objects.requireNonNull(url.toURI())).getParent();
            Path projectRoot = classFileDir;
            while (projectRoot != null && !projectRoot.endsWith(Paths.get("graph-book-core"))) {
                projectRoot = projectRoot.getParent();
            }
            if (projectRoot == null) {
                throw new RuntimeException("Failed to locate the Project Root. File must be outside of the project.");
            }
            return projectRoot.resolve(Paths.get("src/main/resources")).resolve("config.json");
        } catch (URISyntaxException e) {
            LogManager.getLogger("SafeGraphBookInitializer").error("Failed to locate the project root. Check error log for details.");
            throw new RuntimeException("Failed to locate the project root. Check error log for details.", e);
        }
    }
}
