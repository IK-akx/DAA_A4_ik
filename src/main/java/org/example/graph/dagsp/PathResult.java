package org.example.graph.dagsp;

import java.util.List;
import java.util.Map;

public class PathResult {
    private Map<Integer, Integer> distances;
    private Map<Integer, Integer> predecessors;
    private List<Integer> criticalPath;
    private int criticalPathLength;
    private int source;
    private boolean isShortestPath;

    public PathResult(Map<Integer, Integer> distances, Map<Integer, Integer> predecessors,
                      List<Integer> criticalPath, int criticalPathLength, int source, boolean isShortestPath) {
        this.distances = distances;
        this.predecessors = predecessors;
        this.criticalPath = criticalPath;
        this.criticalPathLength = criticalPathLength;
        this.source = source;
        this.isShortestPath = isShortestPath;
    }

    // Getters
    public Map<Integer, Integer> getDistances() {
        return distances;
    }

    public Map<Integer, Integer> getPredecessors() {
        return predecessors;
    }

    public List<Integer> getCriticalPath() {
        return criticalPath;
    }

    public int getCriticalPathLength() {
        return criticalPathLength;
    }

    public int getSource() {
        return source;
    }

    public boolean isShortestPath() {
        return isShortestPath;
    }

    /**
     * Reconstructs path from source to target vertex
     */
    public List<Integer> reconstructPath(int target) {
        if (predecessors == null || !predecessors.containsKey(target)) {
            return List.of(); // No path exists
        }

        java.util.List<Integer> path = new java.util.ArrayList<>();
        Integer current = target;

        // Reconstruct path backwards
        while (current != null) {
            path.add(0, current);
            current = predecessors.get(current);
            if (current != null && current == source) {
                path.add(0, source);
                break;
            }
        }

        // Ensure path starts from source
        if (!path.isEmpty() && path.get(0) != source) {
            path.add(0, source);
        }

        return path;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isShortestPath) {
            sb.append("Shortest Paths from source ").append(source).append(":\n");
        } else {
            sb.append("Longest Paths from source ").append(source).append(":\n");
        }

        for (Map.Entry<Integer, Integer> entry : distances.entrySet()) {
            int vertex = entry.getKey();
            int distance = entry.getValue();
            if (distance == Integer.MAX_VALUE) {
                sb.append("  Vertex ").append(vertex).append(": UNREACHABLE\n");
            } else if (distance == Integer.MIN_VALUE) {
                sb.append("  Vertex ").append(vertex).append(": NEGATIVE_INFINITY\n");
            } else {
                sb.append("  Vertex ").append(vertex).append(": ").append(distance);
                if (predecessors != null && predecessors.containsKey(vertex)) {
                    List<Integer> path = reconstructPath(vertex);
                    sb.append(" (Path: ").append(path).append(")");
                }
                sb.append("\n");
            }
        }

        if (criticalPath != null && !criticalPath.isEmpty()) {
            sb.append("\nCritical Path (Longest): ").append(criticalPath)
                    .append("\nCritical Path Length: ").append(criticalPathLength).append("\n");
        }

        return sb.toString();
    }
}