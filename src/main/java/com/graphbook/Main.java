package com.graphbook;

import java.time.LocalDateTime;

import com.graphbook.frontend.GraphBookGUIManager;
import com.graphbook.util.TimeMeasurer;

public class Main {
    private static final GraphBookGUIManager manager = new GraphBookGUIManager();

    public static void main(String[] args) {
        LocalDateTime start = TimeMeasurer.startMeasuring();
        // manager.createEdgesScoresCalculated();
        // manager.createGraph();
        manager.initializeProject();
        LocalDateTime end = TimeMeasurer.endMeasuring();
        System.out.println(TimeMeasurer.getTimePassedString(start, end));
        manager.exitGUI();
    }
}
