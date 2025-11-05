package org.example.graph.dagsp;

import org.example.graph.metrics.Metrics;
import org.example.graph.models.Graph;
import org.example.graph.models.Edge;
import org.example.graph.topo.TopologicalSortResult;

import java.util.*;

public class DAGShortestLongestPaths {
    private static final int INFINITY = Integer.MAX_VALUE; // Avoid overflow
    private static final int NEG_INFINITY = Integer.MIN_VALUE;
    private Metrics metrics;


    public DAGShortestLongestPaths() {
        this.metrics = new Metrics("DAGShortestLongestPaths");
    }

    public Metrics getMetrics() {
        return metrics;
    }

    /**
     * Computes shortest paths from source in a DAG using topological order
     */
    public PathResult shortestPathsFromSource(Graph dag, TopologicalSortResult topoResult, int source) {
        metrics.startTimer();
        metrics.reset();

        if (dag == null || topoResult == null || !topoResult.isValid()) {
            throw new IllegalArgumentException("Invalid input: DAG must be valid and topologically sorted");
        }

        int n = dag.getN();
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> predecessors = new HashMap<>();

        // Initialize distances
        for (int i = 0; i < n; i++) {
            distances.put(i, INFINITY);
            metrics.incrementVerticesVisited(); // Count initialization
        }
        distances.put(source, 0);

        List<Integer> topologicalOrder = topoResult.getVertexOrder();

        // Process vertices in topological order
        for (int u : topologicalOrder) {
            metrics.incrementVerticesVisited(); // Count vertex processing

            if (distances.get(u) != INFINITY) {
                // Relax all outgoing edges from u
                for (Edge edge : dag.getOutgoingEdges(u)) {
                    metrics.incrementEdgesRelaxed(); // COUNT THIS METRIC!

                    int v = edge.getV();
                    int weight = edge.getW();

                    // Check for integer overflow
                    if (distances.get(u) > 0 && weight > INFINITY - distances.get(u)) {
                        continue; // Skip to avoid overflow
                    }

                    int newDistance = distances.get(u) + weight;

                    if (newDistance < distances.get(v)) {
                        distances.put(v, newDistance);
                        predecessors.put(v, u);
                    }
                }
            }
        }

        // Find the reachable vertex with maximum distance (critical path for shortest paths context)
        int maxDistance = 0;
        int criticalVertex = source;
        List<Integer> criticalPath = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : distances.entrySet()) {
            if (entry.getValue() != INFINITY && entry.getValue() > maxDistance) {
                maxDistance = entry.getValue();
                criticalVertex = entry.getKey();
            }
        }

        // Simple path reconstruction - just from source to critical vertex
        if (criticalVertex != source) {
            Integer current = criticalVertex;
            while (current != null && current != source) {
                criticalPath.add(0, current);
                current = predecessors.get(current);
                if (current == null) {
                    // If we lose the path, start over with just source
                    criticalPath.clear();
                    criticalPath.add(source);
                    break;
                }
            }
            if (!criticalPath.isEmpty() && criticalPath.get(0) != source) {
                criticalPath.add(0, source);
            }
        } else {
            criticalPath.add(source);
        }

        // Ensure critical path length is correct
        if (criticalPath.size() == 1 && criticalPath.get(0) == source) {
            maxDistance = 0;
        }

        metrics.stopTimer();
        return new PathResult(distances, predecessors, criticalPath, maxDistance, source, true);
    }

    /**
     * Computes longest paths from source in a DAG using topological order
     */
    public PathResult longestPathsFromSource(Graph dag, TopologicalSortResult topoResult, int source) {
        if (dag == null || topoResult == null || !topoResult.isValid()) {
            throw new IllegalArgumentException("Invalid input: DAG must be valid and topologically sorted");
        }

        int n = dag.getN();
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> predecessors = new HashMap<>();

        // Initialize distances to negative infinity
        for (int i = 0; i < n; i++) {
            distances.put(i, NEG_INFINITY);
        }
        distances.put(source, 0);

        List<Integer> topologicalOrder = topoResult.getVertexOrder();

        // Process vertices in topological order
        for (int u : topologicalOrder) {
            if (distances.get(u) != NEG_INFINITY) {
                // Relax all outgoing edges from u (maximizing)
                for (Edge edge : dag.getOutgoingEdges(u)) {
                    int v = edge.getV();
                    int weight = edge.getW();
                    int newDistance = distances.get(u) + weight;

                    if (newDistance > distances.get(v)) {
                        distances.put(v, newDistance);
                        predecessors.put(v, u);
                    }
                }
            }
        }

        // Find the vertex with maximum distance (critical path)
        int maxDistance = NEG_INFINITY;
        int criticalVertex = source;
        List<Integer> criticalPath = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : distances.entrySet()) {
            if (entry.getValue() != NEG_INFINITY && entry.getValue() > maxDistance) {
                maxDistance = entry.getValue();
                criticalVertex = entry.getKey();
            }
        }

        // Simple path reconstruction
        if (criticalVertex != source) {
            Integer current = criticalVertex;
            while (current != null && current != source) {
                criticalPath.add(0, current);
                current = predecessors.get(current);
            }
            criticalPath.add(0, source);
        } else {
            criticalPath.add(source);
        }

        // If no path found other than source, set distance to 0
        if (maxDistance == NEG_INFINITY) {
            maxDistance = 0;
        }

        return new PathResult(distances, predecessors, criticalPath, maxDistance, source, false);
    }

    /**
     * Computes both shortest and longest paths in one pass
     */
    public Map<String, PathResult> computeAllPaths(Graph dag, TopologicalSortResult topoResult, int source) {
        Map<String, PathResult> results = new HashMap<>();
        results.put("shortest", shortestPathsFromSource(dag, topoResult, source));
        results.put("longest", longestPathsFromSource(dag, topoResult, source));
        return results;
    }
}