package com.graphbook.util;

import java.util.List;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.neo4j.driver.exceptions.Neo4jException;

import com.graphbook.elements.PDFText;
import com.graphbook.server.ApacheHTTP_SimilarityClient;
import com.graphbook.util.interfaces.IDatabase;
import com.graphbook.util.interfaces.ISimilarityCalculator;
import com.graphbook.util.interfaces.ISimilarityClient;

public class NeoDatabase implements IDatabase {
    private Driver driver = null;

    @Override
    public void connect() {
        try {
            driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "123456siedem"));
            driver.verifyConnectivity();
        } catch (Exception e) {
            System.out.println("Failed to connect to the database");
            e.printStackTrace();
            if (e instanceof org.neo4j.driver.exceptions.ServiceUnavailableException) {
                System.out.println("Ensure the database is running and that there is a working network connection to it.");
            }
        }
    }

    @Override
    public void save(List<PDFText> texts) {
        connect();
        try (Session session = driver.session()) {
            for (PDFText text : texts) {
                String content = text.getText();
                session.run("CREATE (p:Page {text: $text})", Values.parameters("text", content));
            }
        }
        catch (Neo4jException e) {
            System.out.println();
            System.out.println("Neo4j operation failed: " + e.getMessage());
            System.out.println();
            throw e;
        }
        disconnect();
    }

    @Override
    public void disconnect() {
        if (driver != null) {
            driver.close();
        }
    }

    public void createEdge(PDFText text1, PDFText text2, double similarityScore) {
        try (Session session = driver.session()) {
            String content1 = text1.getText();
            String content2 = text2.getText();

            session.run("MATCH (a:Page {text: $textA}), (b:Page {text: $textB})" + "MERGE (a)-[r:RELATED {value: $value}]->(b)", Values.parameters("textA", content1, "textB", content2, "value", similarityScore));
        } 
        catch (Neo4jException e) {
            System.out.println();
            System.out.println("Neo4j operation failed: " + e.getMessage());
            System.out.println();
            throw e;
        }
    }

    private final ISimilarityClient client = new ApacheHTTP_SimilarityClient(new SimpleScoreExtractor());
    public void createAllEdges(List<PDFText> texts, ISimilarityCalculator calculator, double similarityTreshold) {
        connect();
        System.out.println("Connected successfully");
        for (int i = 121; i < 170; i++) { // TODO Adjust the loop parameters // i < texts.size()  and j < texts.size()-1
            System.out.println(i + "th iteration");
            PDFText text1 = texts.get(i);
            for (int j = i+1; j < 169; j++) {
                System.out.println(j);
                PDFText text2 = texts.get(j);
                // double score = calculator.calculate(text1, text2);
                double score = (Double) client.getSimilarityResponse(text1.getText(), text2.getText());
                if (score < similarityTreshold) continue;
                createEdge(text1, text2, score);
            }
        }
        disconnect();
    }

    public void clearAllEdges() {
        connect();
        try (Session session = driver.session()) {
            session.run("MATCH ()-[r]-() DELETE r");
        } catch (Neo4jException e) {
            System.out.println();
            System.out.println("Neo4j operation failed: " + e.getMessage());
            System.out.println();
            throw e;
        }
        finally {
            disconnect();
        }
    }

    public void reset() {
        connect();
        try (Session session = driver.session()) {
            session.run("MATCH (n) DETACH DELETE n");
        } catch (Neo4jException e) {
            System.out.println();
            System.out.println("Neo4j operation failed: " + e.getMessage());
            System.out.println();
            throw e;
        }
        disconnect();
    }
}
