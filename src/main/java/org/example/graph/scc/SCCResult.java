package org.example.graph.scc;

import org.example.graph.models.Graph;
import java.util.List;
import java.util.ArrayList;


public class SCCResult {
    private List<List<Integer>> components;
    private Graph condensationGraph;
    private int[] componentId; // componentId[i] = id of SCC containing vertex i

    public SCCResult(List<List<Integer>> components, Graph condensationGraph, int[] componentId) {
        this.components = components;
        this.condensationGraph = condensationGraph;
        this.componentId = componentId;
    }

    // Getters
    public List<List<Integer>> getComponents() {
        return components;
    }

    public Graph getCondensationGraph() {
        return condensationGraph;
    }

    public int[] getComponentId() {
        return componentId;
    }

    // Gets the component ID for a given vertex
    public int getComponentId(int vertex) {
        if (vertex < 0 || vertex >= componentId.length) {
            throw new IllegalArgumentException("Invalid vertex: " + vertex);
        }
        return componentId[vertex];
    }

    // Gets the size of each component
    public List<Integer> getComponentSizes() {
        List<Integer> sizes = new ArrayList<>();
        for (List<Integer> component : components) {
            sizes.add(component.size());
        }
        return sizes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SCC Result:\n");
        sb.append("Number of components: ").append(components.size()).append("\n");
        for (int i = 0; i < components.size(); i++) {
            sb.append("Component ").append(i).append(": ").append(components.get(i))
                    .append(" (size: ").append(components.get(i).size()).append(")\n");
        }
        return sb.toString();
    }
}
