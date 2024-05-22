package com.graphbook.util.interfaces;

import java.io.File;

public interface IGraphBookInitializer {
    
    void setProjectPath(File chosenDir);
    void createNecessaryDirectories();
}
