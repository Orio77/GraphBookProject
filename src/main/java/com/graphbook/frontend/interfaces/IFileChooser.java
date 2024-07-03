package com.graphbook.frontend.interfaces;

import java.io.File;
import java.util.List;

public interface IFileChooser {
    
    File choosePDF();
    File choosePDF(File initialDirectory);
    File chooseDir();
    File chooseDir(File initialDirectory);
    File chooseTXT();
    File chooseTXT(File initialDirectory);
    File chooseJSON();
    File chooseJSON(File initialDirectory);
    List<String> chooseConcepts(List<String> allConcepts);
    String getUserInput(String title, String information);
    void exit();
}
