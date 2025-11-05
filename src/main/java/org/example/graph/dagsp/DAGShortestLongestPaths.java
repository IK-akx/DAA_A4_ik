package org.example.graph.dagsp;

import org.example.graph.models.Graph;
import org.example.graph.models.Edge;
import org.example.graph.topo.TopologicalSortResult;

import java.util.*;

public class DAGShortestLongestPaths {
    private static final int INFINITY = Integer.MAX_VALUE;
    private static final int NEG_INFINITY = Integer.MIN_VALUE;

    // Computes shortest paths from source in a DAG using topological order
    public PathResult shortestPathsFromSource(Graph dag, TopologicalSortResult topoResult, int source) {
        if (dag == null || topoResult == null || !topoResult.isValid()) {
            throw new IllegalArgumentException("Invalid input: DAG must be valid and topologically sorted");
        }

        int n = dag.getN();
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> predecessors = new HashMap<>();

        // Initialize distances
        for (int i = 0; i < n; i++) {
            distances.put(i, INFINITY);
        }
        distances.put(source, 0);

        List<Integer> topologicalOrder = topoResult.getVertexOrder();

        // Process vertices in topological order
        for (int u : topologicalOrder) {
            if (distances.get(u) != INFINITY) {
                // Relax all outgoing edges from u
                for (Edge edge : dag.getOutgoingEdges(u)) {
                    int v = edge.getV();
                    int weight = edge.getW();

                    // Skip if weight would cause integer overflow
                    if (distances.get(u) > 0 && weight > INFINITY - distances.get(u)) {
                        continue;
                    }

                    int newDistance = distances.get(u) + weight;

                    if (newDistance < distances.get(v)) {
                        distances.put(v, newDistance);
                        predecessors.put(v, u);
                    }
                }
            }
        }

        // Find critical path (longest path from source)
        List<Integer> criticalPath = findCriticalPath(distances, predecessors, source);
        int criticalPathLength = 0;

        if (!criticalPath.isEmpty()) {
            int lastVertex = criticalPath.get(criticalPath.size() - 1);
            criticalPathLength = distances.get(lastVertex);
            // If last vertex is unreachable, set length to 0
            if (criticalPathLength == INFINITY) {
                criticalPathLength = 0;
            }
        }

        return new PathResult(distances, predecessors, criticalPath, criticalPathLength, source, true);
    }

    // Computes longest paths from source in a DAG using topological order
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

        // Find critical path (longest path from source)
        List<Integer> criticalPath = findCriticalPath(distances, predecessors, source);
        int criticalPathLength = criticalPath.isEmpty() ? 0 :
                distances.get(criticalPath.get(criticalPath.size() - 1));

        return new PathResult(distances, predecessors, criticalPath, criticalPathLength, source, false);
    }

    // Finds the critical path (longest path from source)
    private List<Integer> findCriticalPath(Map<Integer, Integer> distances,
                                           Map<Integer, Integer> predecessors, int source) {
        if (distances.isEmpty()) return List.of();

        // Find vertex with maximum distance (excluding unreachable vertices)
        int maxDistance = NEG_INFINITY;
        int target = -1;

        for (Map.Entry<Integer, Integer> entry : distances.entrySet()) {
            int vertex = entry.getKey();
            int distance = entry.getValue();

            // Skip unreachable vertices and the source itself
            if (distance != INFINITY && distance != NEG_INFINITY && vertex != source) {
                if (distance > maxDistance) {
                    maxDistance = distance;
                    target = vertex;
                }
            }
        }

        // If no reachable vertex found (only source), return source only
        if (target == -1) {
            return List.of(source);
        }

        // Reconstruct path from source to target
        List<Integer> path = reconstructPath(predecessors, source, target);

        // If path reconstruction failed, return at least the source
        return path.isEmpty() ? List.of(source) : path;
    }

    // Reconstructs path from source to target using predecessors
    private List<Integer> reconstructPath(Map<Integer, Integer> predecessors, int source, int target) {
        List<Integer> path = new ArrayList<>();

        // If target is the source, return just the source
        if (target == source) {
            path.add(source);
            return path;
        }

        // If target has no predecessor, no path exists
        if (!predecessors.containsKey(target)) {
            return path;
        }

        // Reconstruct path backwards from target to source
        Integer current = target;
        while (current != null) {
            path.add(0, current);
            current = predecessors.get(current);
            // Stop when we reach source or null
            if (current != null && current == source) {
                path.add(0, source);
                break;
            }
        }

        // Ensure the path starts from source
        if (!path.isEmpty() && path.get(0) != source) {
            // If we didn't reach source, the path is incomplete
            return new ArrayList<>();
        }

        return path;
    }

    //  Computes both shortest and longest paths in one pass
    public Map<String, PathResult> computeAllPaths(Graph dag, TopologicalSortResult topoResult, int source) {
        Map<String, PathResult> results = new HashMap<>();
        results.put("shortest", shortestPathsFromSource(dag, topoResult, source));
        results.put("longest", longestPathsFromSource(dag, topoResult, source));
        return results;
    }
}