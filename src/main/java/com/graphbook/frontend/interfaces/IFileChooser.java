package com.graphbook.frontend.interfaces;

import java.io.File;

public interface IFileChooser {
    
    File choosePDF();
    File choosePDF(File initialDirectory);
    File chooseDir();
    File chooseDir(File initialDirectory);
    File chooseTXT();
    File chooseTXT(File initialDirectory);
    File chooseJSON();
    File chooseJSON(File initialDirectory);
    String getUserInput(String title, String label);
    void exit();
}
