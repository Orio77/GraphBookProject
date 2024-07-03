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
 * ApacheHTTP_SimilarityClient is a class that communicates with a local AI service via HTTP requests.
 * The purpose of this class is to send two texts to the AI service and receive a similarity score as a double in the range of 0.0 to 100.0.
 * This class implements the IAISimilarityClient interface.
 */
public class ApacheHTTP_SimilarityClient implements IAISimilarityClient {
    // ObjectMapper is used to convert Java objects to JSON and vice versa
    private final ObjectMapper MAPPER;
    // Logger is used for logging errors
    private final Logger logger = LogManager.getLogger(ApacheHTTP_SimilarityClient.class);

    /**
     * Constructor for the ApacheHTTP_SimilarityClient class.
     * 
     * @param extractor An instance of IAIResponseSimilarityScoreExtractor to extract the similarity score from the AI service's response.
     */
    public ApacheHTTP_SimilarityClient() {
        this.MAPPER = new ObjectMapper();
    }

    @Override
    public Map<Integer, List<Pair<Integer, Double>>> getSimilarityBatchResponse(List<PDFText> pdf, String label) {
        String response = sendPDF(pdf, label, new String(), "SimilarityBatchURI");
        if (response == null) {
            throw new RuntimeException("Received response from the Python Server was null. Check error log for details");
        }
        String jsonResponse = response;
        
        HashMap<String, HashMap<Integer, List<Pair<Integer, Double>>>> responseMap = null;
        try {
            responseMap = MAPPER.readValue(jsonResponse, new TypeReference<HashMap<String, HashMap<Integer, List<Pair<Integer, Double>>>>>() {});
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException occured.", e.getMessage(), e);
            throw new RuntimeException("Mapping Json Failed");
        } catch (JsonProcessingException e) {
            logger.error("JsonMappingException occured.", e.getMessage(), e);
            throw new RuntimeException("JsonProcessingException occured during mapping json");
        }

        HashMap<Integer, List<Pair<Integer, Double>>> res = responseMap.get("similarity_batch");
        return res;
    }

    @Override
    public List<Pair<Integer, Double>> getConceptScores(List<PDFText> pdf, String label, String concept) {
        String response = sendPDF(pdf, label, concept, "ConceptURI");
        if (response == null) {
            throw new RuntimeException("Received response from the Python Server was null. Check error log for details");
        }
        String jsonResponse = response;

        Map<String, List<Pair<Integer, Double>>> responseMap = null;

        try {
            responseMap = MAPPER.readValue(jsonResponse, new TypeReference<Map<String, List<Pair<Integer, Double>>>>() {});
        } 
        catch (JsonMappingException e) {
            logger.error("JsonMappingException occured.", e.getMessage(), e);
            throw new RuntimeException("Mapping Json Failed");
        } catch (JsonProcessingException e) {
            logger.error("JsonMappingException occured.", e.getMessage(), e);
            throw new RuntimeException("JsonProcessingException occured during mapping json");
        }

        List<Pair<Integer, Double>> res = responseMap.get("concept");
        return res;
    }

    private String sendPDF(List<PDFText> pdf, String label, String concept, String uriLabel) {
        RequestConfig requestConfig = RequestConfig.custom()
            // .setSocketTimeout(30000)  // socket timeout
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
