package com.graphbook.backend.service.impl.database;

import java.util.HashMap;
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

import com.graphbook.backend.model.PDFText;
import com.graphbook.backend.model.Pair;
import com.graphbook.backend.service.IDatabase;

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

            AtomicInteger elementIndex = new AtomicInteger(0);
            String query = parameters.keySet().stream()
                .map(key -> {
                    int idx = elementIndex.getAndIncrement();
                    return String.format("CREATE (n%d:Page:`%s` {text: $%s, elementId: '%s%d'})", idx, safeLabel, key, safeLabel, idx);
                })
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
    public void createEdgess(HashMap<Integer, List<Pair<Integer, Double>>> result, String label) {
        connect();
        try (Session session = driver.session()) {
            for (Map.Entry<Integer, List<Pair<Integer, Double>>> entry : result.entrySet()) {
                int id = entry.getKey();
                List<Pair<Integer, Double>> edges = entry.getValue();
                for (Pair<Integer, Double> edge : edges) {
                    int other_id = edge.getEl1();
                    double weight = edge.getEl2();
                    String query = "MATCH (a:" + label + "{id: " + id + "}), (b:" + label + "{id: " + other_id + "}) " + 
                                    "MERGE (a)-[r:RELATED]->(b) " +
                                    "ON CREATE SET r.weight = " + weight + " " + 
                                    "RETURN a, b, r";
                    session.run(query);
                }
            }
        } 
        finally {
            disconnect();
        }
    }

    public void createEdges(Map<Integer, List<Pair<Integer, Double>>> result, String label) {
        int batchSize = 1000; // Adjust based on your system's capabilities
        List<Map<String, Object>> edgeList = result.entrySet().stream()
            .flatMap(entry -> entry.getValue().stream()
                .map(edge -> Map.<String, Object>of(
                    "id", entry.getKey(),
                    "other_id", edge.getEl1(),
                    "weight", edge.getEl2()
                ))
            ).collect(Collectors.toList());
    
        connect();
        try (Session session = driver.session()) {
            for (int i = 0; i < edgeList.size(); i += batchSize) {
                List<Map<String, Object>> batch = edgeList.subList(i, Math.min(i + batchSize, edgeList.size()));
                String query = "UNWIND $edges AS edge " +
                               "MATCH (a {elementId: $label + edge.id}), (b {elementId: $label + edge.other_id}) " +
                               "MERGE (a)-[r:RELATED]->(b) " +
                               "ON CREATE SET r.weight = edge.weight " +
                               "RETURN a, b, r";
                session.run(query, Map.of("edges", batch, "label", label));
            }
        } finally {
            disconnect();
        }
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

    @Override
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

    @Override
    public void createAllEdges(List<PDFText> texts, double similarityTreshold) {
        return;
    }

}
