package org.example;

import org.example.graph.models.Graph;
import org.example.graph.scc.SCCFinder;
import org.example.graph.scc.SCCResult;
import org.example.graph.topo.TopologicalSort;
import org.example.graph.topo.TopologicalSortResult;
import org.example.graph.dagsp.PathFinder;
import org.example.graph.dagsp.PathResult;
import org.example.graph.util.GraphLoader;

import java.util.Map;

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
            SCCResult sccResult = performSCCAnalysis(graph);

            // Perform topological sort
            TopologicalSortResult topoResult = performTopologicalSort(graph, sccResult);

            // Perform path analysis
            performPathAnalysis(graph, topoResult);

        } else {
            System.out.println("Failed to load graph from: " + args[0]);
        }
    }

    private static SCCResult performSCCAnalysis(Graph graph) {
        System.out.println("=== Performing SCC Analysis ===");
        long startTime = System.nanoTime();

        SCCResult sccResult = SCCFinder.findStronglyConnectedComponents(graph);

        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;

        SCCFinder.printSCCResults(sccResult);
        System.out.printf("\nSCC analysis completed in: %.3f ms\n", durationMs);
        System.out.println();

        return sccResult;
    }

    private static TopologicalSortResult performTopologicalSort(Graph graph, SCCResult sccResult) {
        System.out.println("=== Performing Topological Sort ===");
        long startTime = System.nanoTime();

        TopologicalSortResult topoResult = TopologicalSort.sortFromOriginal(graph, sccResult);

        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;

        TopologicalSort.printTopologicalResults(topoResult, sccResult);

        // Validate the topological order
        if (topoResult.isValid()) {
            boolean isValid = TopologicalSort.validateTopologicalOrder(graph, topoResult.getVertexOrder(), sccResult);
            System.out.println("Topological order validation: " + (isValid ? "PASSED" : "FAILED"));

            // Also validate component order
            boolean compValid = TopologicalSort.validateComponentOrder(sccResult.getCondensationGraph(), topoResult.getComponentOrder());
            System.out.println("Component order validation: " + (compValid ? "PASSED" : "FAILED"));
        }

        System.out.printf("\nTopological sort completed in: %.3f ms\n", durationMs);
        System.out.println();

        return topoResult;
    }

    private static void performPathAnalysis(Graph graph, TopologicalSortResult topoResult) {
        System.out.println("=== Performing Path Analysis ===");
        long startTime = System.nanoTime();

        // Get source from graph
        int source = graph.getSource();

        // Compute both shortest and longest paths
        Map<String, PathResult> pathResults = PathFinder.findAllPaths(graph, topoResult, source);

        PathResult shortestResult = pathResults.get("shortest");
        PathResult longestResult = pathResults.get("longest");

        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;

        // Print results
        PathFinder.printPathResults(shortestResult);
        System.out.println();
        PathFinder.printPathResults(longestResult);
        System.out.println();
        PathFinder.printPathComparison(shortestResult, longestResult);

        System.out.printf("\nPath analysis completed in: %.3f ms\n", durationMs);
    }
}