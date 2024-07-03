package com.graphbook.server.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphbook.backend.model.PDFText;
import com.graphbook.backend.model.Pair;
import com.graphbook.server.IAISimilarityClient;
import com.graphbook.util.PropertiesHandler;

/**
 * The {@code ApacheHTTP_SimilarityClient} class is responsible for communicating with a local AI service
 * through HTTP requests. It implements the {@code IAISimilarityClient} interface to provide functionality
 * for sending text data to the AI service and receiving similarity scores or concept scores in response.
 * This class utilizes Apache HTTP components for the network communication and Jackson for JSON processing.
 * 
 * <p>It offers two main functionalities:</p>
 * <ul>
 *     <li>Fetching batch similarity scores between provided texts and a label.</li>
 *     <li>Fetching concept scores for a given concept across provided texts.</li>
 * </ul>
 * 
 * <p>Errors during the HTTP communication or JSON processing are logged using a {@code Logger} instance,
 * and exceptions are thrown to indicate failure scenarios to the caller.</p>
 * 
 * <p>Instances of this class are constructed without parameters. The class relies on an internal
 * {@code ObjectMapper} for JSON serialization and deserialization, and a {@code Logger} for error logging.</p>
 * 
 * @see IAISimilarityClient
 */
public class ApacheHTTP_SimilarityClient implements IAISimilarityClient {
    // ObjectMapper is used to convert Java objects to JSON and vice versa
    private final ObjectMapper MAPPER;
    // Logger is used for logging errors
    private final Logger logger = LogManager.getLogger(ApacheHTTP_SimilarityClient.class);

    /**
     * Constructs a new instance of {@code ApacheHTTP_SimilarityClient}. Initializes the internal
     * {@code ObjectMapper} for JSON processing.
     */
    public ApacheHTTP_SimilarityClient() {
        this.MAPPER = new ObjectMapper();
    }

    /**
     * Sends a batch of PDF texts along with a label to the AI service and retrieves similarity scores.
     * The scores are returned as a map where the key is an integer representing the PDF text identifier,
     * and the value is a list of pairs, each containing an integer (representing another PDF text identifier)
     * and a double (the similarity score between the two texts).
     * 
     * @param pdf A list of {@code PDFText} objects representing the PDF texts to be compared.
     * @param label A string label against which the texts are compared for similarity.
     * @return A map of integer keys to lists of pairs, representing the similarity scores between texts.
     * @throws IllegalArgumentException If the provided PDF list or label is null or empty.
     * @throws RuntimeException If the response from the AI service is null or if JSON processing fails.
     */
    @Override
    public Map<Integer, List<Pair<Integer, Double>>> getSimilarityBatchResponse(List<PDFText> pdf, String label) {
        // Validate input parameters
        if (pdf == null || pdf.isEmpty()) {
            throw new IllegalArgumentException("PDF list is null or empty.");
        }
        if (label == null || label.isEmpty()) {
            throw new IllegalArgumentException("Label is null or empty.");
        }
        
        // Send PDF and receive response
        String response = sendPDF(pdf, label, "", "SimilarityBatchURI");
        if (response == null) {
            throw new RuntimeException("Received response from the Python Server was null. Check error log for details");
        }
        
        // Process JSON response
        try {
            TypeReference<HashMap<String, HashMap<Integer, List<Pair<Integer, Double>>>>> typeRef = 
                new TypeReference<HashMap<String, HashMap<Integer, List<Pair<Integer, Double>>>>>() {};
            HashMap<String, HashMap<Integer, List<Pair<Integer, Double>>>> responseMap = MAPPER.readValue(response, typeRef);
            return responseMap.get("similarity_batch");
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException occurred.", e.getMessage(), e);
            throw new RuntimeException("Mapping Json Failed");
        } catch (JsonProcessingException e) {
            logger.error("JsonProcessingException occurred during mapping json.", e.getMessage(), e);
            throw new RuntimeException("JsonProcessingException occurred during mapping json");
        }
    }

    /**
     * Sends a batch of PDF texts along with a label and a concept to the AI service and retrieves concept scores.
     * The scores are returned as a list of pairs, each containing an integer (representing the PDF text identifier)
     * and a double (the concept score for the text).
     * 
     * @param pdf A list of {@code PDFText} objects representing the PDF texts to be analyzed.
     * @param label A string label used in the analysis.
     * @param concept A string representing the concept for which scores are requested.
     * @return A list of pairs, each containing an integer and a double, representing the concept scores for the texts.
     * @throws IllegalArgumentException If any of the provided parameters (pdf list, label, or concept) is null or empty.
     * @throws RuntimeException If the response from the AI service is null or if JSON processing fails.
     */
    @Override
    public List<Pair<Integer, Double>> getConceptScores(List<PDFText> pdf, String label, String concept) {
        // Validate input parameters
        if (pdf == null || pdf.isEmpty()) {
            throw new IllegalArgumentException("PDF list is null or empty.");
        }
        if (label == null || label.isEmpty()) {
            throw new IllegalArgumentException("Label is null or empty.");
        }
        if (concept == null || concept.isEmpty()) {
            throw new IllegalArgumentException("Concept is null or empty.");
        }
    
        // Send PDF and receive response
        String response = sendPDF(pdf, label, concept, "ConceptURI");
        if (response == null) {
            throw new RuntimeException("Received response from the Python Server was null. Check error log for details");
        }
    
        // Process JSON response
        try {
            Map<String, List<Pair<Integer, Double>>> responseMap = MAPPER.readValue(response, new TypeReference<Map<String, List<Pair<Integer, Double>>>>() {});
            return responseMap.get("concept");
        } catch (JsonMappingException e) {
            logger.error("JSON mapping exception occurred.", e.getMessage(), e);
            throw new RuntimeException("Error occurred during JSON mapping");
        } catch (JsonProcessingException e) {
            logger.error("JSON processing exception occurred.", e.getMessage(), e);
            throw new RuntimeException("Error occurred during JSON processing");
        }
    }

    /**
     * Sends the PDF text, label, and concept to the specified URI and returns the response as a JSON string.
     *
     * @param pdf The list of {@link PDFText} to be sent.
     * @param label The label associated with the PDF texts.
     * @param concept The concept associated with the request, can be empty.
     * @param uriLabel The label of the URI to send the request to.
     * @return The response from the AI service as a JSON string.
     */
    private String sendPDF(List<PDFText> pdf, String label, String concept, String uriLabel) {
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(30000)  // connection timeout
            .build();
        try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(requestConfig).build()) {
            // Create a new HttpPost with the AI service URI
            HttpPost post = null;
            try {
                post = new HttpPost(PropertiesHandler.getURI(uriLabel));
            } catch (ExceptionInInitializerError e) {
                System.out.println(e.getLocalizedMessage());
                System.out.println(e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Failed to initialize HttpPost");
            }

            // Convert the texts to JSON
            String json = MAPPER.writeValueAsString(Map.of("texts", pdf, "label", label, "concept", concept));

            // Set the entity and headers
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            post.setEntity(entity);
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");

            HttpResponse response = client.execute(post);

            if (response == null) {
                throw new RuntimeException("Response is null. Look for logged error for the cause");
            }

            // Check if the response' status code is OK
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                // If the status code is not 200, log the error and throw a runtime exception
                logger.error("Error Status Code received. Status Code: {}", statusCode);
                throw new RuntimeException("Error Status Code recived. Status Code: " + statusCode);
            }

            HttpEntity receivedEntity = response.getEntity();
            try {
                String jsonResponse = EntityUtils.toString(receivedEntity);
                return jsonResponse;
            } catch (IOException e) {
                logger.error("IOException occurred while reading the response entity. Entity content: {}", receivedEntity, e);
                throw new RuntimeException("IOException occurred during parsing JSON response", e);
            } finally {
                try {
                    EntityUtils.consume(receivedEntity);
                } catch (IOException e) {
                    logger.warn("IOException occurred while consuming the response entity. This might indicate a resource leak.", e);
                }
            }
        }
        // Catch the errors and log the exceptions
        catch (JsonProcessingException e) {
            logger.error("Error formatting JSON.", e.getMessage(), e);
            return null;
        }
        catch (ClientProtocolException e) {
            logger.error("Client Protocol Exception occured. Exception: {}", e.getMessage(), e);
            return null;
        }
        catch (IOException e) {
            logger.error("IO exception occured. Exception: {}", e.getMessage(), e);
            return null;
        }
    }
}
