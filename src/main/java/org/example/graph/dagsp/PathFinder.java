package org.example.graph.dagsp;

import org.example.graph.metrics.Metrics;
import org.example.graph.models.Graph;
import org.example.graph.topo.TopologicalSortResult;

import java.util.List;
import java.util.Map;


public class PathFinder {

    /**
     * Computes shortest paths from source in DAG
     */
    public static PathResult findShortestPaths(Graph dag, TopologicalSortResult topoResult, int source) {
        DAGShortestLongestPaths pathFinder = new DAGShortestLongestPaths();
        return pathFinder.shortestPathsFromSource(dag, topoResult, source);
    }

    /**
     * Computes longest paths from source in DAG
     */
    public static PathResult findLongestPaths(Graph dag, TopologicalSortResult topoResult, int source) {
        DAGShortestLongestPaths pathFinder = new DAGShortestLongestPaths();
        return pathFinder.longestPathsFromSource(dag, topoResult, source);
    }

    /**
     * Computes both shortest and longest paths
     */
    public static Map<String, PathResult> findAllPaths(Graph dag, TopologicalSortResult topoResult, int source) {
        DAGShortestLongestPaths pathFinder = new DAGShortestLongestPaths();
        return pathFinder.computeAllPaths(dag, topoResult, source);
    }

    /**
     * Prints path results in a formatted way
     */
    public static void printPathResults(PathResult result) {
        if (result.isShortestPath()) {
            System.out.println("=== Shortest Paths Analysis ===");
        } else {
            System.out.println("=== Longest Paths Analysis ===");
        }

        System.out.println("Source vertex: " + result.getSource());
        System.out.println();

        System.out.println(result.toString());

        // Additional validation for critical path
        if (result.getCriticalPath() != null && !result.getCriticalPath().isEmpty()) {
            System.out.println("Critical Path Details:");
            System.out.println("  Path: " + result.getCriticalPath());
            System.out.println("  Length: " + result.getCriticalPathLength());

            // Show path reconstruction for demonstration
            if (!result.getCriticalPath().isEmpty()) {
                int lastVertex = result.getCriticalPath().get(result.getCriticalPath().size() - 1);
                List<Integer> reconstructed = result.reconstructPath(lastVertex);
                System.out.println("  Reconstructed: " + reconstructed);
                System.out.println("  Reconstruction matches: " +
                        reconstructed.equals(result.getCriticalPath()));
            }
        }
    }

    /**
     * Prints comparison of shortest and longest paths
     */
    public static void printPathComparison(PathResult shortest, PathResult longest) {
        System.out.println("=== Path Comparison ===");
        System.out.println("Source vertex: " + shortest.getSource());
        System.out.println();

        System.out.println("Shortest vs Longest distances:");
        for (int i = 0; i < shortest.getDistances().size(); i++) {
            int shortDist = shortest.getDistances().get(i);
            int longDist = longest.getDistances().get(i);

            System.out.print("Vertex " + i + ": ");
            if (shortDist == Integer.MAX_VALUE) {
                System.out.print("UNREACHABLE");
            } else {
                System.out.print("short=" + shortDist);
            }

            System.out.print(" | ");

            if (longDist == Integer.MIN_VALUE) {
                System.out.print("UNREACHABLE");
            } else {
                System.out.print("long=" + longDist);
            }

            // Show path if available
            if (shortDist != Integer.MAX_VALUE) {
                List<Integer> shortPath = shortest.reconstructPath(i);
                if (!shortPath.isEmpty()) {
                    System.out.print(" (short path: " + shortPath + ")");
                }
            }

            System.out.println();
        }
    }

    /**
     * Prints path results with metrics
     */
    public static void printPathResultsWithMetrics(PathResult result, Metrics metrics) {
        printPathResults(result);
        if (metrics != null) {
            System.out.println(metrics.toReport());
        }
    }
}