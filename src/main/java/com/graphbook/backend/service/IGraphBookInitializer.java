package com.graphbook.backend.service;

import java.io.File;

public interface IGraphBookInitializer {
    
    void setProjectPath(File chosenDir);
    void createNecessaryDirectories();
}
