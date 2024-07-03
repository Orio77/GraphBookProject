package com.graphbook.util;

import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.graphbook.backend.service.impl.dataManagers.GraphBookConfigManager;

/**
 * The {@code PropertiesHandler} class is a utility class in the {@code com.graphbook.util} package,
 * designed to manage and retrieve URI properties from a configuration source. It leverages the
 * {@code GraphBookConfigManager} to access the configuration data.
 * 
 * <p>This class provides a static method to fetch URIs by their labels from the configuration. It ensures
 * that the URI strings are valid according to RFC 2396 and encapsulates the error handling by logging
 * exceptions and throwing runtime exceptions when necessary.</p>
 * 
 * <p>Usage of this class simplifies the process of URI retrieval from a centralized configuration, abstracting
 * the complexity of configuration management and error handling.</p>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *     <li>Retrieval of URI properties by label.</li>
 *     <li>Validation of URI strings against RFC 2396.</li>
 *     <li>Centralized error handling and logging.</li>
 * </ul>
 * 
 * <h2>Example Usage:</h2>
 * <pre>
 * try {
 *     URI myServiceURI = PropertiesHandler.getURI("myServiceEndpoint");
 *     // Use the URI for creating connections, etc.
 * } catch (RuntimeException e) {
 *     // Handle exception - possibly configuration issue or invalid URI
 * }
 * </pre>
 * 
 * @see URI
 * @see GraphBookConfigManager
 */
public class PropertiesHandler {
    /**
     * Logger for logging errors and information. It is used to log exceptions
     * and important events throughout the class's operations.
     */
    private static final Logger logger = LogManager.getLogger(PropertiesHandler.class);
    
    /**
     * Retrieves a URI based on a given label from the configuration. This method
     * ensures that the URI is valid and throws a runtime exception if the URI
     * cannot be created either because the label does not exist in the configuration
     * or the URI string is invalid.
     * 
     * @param uriLabel The label of the URI to retrieve from the configuration.
     * @return The URI associated with the given label.
     * @throws RuntimeException if the URI label is null, no URI is found for the given label,
     *                          or the URI string is invalid according to RFC 2396.
     */
    public static URI getURI(String uriLabel) {
        if (uriLabel == null) {
            throw new RuntimeException("URI creation failed. Provided String was null");
        }
        URI myURI = null;
        try {
            String receivedProperty = new GraphBookConfigManager().getProperty("URIs", uriLabel);
            if (receivedProperty == null) {
                throw new RuntimeException("No uri with such label in the config");
            }
            myURI = URI.create(receivedProperty);
        } 
        catch (IllegalArgumentException e) {
            logger.error(e);
            throw new RuntimeException("URI creation failed. Provided String probably violated RFC 2396", e);
        }
        return myURI;
    }
}
