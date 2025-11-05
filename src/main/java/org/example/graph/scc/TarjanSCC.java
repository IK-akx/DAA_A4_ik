package org.example.graph.scc;

import org.example.graph.models.Graph;
import org.example.graph.models.Edge;
import java.util.*;

public class TarjanSCC {
    private int index;
    private int[] indices;
    private int[] lowlinks;
    private boolean[] onStack;
    private Stack<Integer> stack;
    private List<List<Integer>> components;
    private Graph graph;

    public TarjanSCC() {}

    //Finds all strongly connected components in the graph

    public SCCResult findSCCs(Graph graph) {
        this.graph = graph;
        int n = graph.getN();

        // Initialize arrays
        indices = new int[n];
        lowlinks = new int[n];
        onStack = new boolean[n];
        stack = new Stack<>();
        components = new ArrayList<>();

        // Initialize arrays with -1 (unvisited)
        Arrays.fill(indices, -1);

        index = 0;

        // Perform DFS for each unvisited vertex
        for (int i = 0; i < n; i++) {
            if (indices[i] == -1) {
                strongConnect(i);
            }
        }

        // Build component ID mapping
        int[] componentId = buildComponentIdMapping(components, n);

        // Build condensation graph
        Graph condensationGraph = buildCondensationGraph(graph, components, componentId);

        return new SCCResult(components, condensationGraph, componentId);
    }

    // DFS method for Tarjan's algorithm
    private void strongConnect(int vertex) {
        indices[vertex] = index;
        lowlinks[vertex] = index;
        index++;
        stack.push(vertex);
        onStack[vertex] = true;

        // Consider all outgoing edges
        for (Edge edge : graph.getOutgoingEdges(vertex)) {
            int neighbor = edge.getV();

            if (indices[neighbor] == -1) {
                // Neighbor not visited, recurse
                strongConnect(neighbor);
                lowlinks[vertex] = Math.min(lowlinks[vertex], lowlinks[neighbor]);
            } else if (onStack[neighbor]) {
                // Neighbor is on stack, update lowlink
                lowlinks[vertex] = Math.min(lowlinks[vertex], indices[neighbor]);
            }
        }

        // If vertex is root node, pop stack and form SCC
        if (lowlinks[vertex] == indices[vertex]) {
            List<Integer> component = new ArrayList<>();
            int poppedVertex;

            do {
                poppedVertex = stack.pop();
                onStack[poppedVertex] = false;
                component.add(poppedVertex);
            } while (poppedVertex != vertex);

            components.add(component);
        }
    }

    //  Builds a mapping from vertex to its component ID
    private int[] buildComponentIdMapping(List<List<Integer>> components, int n) {
        int[] componentId = new int[n];
        Arrays.fill(componentId, -1);

        for (int compId = 0; compId < components.size(); compId++) {
            for (int vertex : components.get(compId)) {
                componentId[vertex] = compId;
            }
        }

        return componentId;
    }

    // Builds the condensation graph (DAG of components)
    private Graph buildCondensationGraph(Graph originalGraph, List<List<Integer>> components, int[] componentId) {
        int numComponents = components.size();
        List<Edge> condensationEdges = new ArrayList<>();
        Set<String> edgeSet = new HashSet<>(); // To avoid duplicate edges

        // For each original edge, add edge between components if they are different
        for (Edge originalEdge : originalGraph.getEdges()) {
            int u = originalEdge.getU();
            int v = originalEdge.getV();
            int compU = componentId[u];
            int compV = componentId[v];

            // Only add edge if it goes between different components
            if (compU != compV) {
                String edgeKey = compU + "->" + compV;
                if (!edgeSet.contains(edgeKey)) {
                    condensationEdges.add(new Edge(compU, compV, originalEdge.getW()));
                    edgeSet.add(edgeKey);
                }
            }
        }

        // Create new graph for condensation
        return new Graph(
                true, // directed
                numComponents, // number of vertices = number of components
                condensationEdges,
                componentId[originalGraph.getSource()], // map source to its component
                originalGraph.getWeightModel()
        );
    }
}