package org.example;

import org.example.graph.model.Graph;
import org.example.graph.util.GraphLoader;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("---");
            return;
        }

        Graph graph = GraphLoader.loadFromJsonSafe(args[0]);
        if (graph != null) {
            System.out.println("Successfully loaded graph:");
            System.out.println(graph);
            System.out.println("Number of edges: " + graph.getEdges().size());
            System.out.println("Source vertex: " + graph.getSource());
            System.out.println("Weight model: " + graph.getWeightModel());
        } else {
            System.out.println("Failed to load graph from: " + args[0]);
        }
    }
}