package graph;

import org.example.graph.models.Graph;
import org.example.graph.models.Edge;
import org.example.graph.scc.SCCFinder;
import org.example.graph.scc.SCCResult;
import org.example.graph.topo.TopologicalSort;
import org.example.graph.topo.TopologicalSortResult;
import org.example.graph.dagsp.PathFinder;
import org.example.graph.dagsp.PathResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Comprehensive tests for the complete graph analysis pipeline
 */
class ComprehensiveTest {

    @Test
    void testThreeVertexChain() {
        // Simple chain: 0->1->2
        Graph graph = new Graph(true, 3, List.of(
                new Edge(0, 1, 5),
                new Edge(1, 2, 3)
        ), 0, "edge");

        // SCC analysis
        SCCResult sccResult = SCCFinder.findStronglyConnectedComponents(graph);
        assertEquals(3, sccResult.getComponents().size()); // Each vertex is its own SCC
        assertEquals(3, sccResult.getCondensationGraph().getN());

        // Topological sort
        TopologicalSortResult topoResult = TopologicalSort.sortFromOriginal(graph, sccResult);
        assertTrue(topoResult.isValid());
        assertEquals(List.of(0, 1, 2), topoResult.getVertexOrder());

        // Path analysis
        PathResult shortest = PathFinder.findShortestPaths(graph, topoResult, 0);
        PathResult longest = PathFinder.findLongestPaths(graph, topoResult, 0);

        assertEquals(0, shortest.getDistances().get(0));
        assertEquals(5, shortest.getDistances().get(1));
        assertEquals(8, shortest.getDistances().get(2));

        assertEquals(0, longest.getDistances().get(0));
        assertEquals(5, longest.getDistances().get(1));
        assertEquals(8, longest.getDistances().get(2));
    }

    @Test
    void testTasksJsonGraph() {
        // Test with the original tasks.json structure
        Graph graph = new Graph(true, 8, List.of(
                new Edge(0, 1, 3),
                new Edge(1, 2, 2),
                new Edge(2, 3, 4),
                new Edge(3, 1, 1),  // Cycle: 1->2->3->1
                new Edge(4, 5, 2),
                new Edge(5, 6, 5),
                new Edge(6, 7, 1)
        ), 4, "edge");

        // SCC analysis
        SCCResult sccResult = SCCFinder.findStronglyConnectedComponents(graph);

        // Should have 6 SCCs: [1,2,3], [0], [4], [5], [6], [7]
        assertEquals(6, sccResult.getComponents().size());

        // Find the cycle component
        List<Integer> cycleComponent = sccResult.getComponents().stream()
                .filter(comp -> comp.size() == 3)
                .findFirst()
                .orElse(List.of());
        assertEquals(3, cycleComponent.size());
        assertTrue(cycleComponent.contains(1));
        assertTrue(cycleComponent.contains(2));
        assertTrue(cycleComponent.contains(3));

        // Topological sort should be valid
        TopologicalSortResult topoResult = TopologicalSort.sortFromOriginal(graph, sccResult);
        assertTrue(topoResult.isValid());

        // Path analysis from source 4
        PathResult shortest = PathFinder.findShortestPaths(graph, topoResult, 4);
        PathResult longest = PathFinder.findLongestPaths(graph, topoResult, 4);

        // From vertex 4, should reach 5,6,7 but not 0,1,2,3
        assertEquals(0, shortest.getDistances().get(4));
        assertEquals(2, shortest.getDistances().get(5));
        assertEquals(7, shortest.getDistances().get(6));
        assertEquals(8, shortest.getDistances().get(7));

        // Unreachable vertices should have MAX_VALUE
        assertEquals(Integer.MAX_VALUE, shortest.getDistances().get(0));
        assertEquals(Integer.MAX_VALUE, shortest.getDistances().get(1));
    }

    @Test
    void testSingleVertexGraph() {
        Graph graph = new Graph(true, 1, List.of(), 0, "edge");

        SCCResult sccResult = SCCFinder.findStronglyConnectedComponents(graph);
        assertEquals(1, sccResult.getComponents().size());
        assertEquals(1, sccResult.getComponents().get(0).size());
        assertEquals(0, sccResult.getComponents().get(0).get(0).intValue());

        TopologicalSortResult topoResult = TopologicalSort.sortFromOriginal(graph, sccResult);
        assertTrue(topoResult.isValid());
        assertEquals(1, topoResult.getVertexOrder().size());
        assertEquals(0, topoResult.getVertexOrder().get(0).intValue());

        PathResult shortest = PathFinder.findShortestPaths(graph, topoResult, 0);
        assertEquals(0, shortest.getDistances().get(0));
        assertEquals(0, shortest.getCriticalPathLength());
        assertEquals(1, shortest.getCriticalPath().size());
        assertEquals(0, shortest.getCriticalPath().get(0).intValue());
    }

    @Test
    void testDisconnectedGraph() {
        // Two disconnected components: 0->1 and 2->3
        Graph graph = new Graph(true, 4, List.of(
                new Edge(0, 1, 1),
                new Edge(2, 3, 1)
        ), 0, "edge");

        SCCResult sccResult = SCCFinder.findStronglyConnectedComponents(graph);
        assertEquals(4, sccResult.getComponents().size()); // All vertices are separate SCCs

        TopologicalSortResult topoResult = TopologicalSort.sortFromOriginal(graph, sccResult);
        assertTrue(topoResult.isValid());
        assertEquals(4, topoResult.getVertexOrder().size());

        PathResult shortest = PathFinder.findShortestPaths(graph, topoResult, 0);
        assertEquals(0, shortest.getDistances().get(0));
        assertEquals(1, shortest.getDistances().get(1));

        int INFINITY = Integer.MAX_VALUE;
        assertEquals(INFINITY, shortest.getDistances().get(2)); // Unreachable
        assertEquals(INFINITY, shortest.getDistances().get(3)); // Unreachable
    }

    @Test
    void testCompleteCycle() {
        // Complete cycle: 0->1->2->0
        Graph graph = new Graph(true, 3, List.of(
                new Edge(0, 1, 1),
                new Edge(1, 2, 1),
                new Edge(2, 0, 1)
        ), 0, "edge");

        SCCResult sccResult = SCCFinder.findStronglyConnectedComponents(graph);
        assertEquals(1, sccResult.getComponents().size()); // One big SCC
        assertEquals(3, sccResult.getComponents().get(0).size());

        TopologicalSortResult topoResult = TopologicalSort.sortFromOriginal(graph, sccResult);
        assertTrue(topoResult.isValid());
        assertEquals(3, topoResult.getVertexOrder().size());

        // In a cycle, all vertices should be reachable from source
        PathResult shortest = PathFinder.findShortestPaths(graph, topoResult, 0);
        assertTrue(shortest.getDistances().get(0) < Integer.MAX_VALUE);
        assertTrue(shortest.getDistances().get(1) < Integer.MAX_VALUE);
        assertTrue(shortest.getDistances().get(2) < Integer.MAX_VALUE);
    }

    @Test
    void testMultiplePaths() {
        // Graph with multiple paths to same vertex
        // 0->1->3 and 0->2->3
        Graph graph = new Graph(true, 4, List.of(
                new Edge(0, 1, 2),
                new Edge(0, 2, 5),
                new Edge(1, 3, 3),
                new Edge(2, 3, 1)
        ), 0, "edge");

        SCCResult sccResult = SCCFinder.findStronglyConnectedComponents(graph);
        TopologicalSortResult topoResult = TopologicalSort.sortFromOriginal(graph, sccResult);
        PathResult shortest = PathFinder.findShortestPaths(graph, topoResult, 0);

        // Shortest path to 3 should be 0->1->3 = 2+3=5
        // Alternative 0->2->3 = 5+1=6
        assertEquals(5, shortest.getDistances().get(3));

        PathResult longest = PathFinder.findLongestPaths(graph, topoResult, 0);
        // Longest path to 3 should be 0->2->3 = 6
        assertEquals(6, longest.getDistances().get(3));
    }

    @Test
    void testEmptyGraph() {
        Graph graph = new Graph(true, 0, List.of(), 0, "edge");

        // Should handle empty graph gracefully
        SCCResult sccResult = SCCFinder.findStronglyConnectedComponents(graph);
        assertNotNull(sccResult);
        assertEquals(0, sccResult.getComponents().size());

        assertNotNull(sccResult.getCondensationGraph());
        assertEquals(0, sccResult.getCondensationGraph().getN());
    }

    @Test
    void testSelfLoop() {
        // Graph with self-loop: 0->0
        Graph graph = new Graph(true, 2, List.of(
                new Edge(0, 0, 1),  // Self-loop
                new Edge(0, 1, 2)
        ), 0, "edge");

        SCCResult sccResult = SCCFinder.findStronglyConnectedComponents(graph);
        // Self-loop creates a SCC of size 1
        boolean foundSelfLoopComponent = sccResult.getComponents().stream()
                .anyMatch(comp -> comp.size() == 1 && comp.contains(0));
        assertTrue(foundSelfLoopComponent);

        TopologicalSortResult topoResult = TopologicalSort.sortFromOriginal(graph, sccResult);
        assertTrue(topoResult.isValid());
    }
}