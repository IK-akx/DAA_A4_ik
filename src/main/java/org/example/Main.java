package org.example;

import org.example.graph.dagsp.DAGShortestLongestPaths;
import org.example.graph.models.Graph;
import org.example.graph.scc.SCCFinder;
import org.example.graph.scc.SCCResult;
import org.example.graph.topo.KahnTopologicalSort;
import org.example.graph.topo.TopologicalSort;
import org.example.graph.topo.TopologicalSortResult;
import org.example.graph.dagsp.PathFinder;
import org.example.graph.dagsp.PathResult;
import org.example.graph.util.GraphLoader;
import org.example.graph.util.GraphGenerator;
import org.example.graph.metrics.Metrics;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Main <path-to-json-file>");
            System.out.println("Or: java Main --generate-datasets");
            return;
        }

        if ("--generate-datasets".equals(args[0])) {
            GraphGenerator.generateAllDatasets();
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

            // Perform full analysis with metrics
            performFullAnalysis(graph);

        } else {
            System.out.println("Failed to load graph from: " + args[0]);
        }
    }

    private static void performFullAnalysis(Graph graph) {
        // Perform SCC analysis
        SCCResult sccResult = performSCCAnalysis(graph);

        // Perform topological sort
        TopologicalSortResult topoResult = performTopologicalSort(graph, sccResult);

        // Perform path analysis
        performPathAnalysis(graph, topoResult);

        // Print summary
        printAnalysisSummary(graph, sccResult, topoResult);
    }

    private static SCCResult performSCCAnalysis(Graph graph) {
        System.out.println("=== Performing SCC Analysis ===");

        SCCFinder.SCCResultWithMetrics sccWithMetrics =
                SCCFinder.findStronglyConnectedComponentsWithMetrics(graph);

        SCCResult sccResult = sccWithMetrics.getResult();
        Metrics sccMetrics = sccWithMetrics.getMetrics();

        SCCFinder.printSCCResultsWithMetrics(sccResult, sccMetrics);
        System.out.printf("\nSCC analysis completed in: %.3f ms\n", sccMetrics.getElapsedTimeMillis());
        System.out.println();

        return sccResult;
    }

    private static TopologicalSortResult performTopologicalSort(Graph graph, SCCResult sccResult) {
        System.out.println("=== Performing Topological Sort ===");

        KahnTopologicalSort kahn = new KahnTopologicalSort();
        TopologicalSortResult topoResult = kahn.sortFromSCC(graph, sccResult);
        Metrics topoMetrics = kahn.getMetrics();

        TopologicalSort.printTopologicalResults(topoResult, sccResult);

        // Validate the topological order
        if (topoResult.isValid()) {
            boolean isValid = TopologicalSort.validateTopologicalOrder(graph, topoResult.getVertexOrder(), sccResult);
            System.out.println("Topological order validation: " + (isValid ? "PASSED" : "FAILED"));

            // Also validate component order
            boolean compValid = TopologicalSort.validateComponentOrder(sccResult.getCondensationGraph(), topoResult.getComponentOrder());
            System.out.println("Component order validation: " + (compValid ? "PASSED" : "FAILED"));
        }

        System.out.println(topoMetrics.toReport());
        System.out.printf("\nTopological sort completed in: %.3f ms\n", topoMetrics.getElapsedTimeMillis());
        System.out.println();

        return topoResult;
    }

    private static void performPathAnalysis(Graph graph, TopologicalSortResult topoResult) {
        System.out.println("=== Performing Path Analysis ===");

        // Get source from graph
        int source = graph.getSource();

        // Compute both shortest and longest paths with metrics
        DAGShortestLongestPaths pathFinder = new DAGShortestLongestPaths();
        Map<String, PathResult> pathResults = pathFinder.computeAllPaths(graph, topoResult, source);

        PathResult shortestResult = pathResults.get("shortest");
        PathResult longestResult = pathResults.get("longest");

        // Print results with metrics
        PathFinder.printPathResultsWithMetrics(shortestResult, pathFinder.getMetrics());
        System.out.println();
        PathFinder.printPathResultsWithMetrics(longestResult, pathFinder.getMetrics());
        System.out.println();
        PathFinder.printPathComparison(shortestResult, longestResult);
    }

    private static void printAnalysisSummary(Graph graph, SCCResult sccResult, TopologicalSortResult topoResult) {
        System.out.println("=== Analysis Summary ===");
        System.out.println("Graph: " + graph.getN() + " vertices, " + graph.getEdges().size() + " edges");
        System.out.println("SCCs: " + sccResult.getComponents().size() + " components");
        System.out.println("Largest SCC: " + sccResult.getComponents().stream()
                .mapToInt(List::size)
                .max()
                .orElse(0) + " vertices");
        System.out.println("Topological Order: " + (topoResult.isValid() ? "VALID" : "INVALID"));
        System.out.println("Is DAG: " + (sccResult.getComponents().size() == graph.getN() ? "Yes" : "No (has cycles)"));
    }
}