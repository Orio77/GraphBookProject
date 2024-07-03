package com.graphbook;

import com.graphbook.frontend.service.GraphBookGUIManager;

public class ProjectTest {
    private final static GraphBookGUIManager manager = new GraphBookGUIManager();

    public static void main(String[] args) {
        manager.createChart();
    }

}
