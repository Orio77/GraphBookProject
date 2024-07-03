package com.graphbook.backend.service.impl.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.neo4j.driver.exceptions.Neo4jException;
import org.neo4j.driver.Record;

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
        if (texts == null || texts.isEmpty()) {
            throw new IllegalArgumentException("Texts cannot be null");
        }
        if (label == null || label.isEmpty()) {
            throw new IllegalArgumentException("Label cannot be null or empty");
        }
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
        } 
        finally {
            disconnect();
        }
    }

    @Override
    public void disconnect() {
        if (driver != null) {
            driver.close();
        }
    }

    public void createEdge(PDFText text1, PDFText text2, double similarityScore, int pageNum) {
        if (text1 == null) {
            throw new IllegalArgumentException("text1 cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("text2 cannot be null");
        }
        try (Session session = driver.session()) {
            String content1 = text1.getText();
            String content2 = text2.getText();

            session.run("MATCH (a:Page {text: $textA}), (b:Page {text: $textB})" + "MERGE (a)-[r:RELATED {value: $value}]->(b)" + "MERGE (a)-[s:" + "P" + pageNum + " {value: $value}]->(b)", Values.parameters("textA", content1, "textB", content2, "value", similarityScore));
        } 
    }

    // HashMap = {id, {other_id, weight(double)}}
    public void createEdges(Map<Integer, List<Pair<Integer, Double>>> result, String label) {
        if (label == null || label.isEmpty()) {
            throw new IllegalArgumentException("Label cannot be null or empty");
        }
        if (result == null || result.isEmpty()) {
            throw new IllegalArgumentException("Result cannot be null or empty");
        }
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

    public void createEdges(String concept, List<Pair<Integer, Double>> conceptScores, String label) {
        connect();
        try (Session session = driver.session()) {
    
            String query = "CREATE (n:Concept {text: $concept, elementId: $concept}) " +
                           "WITH n " +
                           "UNWIND $scores as score " +
                           "MATCH (b:Page {elementId: score.pageElementId}) " +
                           "MERGE (a:Concept {elementId: $concept}) " +
                           "CREATE (a)-[r:EDGE {weight: score.score}]->(b)";
    
            List<Map<String, Object>> scores = conceptScores.stream().map(pair -> {
                String pageElementId = label + pair.getEl1();
                return Map.<String, Object>of("pageElementId", pageElementId, "score", pair.getEl2());
            }).collect(Collectors.toList());
    
            session.run(query, Map.of("concept", concept, "scores", scores));
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

    public List<String> getConceptList() {
        List<String> conceptList = null;
        try (Session session = driver.session()) {
            String cypherQuery = "MATCH (n:Concept) RETURN n.text AS text";
            conceptList = session.run(cypherQuery)
                                        .list(record -> record.get("text").asString());

            System.out.println(conceptList);
        }
        catch (Neo4jException e) {
            System.out.println();
            System.out.println("Neo4j operation failed: " + e.getMessage());
            System.out.println();
            throw e;
        }
        
        return conceptList;
    }

    public Map<String, List<Pair<String, Double>>> getConceptNodes(List<String> chosenConcepts) {
        Map<String, List<Pair<String, Double>>> conceptEdges = new HashMap<>();
        try (Session session = driver.session()) {
            for (String concept : chosenConcepts) {
                List<Pair<String, Double>> edges = new ArrayList<>();
                String query = "MATCH (c:Concept {text: $conceptName})-[r]->(n) RETURN n.elementId AS elementId, r.weight AS weight";
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("conceptName", concept);
                Result result = session.run(query, parameters);

                System.out.println(result.keys());
                System.out.println(result.hasNext());

                // Log the query and parameters
                System.out.println("Executing query for concept: " + concept);
                System.out.println("Query: " + query);
                System.out.println("Parameters: " + parameters);

                while (result.hasNext()) {
                    Record record = result.next();
                    String elementId = record.get("elementId").asString();
                    Double weight = record.get("weight").asDouble();
                    edges.add(new Pair<>(elementId, weight));
                }
                conceptEdges.put(concept, edges);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception (e.g., logging, rethrowing, etc.)
        }
        return conceptEdges;
    }

    @Override
    public void reset() {
        connect();
        try (Session session = driver.session()) {
            session.run("MATCH (n) DETACH DELETE n");
        } 
        finally {
            disconnect();
        }
    }
}
