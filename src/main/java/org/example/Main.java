package org.example;

import org.example.graph.models.Graph;
import org.example.graph.scc.SCCFinder;
import org.example.graph.scc.SCCResult;
import org.example.graph.util.GraphLoader;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Main <path-to-json-file>");
            return;
        }

        Graph graph = GraphLoader.loadFromJsonSafe(args[0]);
        if (graph != null) {
            System.out.println("Successfully loaded graph:");
            System.out.println(graph);
            System.out.println("Number of edges: " + graph.getEdges().size());
            System.out.println("Source vertex: " + graph.getSource());
            System.out.println("Weight model: " + graph.getWeightModel());
            System.out.println();

            // Perform SCC analysis
            performSCCAnalysis(graph);
        } else {
            System.out.println("Failed to load graph from: " + args[0]);
        }
    }

    private static void performSCCAnalysis(Graph graph) {
        System.out.println("=== Performing SCC Analysis ===");
        long startTime = System.nanoTime();

        SCCResult sccResult = SCCFinder.findStronglyConnectedComponents(graph);

        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;

        SCCFinder.printSCCResults(sccResult);
        System.out.printf("\nSCC analysis completed in: %.3f ms\n", durationMs);
    }
}