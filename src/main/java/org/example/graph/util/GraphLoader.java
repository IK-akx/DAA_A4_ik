package org.example.graph.util;

import org.example.graph.model.Graph;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class GraphLoader {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Loads a graph from JSON file
     *
     * @param filePath path to the JSON file
     * @return Graph object
     * @throws IOException if file cannot be read or parsed
     */
    public static Graph loadFromJson(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }

        Graph graph = objectMapper.readValue(file, Graph.class);

        // Validate the loaded graph
        if (!graph.validate()) {
            throw new IOException("Invalid graph structure in file: " + filePath);
        }

        // Build adjacency list for efficient operations
        graph.buildAdjacencyList();

        return graph;
    }

    /**
     * Loads a graph from JSON file with error handling
     *
     * @param filePath path to the JSON file
     * @return Graph object or null if error occurs
     */
    public static Graph loadFromJsonSafe(String filePath) {
        try {
            return loadFromJson(filePath);
        } catch (IOException e) {
            System.err.println("Error loading graph from " + filePath + ": " + e.getMessage());
            return null;
        }
    }
}
