package graph;

import org.example.graph.models.Graph;
import org.example.graph.models.Edge;
import org.example.graph.scc.SCCFinder;
import org.example.graph.scc.SCCResult;
import org.example.graph.topo.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class TopologicalSortTest {

    @Test
    void testSimpleDAG() {
        // Simple DAG: 0->1->2, 0->3
        Graph graph = new Graph(true, 4, List.of(
                new Edge(0, 1, 1),
                new Edge(1, 2, 1),
                new Edge(0, 3, 1)
        ), 0, "edge");

        SCCResult sccResult = SCCFinder.findStronglyConnectedComponents(graph);
        TopologicalSortResult topoResult = TopologicalSort.sortFromOriginal(graph, sccResult);

        assertTrue(topoResult.isValid());
        assertFalse(topoResult.hasCycle());
        assertEquals(4, topoResult.getVertexOrder().size());

        // Validate topological order - ДОБАВЬ SCCResult параметр
        assertTrue(TopologicalSort.validateTopologicalOrder(graph, topoResult.getVertexOrder(), sccResult));
    }

    @Test
    void testComplexDAG() {
        // More complex DAG
        Graph graph = new Graph(true, 6, List.of(
                new Edge(0, 1, 1),
                new Edge(0, 2, 1),
                new Edge(1, 3, 1),
                new Edge(2, 3, 1),
                new Edge(3, 4, 1),
                new Edge(3, 5, 1)
        ), 0, "edge");

        SCCResult sccResult = SCCFinder.findStronglyConnectedComponents(graph);
        TopologicalSortResult topoResult = TopologicalSort.sortFromOriginal(graph, sccResult);

        assertTrue(topoResult.isValid());
        assertFalse(topoResult.hasCycle());
        assertEquals(6, topoResult.getVertexOrder().size());

        // Validate topological order - ДОБАВЬ SCCResult параметр
        assertTrue(TopologicalSort.validateTopologicalOrder(graph, topoResult.getVertexOrder(), sccResult));
    }

    @Test
    void testGraphWithCycle() {
        // Graph with cycle, but condensation should be DAG
        Graph graph = new Graph(true, 5, List.of(
                new Edge(0, 1, 1),
                new Edge(1, 2, 1),
                new Edge(2, 0, 1), // Cycle 0-1-2
                new Edge(2, 3, 1),
                new Edge(3, 4, 1)
        ), 0, "edge");

        SCCResult sccResult = SCCFinder.findStronglyConnectedComponents(graph);
        TopologicalSortResult topoResult = TopologicalSort.sortFromOriginal(graph, sccResult);

        // Should still be valid since condensation graph is DAG
        assertTrue(topoResult.isValid());
        assertFalse(topoResult.hasCycle());

        // Use the updated validation that considers SCCs
        assertTrue(TopologicalSort.validateTopologicalOrder(graph, topoResult.getVertexOrder(), sccResult));

        // Also validate component order
        assertTrue(TopologicalSort.validateComponentOrder(sccResult.getCondensationGraph(), topoResult.getComponentOrder()));
    }

    @Test
    void testSingleComponent() {
        // Single vertex
        Graph graph = new Graph(true, 1, List.of(), 0, "edge");

        SCCResult sccResult = SCCFinder.findStronglyConnectedComponents(graph);
        TopologicalSortResult topoResult = TopologicalSort.sortFromOriginal(graph, sccResult);

        assertTrue(topoResult.isValid());
        assertEquals(1, topoResult.getVertexOrder().size());
        assertEquals(0, topoResult.getVertexOrder().get(0));

        // Validate topological order - ДОБАВЬ SCCResult параметр
        assertTrue(TopologicalSort.validateTopologicalOrder(graph, topoResult.getVertexOrder(), sccResult));
    }
}