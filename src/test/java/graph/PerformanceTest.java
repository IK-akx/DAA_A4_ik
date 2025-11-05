package graph;


import org.example.graph.models.Graph;
import org.example.graph.scc.SCCFinder;
import org.example.graph.topo.TopologicalSort;
import org.example.graph.dagsp.PathFinder;
import org.example.graph.util.GraphLoader;
import org.junit.jupiter.api.Test;
import java.io.File;


class PerformanceTest {

    @Test
    void testAllDatasets() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            System.out.println("Data directory not found, skipping performance tests");
            return;
        }

        File[] datasetFiles = dataDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (datasetFiles == null || datasetFiles.length == 0) {
            System.out.println("No datasets found, skipping performance tests");
            return;
        }

        System.out.println("\n=== Performance Test Results ===");
        System.out.printf("%-20s %-8s %-8s %-12s %-12s %-12s%n",
                "Dataset", "Vertices", "Edges", "SCC Time(ms)", "Topo Time(ms)", "Path Time(ms)");
        System.out.println("--------------------------------------------------------------------------------");

        for (File dataset : datasetFiles) {
            try {
                Graph graph = GraphLoader.loadFromJson(dataset.getPath());

                long startTime, endTime;

                // SCC timing
                startTime = System.nanoTime();
                var sccResult = SCCFinder.findStronglyConnectedComponents(graph);
                endTime = System.nanoTime();
                double sccTime = (endTime - startTime) / 1_000_000.0;

                // Topological sort timing
                startTime = System.nanoTime();
                var topoResult = TopologicalSort.sortFromOriginal(graph, sccResult);
                endTime = System.nanoTime();
                double topoTime = (endTime - startTime) / 1_000_000.0;

                // Path finding timing
                startTime = System.nanoTime();
                PathFinder.findAllPaths(graph, topoResult, graph.getSource());
                endTime = System.nanoTime();
                double pathTime = (endTime - startTime) / 1_000_000.0;

                System.out.printf("%-20s %-8d %-8d %-12.3f %-12.3f %-12.3f%n",
                        dataset.getName().replace(".json", ""),
                        graph.getN(),
                        graph.getEdges().size(),
                        sccTime,
                        topoTime,
                        pathTime);

            } catch (Exception e) {
                System.out.printf("%-20s %-46s%n",
                        dataset.getName(), "ERROR: " + e.getMessage());
            }
        }
    }
}