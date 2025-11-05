package graph;

import org.example.graph.models.Graph;
import org.example.graph.models.Edge;
import org.example.graph.scc.SCCFinder;
import org.example.graph.scc.SCCResult;
import org.example.graph.topo.TopologicalSort;
import org.example.graph.topo.TopologicalSortResult;
import org.example.graph.dagsp.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class DAGPathsTest {

    @Test
    void testShortestPathSimpleDAG() {
        // Simple DAG: 0->1(2), 0->2(5), 1->3(1), 2->3(2)
        Graph graph = new Graph(true, 4, List.of(
                new Edge(0, 1, 2),
                new Edge(0, 2, 5),
                new Edge(1, 3, 1),
                new Edge(2, 3, 2)
        ), 0, "edge");

        SCCResult sccResult = SCCFinder.findStronglyConnectedComponents(graph);
        TopologicalSortResult topoResult = TopologicalSort.sortFromOriginal(graph, sccResult);
        PathResult result = PathFinder.findShortestPaths(graph, topoResult, 0);

        assertTrue(result.isShortestPath());
        assertEquals(0, result.getDistances().get(0));
        assertEquals(2, result.getDistances().get(1));
        assertEquals(5, result.getDistances().get(2));
        assertEquals(3, result.getDistances().get(3)); // 0->1->3 = 2+1=3

        List<Integer> pathTo3 = result.reconstructPath(3);
        assertTrue(pathTo3.equals(List.of(0, 1, 3)) || pathTo3.equals(List.of(0, 2, 3)));
    }

    @Test
    void testLongestPathSimpleDAG() {
        // Simple DAG: 0->1(2), 0->2(5), 1->3(1), 2->3(2)
        Graph graph = new Graph(true, 4, List.of(
                new Edge(0, 1, 2),
                new Edge(0, 2, 5),
                new Edge(1, 3, 1),
                new Edge(2, 3, 2)
        ), 0, "edge");

        SCCResult sccResult = SCCFinder.findStronglyConnectedComponents(graph);
        TopologicalSortResult topoResult = TopologicalSort.sortFromOriginal(graph, sccResult);
        PathResult result = PathFinder.findLongestPaths(graph, topoResult, 0);

        assertFalse(result.isShortestPath());
        assertEquals(0, result.getDistances().get(0));
        assertEquals(2, result.getDistances().get(1));
        assertEquals(5, result.getDistances().get(2));
        assertEquals(7, result.getDistances().get(3)); // 0->2->3 = 5+2=7

        assertEquals(7, result.getCriticalPathLength());
        assertEquals(List.of(0, 2, 3), result.getCriticalPath());
    }

    @Test
    void testUnreachableVertices() {
        // Graph with unreachable vertices: 0->1, 2->3
        Graph graph = new Graph(true, 4, List.of(
                new Edge(0, 1, 1),
                new Edge(2, 3, 1)
        ), 0, "edge");

        SCCResult sccResult = SCCFinder.findStronglyConnectedComponents(graph);
        TopologicalSortResult topoResult = TopologicalSort.sortFromOriginal(graph, sccResult);
        PathResult result = PathFinder.findShortestPaths(graph, topoResult, 0);

        assertEquals(0, result.getDistances().get(0));
        assertEquals(1, result.getDistances().get(1));

        int INFINITY = Integer.MAX_VALUE;
        assertEquals(INFINITY, result.getDistances().get(2));
        assertEquals(INFINITY, result.getDistances().get(3));
    }

    @Test
    void testSingleVertex() {
        // Single vertex graph
        Graph graph = new Graph(true, 1, List.of(), 0, "edge");

        SCCResult sccResult = SCCFinder.findStronglyConnectedComponents(graph);
        TopologicalSortResult topoResult = TopologicalSort.sortFromOriginal(graph, sccResult);
        PathResult result = PathFinder.findShortestPaths(graph, topoResult, 0);

        assertEquals(0, result.getDistances().get(0));
        assertEquals(List.of(0), result.getCriticalPath());
        assertEquals(0, result.getCriticalPathLength());
    }
}
