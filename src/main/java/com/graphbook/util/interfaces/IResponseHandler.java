package com.graphbook.util.interfaces;

import java.util.HashMap;
import java.util.List;

public interface IResponseHandler {
    public HashMap<Integer, List<List<Double>>> handle(Object o);
}
