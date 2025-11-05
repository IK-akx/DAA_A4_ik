package org.example.graph.scc;

import org.example.graph.metrics.Metrics;
import org.example.graph.models.Graph;

import java.util.List;


public class SCCFinder {

    //  Finds SCCs using Tarjan's algorithm
    public static SCCResult findStronglyConnectedComponents(Graph graph) {
        TarjanSCC tarjan = new TarjanSCC();
        return tarjan.findSCCs(graph);
    }

    /**
     * Finds SCCs and returns both result and metrics
     */
    public static SCCResultWithMetrics findStronglyConnectedComponentsWithMetrics(Graph graph) {
        TarjanSCC tarjan = new TarjanSCC();
        SCCResult result = tarjan.findSCCs(graph);
        return new SCCResultWithMetrics(result, tarjan.getMetrics());
    }

    /**
     * Prints SCC results with metrics
     */
    public static void printSCCResultsWithMetrics(SCCResult result, Metrics metrics) {
        printSCCResults(result);
        if (metrics != null) {
            System.out.println(metrics.toReport());
        }
    }

    /**
     * Helper class to return both result and metrics
     */
    public static class SCCResultWithMetrics {
        private final SCCResult result;
        private final Metrics metrics;

        public SCCResultWithMetrics(SCCResult result, Metrics metrics) {
            this.result = result;
            this.metrics = metrics;
        }

        public SCCResult getResult() { return result; }
        public Metrics getMetrics() { return metrics; }
    }

    // Prints SCC results in a formatted way
    public static void printSCCResults(SCCResult result) {
        System.out.println("=== Strongly Connected Components Analysis ===");
        System.out.println("Number of SCCs: " + result.getComponents().size());
        System.out.println();

        System.out.println("Components and their sizes:");
        for (int i = 0; i < result.getComponents().size(); i++) {
            List<Integer> component = result.getComponents().get(i);
            System.out.println("SCC " + i + ": " + component + " (size: " + component.size() + ")");
        }

        System.out.println();
        System.out.println("Condensation Graph Info:");
        Graph condensation = result.getCondensationGraph();
        System.out.println("Vertices: " + condensation.getN());
        System.out.println("Edges: " + condensation.getEdges().size());
        System.out.println("Is DAG: " + (condensation.getEdges().size() > 0 ? "Yes" : "Yes (trivial)"));
    }
}
