package org.example.graph.topo;


import org.example.graph.models.Graph;
import org.example.graph.models.Edge;
import org.example.graph.scc.SCCResult;

import java.util.*;

public class KahnTopologicalSort {

    // Performs topological sort on the condensation graph
    public TopologicalSortResult sort(Graph condensationGraph, SCCResult sccResult) {
        int n = condensationGraph.getN();

        // Calculate in-degrees for each component
        int[] inDegree = new int[n];
        List<List<Integer>> adjacencyList = buildComponentAdjacencyList(condensationGraph, n);

        // Initialize in-degrees
        for (int u = 0; u < n; u++) {
            for (int v : adjacencyList.get(u)) {
                inDegree[v]++;
            }
        }

        // Queue for nodes with zero in-degree
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        List<Integer> componentOrder = new ArrayList<>();
        int visitedCount = 0;

        // Process nodes
        while (!queue.isEmpty()) {
            int u = queue.poll();
            componentOrder.add(u);
            visitedCount++;

            // Decrease in-degree of neighbors
            for (int v : adjacencyList.get(u)) {
                inDegree[v]--;
                if (inDegree[v] == 0) {
                    queue.offer(v);
                }
            }
        }

        // Check for cycles (should not happen in condensation graph, but safety check)
        boolean hasCycle = (visitedCount != n);

        // Build vertex order from component order
        List<Integer> vertexOrder = buildVertexOrder(componentOrder, sccResult);

        return new TopologicalSortResult(componentOrder, vertexOrder, hasCycle);
    }

    // Builds adjacency list for components
    private List<List<Integer>> buildComponentAdjacencyList(Graph condensationGraph, int n) {
        List<List<Integer>> adjList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adjList.add(new ArrayList<>());
        }

        for (Edge edge : condensationGraph.getEdges()) {
            adjList.get(edge.getU()).add(edge.getV());
        }

        return adjList;
    }

    // Builds order of original vertices from component order
    private List<Integer> buildVertexOrder(List<Integer> componentOrder, SCCResult sccResult) {
        List<Integer> vertexOrder = new ArrayList<>();
        List<List<Integer>> components = sccResult.getComponents();

        // For each component in topological order, add its vertices
        for (int compId : componentOrder) {
            List<Integer> componentVertices = components.get(compId);
            // Sort vertices within component for consistent ordering
            Collections.sort(componentVertices);
            vertexOrder.addAll(componentVertices);
        }

        return vertexOrder;
    }

    // Alternative method: topological sort using original graph and SCC result
    public TopologicalSortResult sortFromSCC(Graph originalGraph, SCCResult sccResult) {
        return sort(sccResult.getCondensationGraph(), sccResult);
    }
}