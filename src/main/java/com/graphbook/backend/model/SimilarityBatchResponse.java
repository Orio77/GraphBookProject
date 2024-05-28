package com.graphbook.backend.model;

import java.util.HashMap;
import java.util.List;

public class SimilarityBatchResponse {
    private HashMap<Integer, List<Pair<Integer, Double>>> map;

    public HashMap<Integer, List<Pair<Integer, Double>>> getMap() {
        return map;
    }
}
