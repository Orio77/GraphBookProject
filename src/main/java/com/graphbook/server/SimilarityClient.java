package com.graphbook.server;

import java.io.IOException;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphbook.util.CONSTANTS;
import com.graphbook.util.DataSaver;

public class SimilarityClient {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    

    public static Object getSimilarityResponse(String text1, String text2) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(CONSTANTS.MY_URI);

            String json = MAPPER.writeValueAsString(Map.of("text1", text1, "text2", text2));

            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            post.setEntity(entity);
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");

            org.apache.http.HttpResponse response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                System.out.println("Received error code " + statusCode);
                logError(statusCode, json, text1, text2);
                return Double.valueOf(-1);
            }

            String jsonResponse = EntityUtils.toString(response.getEntity());
            System.out.println("JSON response: " + jsonResponse);  // Print the raw JSON response
            Map<?, ?> responseMap = MAPPER.readValue(jsonResponse, Map.class);
            System.out.println("Parsed response: " + responseMap);  // Print the parsed map
            String responseString = (String) responseMap.get("similarity");
            return extractScoreAdjusted(responseString);
        }
        catch (SocketException e) {
            System.out.println("Caught a SocketException. Message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            System.out.println("Stack trace:");
            e.printStackTrace();
            return null;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static double extractScoreAdjusted(String response) {
        char[] digits = new char[5];
        int i = 0;

        while (i < 5 && (response.charAt(i) == '.' || Character.isDigit(response.charAt(i)))) {
            digits[i] = response.charAt(i);
            i++;
        }

        return Double.parseDouble(new String(digits));
    }

    public static double extractScore(String response) {
        Pattern pattern = Pattern.compile("Score: ([0-9]*\\.?[0-9]+)");
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String scoreTextString = matcher.group();
            String scoreString = scoreTextString.substring(scoreTextString.indexOf(" ") + 1);
            double score = Double.parseDouble(scoreString);
            return score;
        }
        else return -1;
    }

    private static void logError(int errorCode, String sentJson, String text1, String text2) { 
        DataSaver saver = new DataSaver();

        // create file for error log
        Path dirPath = saver.createDir(CONSTANTS.ERROR_LOG_PATH);
        Path filePath = saver.createFile(dirPath.toString(), "log");

        // Write into the file
        try {
            Files.write(filePath, ("Error code: " + Integer.toString(errorCode) + "\n" +
                                    "Sent Json: " + sentJson + "\n\n" +
                                    "TEXT1: " + text1 + "\n\n" +
                                    "TEXT2: " + text2 + "\n\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            try {
                Files.write(filePath, (Arrays.toString(e.getStackTrace()) + "\n" + e.getMessage() + "\n").getBytes(), StandardOpenOption.APPEND);
            } catch (IOException ex) {
                return;
            }
        }
    }
}
