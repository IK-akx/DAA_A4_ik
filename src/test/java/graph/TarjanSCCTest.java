package graph;

import org.example.graph.models.Graph;
import org.example.graph.models.Edge;
import org.example.graph.scc.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Arrays;

class TarjanSCCTest {

    @Test
    void testSimpleCycle() {
        // Graph: 0->1->2->0 (one cycle)
        Graph graph = new Graph(true, 3, List.of(
                new Edge(0, 1, 1),
                new Edge(1, 2, 1),
                new Edge(2, 0, 1)
        ), 0, "edge");

        SCCResult result = new TarjanSCC().findSCCs(graph);
        List<List<Integer>> components = result.getComponents();

        assertEquals(1, components.size());
        assertTrue(components.get(0).containsAll(List.of(0, 1, 2)));
        assertEquals(3, components.get(0).size());
    }

    @Test
    void testMultipleSCCs() {
        // Graph: 0->1->2->0, 3->4, 4->3, 5 (isolated)
        Graph graph = new Graph(true, 6, List.of(
                new Edge(0, 1, 1),
                new Edge(1, 2, 1),
                new Edge(2, 0, 1),
                new Edge(3, 4, 1),
                new Edge(4, 3, 1)
        ), 0, "edge");

        SCCResult result = new TarjanSCC().findSCCs(graph);
        List<List<Integer>> components = result.getComponents();

        assertEquals(3, components.size());

        // Check that we have components of correct sizes
        boolean foundCycle3 = false;
        boolean foundCycle2 = false;
        boolean foundSingle = false;

        for (List<Integer> component : components) {
            if (component.size() == 3) foundCycle3 = true;
            if (component.size() == 2) foundCycle2 = true;
            if (component.size() == 1) foundSingle = true;
        }

        assertTrue(foundCycle3);
        assertTrue(foundCycle2);
        assertTrue(foundSingle);
    }

    @Test
    void testDAG() {
        // Pure DAG: 0->1->2, 0->3
        Graph graph = new Graph(true, 4, List.of(
                new Edge(0, 1, 1),
                new Edge(1, 2, 1),
                new Edge(0, 3, 1)
        ), 0, "edge");

        SCCResult result = new TarjanSCC().findSCCs(graph);
        List<List<Integer>> components = result.getComponents();

        // In a DAG, each vertex is its own SCC
        assertEquals(4, components.size());
        assertEquals(4, result.getCondensationGraph().getN());
    }

    @Test
    void testCondensationGraph() {
        // Graph with two cycles connected by an edge
        Graph graph = new Graph(true, 5, List.of(
                new Edge(0, 1, 1),
                new Edge(1, 0, 1), // Cycle 1: 0-1
                new Edge(2, 3, 1),
                new Edge(3, 2, 1), // Cycle 2: 2-3
                new Edge(1, 2, 1), // Connection between cycles
                new Edge(4, 4, 1)  // Self-loop
        ), 0, "edge");

        SCCResult result = new TarjanSCC().findSCCs(graph);
        Graph condensation = result.getCondensationGraph();

        // Should have 3 components: [0,1], [2,3], [4]
        assertEquals(3, condensation.getN());
        // Should have edges between components
        assertTrue(condensation.getEdges().size() > 0);
    }
}