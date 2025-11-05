package org.example.graph.topo;

import org.example.graph.metrics.Metrics;
import org.example.graph.models.Graph;
import org.example.graph.models.Edge;
import org.example.graph.scc.SCCResult;

import java.util.*;

public class TopologicalSort {

    /**
     * Performs topological sort on condensation graph
     */
    public static TopologicalSortResult sort(Graph condensationGraph, SCCResult sccResult) {
        KahnTopologicalSort kahn = new KahnTopologicalSort();
        return kahn.sort(condensationGraph, sccResult);
    }

    /**
     * Performs topological sort directly from original graph and SCC result
     */
    public static TopologicalSortResult sortFromOriginal(Graph originalGraph, SCCResult sccResult) {
        KahnTopologicalSort kahn = new KahnTopologicalSort();
        return kahn.sortFromSCC(originalGraph, sccResult);
    }

    /**
     * Performs topological sort with metrics
     */
    public static TopologicalSortResult sortWithMetrics(Graph condensationGraph, SCCResult sccResult, Metrics metrics) {
        KahnTopologicalSort kahn = new KahnTopologicalSort();
        TopologicalSortResult result = kahn.sort(condensationGraph, sccResult);
        if (metrics != null) {
            // Можно обновить переданные метрики или использовать возвращаемые
            Metrics kahnMetrics = kahn.getMetrics();
            System.out.println(kahnMetrics.toReport());
        }
        return result;
    }

    /**
     * Prints topological sort results in a formatted way
     */
    public static void printTopologicalResults(TopologicalSortResult result, SCCResult sccResult) {
        System.out.println("=== Topological Sort Analysis ===");

        if (!result.isValid()) {
            System.out.println("ERROR: Graph contains cycles and cannot be topologically sorted!");
            return;
        }

        System.out.println("Valid topological order found!");
        System.out.println();

        // Print component order with details
        System.out.println("Topological order of components (SCCs):");
        List<Integer> componentOrder = result.getComponentOrder();
        for (int i = 0; i < componentOrder.size(); i++) {
            int compId = componentOrder.get(i);
            List<Integer> component = sccResult.getComponents().get(compId);
            System.out.println(i + 1 + ". Component " + compId + ": " + component + " (size: " + component.size() + ")");
        }

        System.out.println();

        // Print vertex order
        System.out.println("Derived topological order of original vertices:");
        List<Integer> vertexOrder = result.getVertexOrder();
        for (int i = 0; i < vertexOrder.size(); i++) {
            System.out.println(i + 1 + ". Vertex " + vertexOrder.get(i));
        }

        System.out.println();
        System.out.println("Total vertices in order: " + vertexOrder.size());
    }

    /**
     * Validates if the topological order respects all dependencies BETWEEN COMPONENTS
     * Note: We only validate edges between different SCCs, not within the same SCC
     */
    public static boolean validateTopologicalOrder(Graph originalGraph, List<Integer> vertexOrder, SCCResult sccResult) {
        // Create mapping from vertex to its position in order
        Map<Integer, Integer> position = new HashMap<>();
        for (int i = 0; i < vertexOrder.size(); i++) {
            position.put(vertexOrder.get(i), i);
        }

        // Check only edges between different components
        for (Edge edge : originalGraph.getEdges()) {
            int u = edge.getU();
            int v = edge.getV();

            // Only validate if vertices are in different components
            if (sccResult.getComponentId(u) != sccResult.getComponentId(v)) {
                if (position.get(u) > position.get(v)) {
                    System.out.println("VIOLATION: Edge " + u + " -> " + v +
                            " but order has " + u + " after " + v);
                    System.out.println("  Component " + u + ": " + sccResult.getComponentId(u));
                    System.out.println("  Component " + v + ": " + sccResult.getComponentId(v));
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Alternative validation that only checks condensation graph edges
     */
    public static boolean validateComponentOrder(Graph condensationGraph, List<Integer> componentOrder) {
        Map<Integer, Integer> compPosition = new HashMap<>();
        for (int i = 0; i < componentOrder.size(); i++) {
            compPosition.put(componentOrder.get(i), i);
        }

        for (Edge edge : condensationGraph.getEdges()) {
            int u = edge.getU();
            int v = edge.getV();

            if (compPosition.get(u) > compPosition.get(v)) {
                System.out.println("COMPONENT VIOLATION: Edge " + u + " -> " + v +
                        " but order has component " + u + " after " + v);
                return false;
            }
        }

        return true;
    }
}