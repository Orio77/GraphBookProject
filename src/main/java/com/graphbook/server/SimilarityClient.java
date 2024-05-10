package com.graphbook.server;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SimilarityClient {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final URI MY_URI = URI.create("http://localhost:5000/similarity");


    public static double getSimilarityDouble(String text1, String text2) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("http://localhost:5000/similarity");
            
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(Map.of("text1", text1, "text2", text2));
            
            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");
            
            org.apache.http.HttpResponse response = client.execute(post);
            String jsonResp = EntityUtils.toString(response.getEntity());
            Map<?, ?> respMap = mapper.readValue(jsonResp, Map.class);
            
            return (Double) respMap.get("similarity");
        }
    }

    public static Object getSimilarityResponsev1(String text1, String text2) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(MY_URI);

            String json = MAPPER.writeValueAsString(Map.of("text1", text1, "text2", text2));

            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            post.setHeader("Accept", "application/json");
            post.setHeader("COntent-type", "application/json");

            org.apache.http.HttpResponse response = client.execute(post);
            String jsonResponse = EntityUtils.toString(response.getEntity());
            Map<?, ?> responseMap = MAPPER.readValue(jsonResponse, Map.class);

            return responseMap.get("similarity");
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getSimilarityResponse(String text1, String text2) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(MY_URI);

            String json = MAPPER.writeValueAsString(Map.of("text1", text1, "text2", text2));

            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");

            org.apache.http.HttpResponse response = client.execute(post);
            String jsonResponse = EntityUtils.toString(response.getEntity());
            System.out.println("JSON response: " + jsonResponse);  // Print the raw JSON response
            Map<?, ?> responseMap = MAPPER.readValue(jsonResponse, Map.class);
            System.out.println("Parsed response: " + responseMap);  // Print the parsed map
            return responseMap.get("similarity");
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
