package org.example.graph.util;

import org.example.graph.models.Graph;
import org.example.graph.models.Edge;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Utility class for generating test graph datasets
 */
public class GraphGenerator {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Random random = new Random();

    /**
     * Generates a DAG (Directed Acyclic Graph)
     */
    public static Graph generateDAG(int vertices, int edges, int maxWeight, String name) {
        List<Edge> edgeList = new ArrayList<>();

        // Ensure we have a topological order by only allowing edges from lower to higher numbered vertices
        for (int i = 0; i < edges; i++) {
            int u = random.nextInt(vertices);
            int v;

            // Ensure v > u to maintain acyclicity, but handle edge cases
            if (u == vertices - 1) {
                // If u is the last vertex, we can't have v > u, so skip or choose different u
                u = random.nextInt(vertices - 1); // Choose u from 0 to vertices-2
            }
            v = random.nextInt(vertices - u - 1) + u + 1; // v > u to ensure acyclicity

            int weight = random.nextInt(maxWeight) + 1;
            edgeList.add(new Edge(u, v, weight));
        }

        // Remove duplicates
        Set<String> edgeSet = new HashSet<>();
        List<Edge> uniqueEdges = new ArrayList<>();
        for (Edge edge : edgeList) {
            String key = edge.getU() + "->" + edge.getV();
            if (!edgeSet.contains(key)) {
                uniqueEdges.add(edge);
                edgeSet.add(key);
            }
        }

        int source = random.nextInt(vertices);
        return new Graph(true, vertices, uniqueEdges, source, "edge");
    }

    /**
     * Generates a graph with cycles
     */
    public static Graph generateCyclicGraph(int vertices, int edges, int maxWeight, String name) {
        List<Edge> edgeList = new ArrayList<>();

        // First create a cycle to ensure there's at least one
        for (int i = 0; i < vertices; i++) {
            int u = i;
            int v = (i + 1) % vertices; // Creates a cycle
            int weight = random.nextInt(maxWeight) + 1;
            edgeList.add(new Edge(u, v, weight));
        }

        // Add remaining edges randomly
        int remainingEdges = edges - vertices;
        for (int i = 0; i < remainingEdges; i++) {
            int u = random.nextInt(vertices);
            int v = random.nextInt(vertices);
            int weight = random.nextInt(maxWeight) + 1;
            edgeList.add(new Edge(u, v, weight));
        }

        int source = random.nextInt(vertices);
        return new Graph(true, vertices, uniqueEdges(edgeList), source, "edge");
    }

    /**
     * Generates a mixed graph with both cyclic and acyclic components
     */
    public static Graph generateMixedGraph(int vertices, int edges, int maxWeight, String name) {
        List<Edge> edgeList = new ArrayList<>();

        // Create one cycle
        int cycleSize = Math.min(vertices / 2, 5);
        for (int i = 0; i < cycleSize; i++) {
            int u = i;
            int v = (i + 1) % cycleSize;
            int weight = random.nextInt(maxWeight) + 1;
            edgeList.add(new Edge(u, v, weight));
        }

        // Add DAG-like edges for the remaining vertices
        int remainingEdges = edges - cycleSize;
        for (int i = 0; i < remainingEdges; i++) {
            int u = random.nextInt(vertices);
            int v;

            // Ensure v > u for acyclicity, but with some randomness
            if (u < vertices - 1 && random.nextDouble() > 0.3) { // 70% acyclic edges
                v = random.nextInt(vertices - u - 1) + u + 1;
            } else {
                v = random.nextInt(vertices); // 30% random edges (could be cyclic)
            }

            int weight = random.nextInt(maxWeight) + 1;
            edgeList.add(new Edge(u, v, weight));
        }

        int source = random.nextInt(vertices);
        return new Graph(true, vertices, uniqueEdges(edgeList), source, "edge");
    }

    /**
     * Removes duplicate edges
     */
    private static List<Edge> uniqueEdges(List<Edge> edges) {
        Set<String> edgeSet = new HashSet<>();
        List<Edge> unique = new ArrayList<>();
        for (Edge edge : edges) {
            String key = edge.getU() + "->" + edge.getV();
            if (!edgeSet.contains(key)) {
                unique.add(edge);
                edgeSet.add(key);
            }
        }
        return unique;
    }

    /**
     * Generates all required datasets
     */
    public static void generateAllDatasets() {
        System.out.println("Generating test datasets...");

        // Small datasets (6-10 vertices)
        generateDataset("small_dag_1", 8, 12, 10, "dag");
        generateDataset("small_cyclic_1", 7, 10, 10, "cyclic");
        generateDataset("small_mixed_1", 9, 12, 10, "mixed");

        // Medium datasets (10-20 vertices)
        generateDataset("medium_dag_1", 15, 25, 15, "dag");
        generateDataset("medium_cyclic_1", 18, 30, 15, "cyclic");
        generateDataset("medium_mixed_1", 16, 28, 15, "mixed");

        // Large datasets (20-50 vertices)
        generateDataset("large_dag_1", 35, 60, 20, "dag");
        generateDataset("large_cyclic_1", 40, 70, 20, "cyclic");
        generateDataset("large_mixed_1", 45, 65, 20, "mixed");

        System.out.println("All datasets generated successfully!");
    }

    /**
     * Generates a single dataset and saves it to file
     */
    public static void generateDataset(String name, int vertices, int edges, int maxWeight, String type) {
        try {
            Graph graph;

            switch (type.toLowerCase()) {
                case "dag":
                    graph = generateDAG(vertices, edges, maxWeight, name);
                    break;
                case "cyclic":
                    graph = generateCyclicGraph(vertices, edges, maxWeight, name);
                    break;
                case "mixed":
                    graph = generateMixedGraph(vertices, edges, maxWeight, name);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown graph type: " + type);
            }

            // Create data directory if it doesn't exist
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            // Save to file
            File outputFile = new File("data/" + name + ".json");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputFile, graph);
            System.out.println("Generated: " + name + ".json (" + vertices + " vertices, " +
                    graph.getEdges().size() + " edges)");
        } catch (Exception e) {
            System.err.println("Error generating dataset " + name + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Improved DAG generation that handles edge cases better
     */
    public static Graph generateDAGImproved(int vertices, int edges, int maxWeight, String name) {
        List<Edge> edgeList = new ArrayList<>();

        // Generate edges ensuring u < v for acyclicity
        int attempts = 0;
        int maxAttempts = edges * 3; // Prevent infinite loops

        while (edgeList.size() < edges && attempts < maxAttempts) {
            int u = random.nextInt(vertices);
            int v = random.nextInt(vertices);

            // Only add edge if u < v (ensures acyclicity)
            if (u < v) {
                int weight = random.nextInt(maxWeight) + 1;
                edgeList.add(new Edge(u, v, weight));
            }
            attempts++;
        }

        // If we couldn't generate enough edges, fill with what we have
        if (edgeList.size() < edges) {
            System.out.println("Warning: Only generated " + edgeList.size() + " edges for " + name);
        }

        int source = random.nextInt(vertices);
        return new Graph(true, vertices, uniqueEdges(edgeList), source, "edge");
    }

    /**
     * Main method for generating datasets
     */
    public static void main(String[] args) {
        generateAllDatasets();
    }
}