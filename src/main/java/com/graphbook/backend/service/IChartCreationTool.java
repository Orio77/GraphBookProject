package com.graphbook.backend.service;

import java.util.List;
import java.util.Map;

import org.jfree.chart3d.Chart3D;

import com.graphbook.backend.model.Pair;

public interface IChartCreationTool {
    
    Object createChart(Map<String, List<Pair<String, Double>>> scores);
    void showChart(Chart3D chart);
    void saveChart();
}
