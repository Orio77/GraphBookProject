package com.graphbook.server;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphbook.util.CONSTANTS;
import com.graphbook.util.interfaces.IAIResponseSimilarityScoreExtractor;
import com.graphbook.util.interfaces.IAISimilarityClient;
import com.graphbook.util.interfaces.IDataSaver;

/**
 * ApacheHTTP_SimilarityClient is a class that communicates with a local AI service via HTTP requests.
 * The purpose of this class is to send two texts to the AI service and receive a similarity score as a double in the range of 0.0 to 100.0.
 * This class implements the IAISimilarityClient interface.
 */
public class ApacheHTTP_SimilarityClient implements IAISimilarityClient {
    // ObjectMapper is used to convert Java objects to JSON and vice versa
    private final ObjectMapper MAPPER;
    // SimilarityScoreExtractor is used to extract score out of an AI response
    private final IAIResponseSimilarityScoreExtractor extractor;
    // Logger is used for logging errors
    private final Logger logger = LogManager.getLogger(ApacheHTTP_SimilarityClient.class);

    /**
     * Constructor for the ApacheHTTP_SimilarityClient class.
     * 
     * @param extractor An instance of IAIResponseSimilarityScoreExtractor to extract the similarity score from the AI service's response.
     * @param errorDataSaver An instance of IDataSaver to save error data if any error occurs during the process.
     */
    public ApacheHTTP_SimilarityClient(IAIResponseSimilarityScoreExtractor extractor, IDataSaver errorDataSavaer) {
        this.extractor = extractor;
        this.MAPPER = new ObjectMapper();
    }
    
    /**
     * Sends a POST request with two texts to an AI service and retrieves their similarity score.
     * If an error occurs during the process, it throws a RuntimeException with a specific error message.
     *
     * @param text1 The first text to compare. It should not be null or empty.
     * @param text2 The second text to compare. It should not be null or empty.
     * @return The similarity score as a Double. If an error occurs, it returns -1.
     * @throws RuntimeException if the response from the server is null, if the status code of the response is not 200,
     *                          if there's a problem parsing the JSON response or if there's a problem mapping the JSON response to a Map.
     */
    @Override
    public Object getSimilarityResponse(String text1, String text2) {
        // Get the response
        HttpResponse response = sendTexts(text1, text2);

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

        // Parse the response
        String jsonResponse = null;
        try {
            jsonResponse = EntityUtils.toString(response.getEntity());
        } catch (ParseException e) {
            logger.error("ParseException occured. Response was: {}", response, e.getMessage(), e);
            throw new RuntimeException("Parsing Json Failed");
        } catch (IOException e) {
            logger.error("IOException occured.", e.getMessage(), e);
            throw new RuntimeException("IOException occured during parsing Json response");
        }

        Map<?, ?> responseMap = null;
        try {
            responseMap = MAPPER.readValue(jsonResponse, Map.class);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException occured.", e.getMessage(), e);
            throw new RuntimeException("Mapping Json Failed");
        } catch (JsonProcessingException e) {
            logger.error("JsonMappingException occured.", e.getMessage(), e);
            throw new RuntimeException("JsonProcessingException occured during mapping json");
        }
        String responseString = (String) responseMap.get("similarity");

        // Extract the similarity score and return it
        return extractor.extract(responseString);
    }

    /**
 * This method sends two texts to a Python server using HTTP POST and returns the server's response.
 * It creates an HTTP client, sets up a POST request with the texts as JSON in the request body, and executes the request.
 * 
 * If an error occurs during this process, it is logged and the method returns null.
 * 
 * @param text1 The first text to be compared. This should be a non-null String.
 * @param text2 The second text to be compared. This should be a non-null String.
 * 
 * @return The HttpResponse from the Python server. If an error occurs during the request, this method returns null.
 * 
 * @throws JsonProcessingException If there is an error formatting the texts as JSON.
 * @throws ClientProtocolException If there is an HTTP protocol error.
 * @throws IOException If there is an error executing the HTTP request.
 */
    private HttpResponse sendTexts(String text1, String text2) {
        HttpResponse response = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Create a new HttpPost with the AI service URI
            HttpPost post = new HttpPost(CONSTANTS.MY_URI);

            // Convert the texts to JSON
            String json = MAPPER.writeValueAsString(Map.of("text1", text1, "text2", text2));

            // Set the entity and headers
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            post.setEntity(entity);
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");

            // Execute the response
            response = client.execute(post);
            return response;
        }
        // Catch the errors and log the exceptions
        catch (JsonProcessingException e) {
            logger.error("Error formatting JSON. Text1: {}, Text2: {}", text1, text2, e.getMessage(), e);
            return null;
        }
        catch (ClientProtocolException e) {
            logger.error("CLient Protocol Exception occured. Exception: {}", e.getMessage(), e);
            return null;
        }
        catch (IOException e) {
            logger.error("IO exception occured. Exception: {}", e.getMessage(), e);
            return null;
        }
    }
}
