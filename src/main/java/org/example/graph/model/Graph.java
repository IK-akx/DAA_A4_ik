package org.example.graph.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;


public class Graph {
    @JsonProperty("directed")
    private boolean directed;

    @JsonProperty("n")
    private int n; // number of vertices

    @JsonProperty("edges")
    private List<Edge> edges;

    @JsonProperty("source")
    private int source;

    @JsonProperty("weight_model")
    private String weightModel;

    private transient List<List<Edge>> adjacencyList;

    public Graph() {
        this.edges = new ArrayList<>();
    }

    public Graph(boolean directed, int n, List<Edge> edges, int source, String weightModel) {
        this.directed = directed;
        this.n = n;
        this.edges = edges != null ? edges : new ArrayList<>();
        this.source = source;
        this.weightModel = weightModel;
        buildAdjacencyList();
    }

    /**
     * Builds adjacency list representation for faster graph traversal
     */
    public void buildAdjacencyList() {
        this.adjacencyList = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adjacencyList.add(new ArrayList<>());
        }

        for (Edge edge : edges) {
            if (edge.getU() < n && edge.getV() < n) {
                adjacencyList.get(edge.getU()).add(edge);
                // If undirected, add reverse edge (though our graphs are directed)
                if (!directed) {
                    adjacencyList.get(edge.getV()).add(new Edge(edge.getV(), edge.getU(), edge.getW()));
                }
            }
        }
    }

    // Getters and setters
    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
        buildAdjacencyList();
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getWeightModel() {
        return weightModel;
    }

    @JsonProperty("weight_model")
    public void setWeightModel(String weightModel) {
        this.weightModel = weightModel;
    }

    /**
     * Gets adjacency list for efficient graph traversal
     */
    public List<List<Edge>> getAdjacencyList() {
        if (adjacencyList == null) {
            buildAdjacencyList();
        }
        return adjacencyList;
    }

    /**
     * Gets all outgoing edges from a vertex
     */
    public List<Edge> getOutgoingEdges(int vertex) {
        if (adjacencyList == null) {
            buildAdjacencyList();
        }
        return vertex < n ? adjacencyList.get(vertex) : new ArrayList<>();
    }

    /**
     * Validates graph structure
     */
    public boolean validate() {
        if (n <= 0) return false;
        if (source < 0 || source >= n) return false;
        for (Edge edge : edges) {
            if (edge.getU() < 0 || edge.getU() >= n ||
                    edge.getV() < 0 || edge.getV() >= n) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Graph{" +
                "directed=" + directed +
                ", n=" + n +
                ", edges=" + edges.size() +
                ", source=" + source +
                ", weightModel='" + weightModel + '\'' +
                '}';
    }
}
