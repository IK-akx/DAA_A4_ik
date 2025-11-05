package graph;

import org.example.graph.model.Graph;
import org.example.graph.model.Edge;
import org.example.graph.util.GraphLoader;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class GraphLoadingTest {

    @Test
    void testGraphStructure() {
        Graph graph = new Graph(true, 8, List.of(
                new Edge(0, 1, 3),
                new Edge(1, 2, 2),
                new Edge(2, 3, 4),
                new Edge(3, 1, 1),
                new Edge(4, 5, 2),
                new Edge(5, 6, 5),
                new Edge(6, 7, 1)
        ), 4, "edge");

        assertTrue(graph.isDirected());
        assertEquals(8, graph.getN());
        assertEquals(7, graph.getEdges().size());
        assertEquals(4, graph.getSource());
        assertEquals("edge", graph.getWeightModel());
        assertTrue(graph.validate());
    }

    @Test
    void testEdgeOperations() {
        Edge edge = new Edge(0, 1, 5);
        assertEquals(0, edge.getU());
        assertEquals(1, edge.getV());
        assertEquals(5, edge.getW());

        Edge sameEdge = new Edge(0, 1, 5);
        assertEquals(edge, sameEdge);
    }

    @Test
    void testAdjacencyList() {
        Graph graph = new Graph(true, 3, List.of(
                new Edge(0, 1, 1),
                new Edge(0, 2, 2),
                new Edge(1, 2, 3)
        ), 0, "edge");

        List<Edge> outgoingFrom0 = graph.getOutgoingEdges(0);
        assertEquals(2, outgoingFrom0.size());

        List<Edge> outgoingFrom2 = graph.getOutgoingEdges(2);
        assertTrue(outgoingFrom2.isEmpty());
    }
}