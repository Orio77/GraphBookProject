package com.graphbook.backend.service.impl.initializer;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.graphbook.backend.service.IGraphBookInitializer;
import com.graphbook.backend.service.impl.dataManagers.GraphBookConfigManager;

public class SafeGraphBookInitializer implements IGraphBookInitializer {
    private GraphBookConfigManager configManager;
    private Path configPath;
    private final Logger logger = LogManager.getLogger(getClass());

    public SafeGraphBookInitializer(File initialDir) {
        setProjectPath(initialDir);
        createNecessaryDirectories();
        configManager.saveToConfigFile();
    }

    @Override
    public void setProjectPath(File chosenDir) {
        this.configPath = getConfigFilePath("config.json");
        this.configManager = new GraphBookConfigManager(configPath);

        Path pathToChosenDir = chosenDir.toPath();
        Path savedPath = pathToChosenDir.resolve("saved");
        Path scoresPath = pathToChosenDir.resolve("scores");
        
        configManager.addProperty("GraphBookProject", "ProjectPath", pathToChosenDir.toString());
        configManager.addProperty("GraphBookProject", "ConfigFilePath", configPath.toString());
        configManager.addProperty("GraphBookProject", "Saved", savedPath.toString());
        configManager.addProperty("GraphBookProject", "Scores", scoresPath.toString());
    }

    public Path getConfigFilePath(String fileName) {
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

    @Override
    public void createNecessaryDirectories() {
        Path projectDirPath = Paths.get(GraphBookConfigManager.getProperty("GraphBookProject", "ProjectPath"));
        Path savedPdfsDirPath = Paths.get(GraphBookConfigManager.getProperty("GraphBookProject", "Saved"));
        File savedPdfsDirectory = projectDirPath.resolve(savedPdfsDirPath).toFile();
        if (!savedPdfsDirectory.exists()) {
            if (!savedPdfsDirectory.mkdir()) {
                throw new RuntimeException("Failed to create necessary directories (savedPdfsDirectory)");
            }
        }
    }
}
