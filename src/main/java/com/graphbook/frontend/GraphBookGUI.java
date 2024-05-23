package com.graphbook.frontend;

import java.util.HashMap;
import java.util.List;

import com.graphbook.elements.PDFText;

import javafx.application.Application;
import javafx.stage.Stage;

public class GraphBookGUI extends Application {
    private  GraphBookGUIManager manager;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("GraphBook");

        manager = new GraphBookGUIManager(primaryStage);

        primaryStage.show();

        List<PDFText> pdf = manager.loadSavedPDF();
        HashMap<Integer, List<List<Double>>> res = manager.getEdgeValues(pdf);
        res.entrySet().stream().forEach(entry -> {
            System.out.println("Key: " + entry.getKey());
            System.out.println("Val: " + entry.getValue());
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}