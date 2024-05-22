package com.graphbook.server;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.graphbook.util.interfaces.IResponseHandler;

public class SimpleResponseHandler implements IResponseHandler{
    private final Logger logger = LogManager.getLogger(this.getClass());

    @SuppressWarnings("unchecked")
    public HashMap<Integer, List<List<Double>>> handle(Object o) {
        if (o instanceof HashMap) {
            try {
                return (HashMap<Integer, List<List<Double>>>) o;
            } catch (ClassCastException e) {
                logger.error("ClassCastException was thrown while casting output. the class of the Object is: " + o.getClass().getName(), e.getMessage(), e);
                throw new RuntimeException("ClassCastException occured in " + this.getClass().getName() + ", check the error log for details");
            }
        }
        else return null;
    }
}
