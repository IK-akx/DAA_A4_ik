package org.example.graph.topo;

import java.util.List;

public class TopologicalSortResult {
    private List<Integer> componentOrder;
    private List<Integer> vertexOrder;
    private boolean hasCycle;

    public TopologicalSortResult(List<Integer> componentOrder, List<Integer> vertexOrder, boolean hasCycle) {
        this.componentOrder = componentOrder;
        this.vertexOrder = vertexOrder;
        this.hasCycle = hasCycle;
    }

    // Getters
    public List<Integer> getComponentOrder() {
        return componentOrder;
    }

    public List<Integer> getVertexOrder() {
        return vertexOrder;
    }

    public boolean hasCycle() {
        return hasCycle;
    }

    public boolean isValid() {
        return !hasCycle && componentOrder != null && vertexOrder != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Topological Sort Result:\n");
        sb.append("Has cycle: ").append(hasCycle).append("\n");

        if (isValid()) {
            sb.append("Component order: ").append(componentOrder).append("\n");
            sb.append("Vertex order: ").append(vertexOrder).append("\n");
        }

        return sb.toString();
    }
}
