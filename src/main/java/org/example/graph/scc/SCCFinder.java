package org.example.graph.scc;

import org.example.graph.models.Graph;

import java.util.List;


public class SCCFinder {

    //  Finds SCCs using Tarjan's algorithm
    public static SCCResult findStronglyConnectedComponents(Graph graph) {
        TarjanSCC tarjan = new TarjanSCC();
        return tarjan.findSCCs(graph);
    }

    // Prints SCC results in a formatted way
    public static void printSCCResults(SCCResult result) {
        System.out.println("=== Strongly Connected Components Analysis ===");
        System.out.println("Number of SCCs: " + result.getComponents().size());
        System.out.println();

        System.out.println("Components and their sizes:");
        for (int i = 0; i < result.getComponents().size(); i++) {
            List<Integer> component = result.getComponents().get(i);
            System.out.println("SCC " + i + ": " + component + " (size: " + component.size() + ")");
        }

        System.out.println();
        System.out.println("Condensation Graph Info:");
        Graph condensation = result.getCondensationGraph();
        System.out.println("Vertices: " + condensation.getN());
        System.out.println("Edges: " + condensation.getEdges().size());
        System.out.println("Is DAG: " + (condensation.getEdges().size() > 0 ? "Yes" : "Yes (trivial)"));
    }
}
