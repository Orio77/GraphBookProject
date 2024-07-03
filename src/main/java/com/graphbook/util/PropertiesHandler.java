package com.graphbook.util;

import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.graphbook.backend.service.impl.dataManagers.GraphBookConfigManager;

public class PropertiesHandler {
    private static final Logger logger = LogManager.getLogger(PropertiesHandler.class);
    
    public static URI getURI(String uriLabel) {
        URI myURI = null;
        try {
            myURI = URI.create(new GraphBookConfigManager().getProperty("URIs", uriLabel));
        } 
        catch (NullPointerException e) {
            logger.error(e);
            throw new RuntimeException("URI creation failed. Provided String was null", e);
        }
        catch (IllegalArgumentException e) {
            logger.error(e);
            throw new RuntimeException("URI creation failed. Provided String probably violated RFC 2396", e);
        }
        if (myURI != null) {
            return myURI;
        }
        else throw new RuntimeException("Created URI is null");
    }
}
