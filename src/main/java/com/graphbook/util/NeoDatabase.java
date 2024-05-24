package com.graphbook.util;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.neo4j.driver.exceptions.Neo4jException;

import com.graphbook.element.PDFText;
import com.graphbook.util.interfaces.IDatabase;
import com.graphbook.util.interfaces.ISimilarityCalculator;

public class NeoDatabase implements IDatabase {
    private Driver driver = null;

    @Override
    public void connect() {
        try {
            // driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "123456siedem"));
            driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "testingtests"));
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
    public void save(List<PDFText> texts, String label) {
        connect();
        try (Session session = driver.session()) {
            // Ensure label is safely escaped
            String safeLabel = label.replace("`", "``");

            // Use streams to build the query and parameters
            AtomicInteger index = new AtomicInteger(0);
            Map<String, Object> parameters = texts.stream()
                .collect(Collectors.toMap(
                    text -> "text" + index.getAndIncrement(),
                    PDFText::getText
                ));

            String query = parameters.keySet().stream()
                .map(key -> String.format("CREATE (:Page:`%s` {text: $%s})", safeLabel, key))
                .collect(Collectors.joining(" "));

            // Execute all node creations in a single transaction if the query is not empty
            if (!query.isEmpty()) {
                session.run(query, parameters);
            }
        } catch (Neo4jException e) {
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

    public void createEdge(PDFText text1, PDFText text2, double similarityScore, int pageNum) {
        try (Session session = driver.session()) {
            String content1 = text1.getText();
            String content2 = text2.getText();

            session.run("MATCH (a:Page {text: $textA}), (b:Page {text: $textB})" + "MERGE (a)-[r:RELATED {value: $value}]->(b)" + "MERGE (a)-[s:" + "P" + pageNum + " {value: $value}]->(b)", Values.parameters("textA", content1, "textB", content2, "value", similarityScore));
        } 
        catch (Neo4jException e) {
            System.out.println();
            System.out.println("Neo4j operation failed: " + e.getMessage());
            System.out.println();
            throw e;
        }
    }

    // TODO allow for continuing with edge creation, by allowing the user to choose the pdf he wishes to carry on with and then check if some of the nodes/edges are present
    public void createAllEdges(List<PDFText> texts, ISimilarityCalculator calculator, double similarityTreshold) {
        connect();
        var start = TimeMeasurer.startMeasuring();
        for (int i = 0; i < texts.size(); i++) {
            PDFText text1 = texts.get(i);
            for (int j = i+1; j < texts.size()-1; j++) {
                PDFText text2 = texts.get(j);
                double score = calculator.calculate(text1, text2);
                if (score < similarityTreshold) continue;
                createEdge(text1, text2, score, i);
            }
        }
        var end = TimeMeasurer.endMeasuring();
        String timePassed = TimeMeasurer.getTimePassedString(start, end);
        new JDataManager().addToMetadata(Paths.get("C:/Users/macie/GraphBookTestDir/saved/rules_50page_fragment/metadata.txt"), "Time taken to create the Edges", timePassed);
        disconnect();
    }

    public void createWeightedRelationships() {
        connect();
        try (Session session = driver.session()) {
            String query = 
                "MATCH (a:Page), (b:Page) " +
                "WHERE id(a) < id(b) " +
                "MERGE (a)-[r:CONNECTED]->(b) " +
                "ON CREATE SET r.weight = rand() " +
                "RETURN a, b, r";

            session.run(query);
        }
        disconnect();
    }

    // HashMap = {id, {other_id, weight(double)}}
    public void createEdges(Map<Integer, List<List<Double>>> result) {

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

    public static void main(String[] args) {
        new NeoDatabase().createWeightedRelationships();
    }
}
