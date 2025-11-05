package org.example.graph.metrics;

public class Metrics {
    private long startTime;
    private long endTime;
    private int verticesVisited;
    private int edgesRelaxed;
    private int stackOperations;
    private int queueOperations;
    private String algorithmName;

    public Metrics(String algorithmName) {
        this.algorithmName = algorithmName;
        reset();
    }

    public void reset() {
        startTime = 0;
        endTime = 0;
        verticesVisited = 0;
        edgesRelaxed = 0;
        stackOperations = 0;
        queueOperations = 0;
    }

    public void startTimer() {
        startTime = System.nanoTime();
    }

    public void stopTimer() {
        endTime = System.nanoTime();
    }

    public long getElapsedTimeNanos() {
        return endTime - startTime;
    }

    public double getElapsedTimeMillis() {
        return getElapsedTimeNanos() / 1_000_000.0;
    }

    // Increment methods
    public void incrementVerticesVisited() {
        verticesVisited++;
    }

    public void incrementVerticesVisited(int count) {
        verticesVisited += count;
    }

    public void incrementEdgesRelaxed() {
        edgesRelaxed++;
    }

    public void incrementEdgesRelaxed(int count) {
        edgesRelaxed += count;
    }

    public void incrementStackOperations() {
        stackOperations++;
    }

    public void incrementQueueOperations() {
        queueOperations++;
    }

    // Getters
    public int getVerticesVisited() {
        return verticesVisited;
    }

    public int getEdgesRelaxed() {
        return edgesRelaxed;
    }

    public int getStackOperations() {
        return stackOperations;
    }

    public int getQueueOperations() {
        return queueOperations;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    @Override
    public String toString() {
        return String.format(
                "Metrics[%s]: Time=%.3fms, Vertices=%d, Edges=%d, StackOps=%d, QueueOps=%d",
                algorithmName, getElapsedTimeMillis(), verticesVisited, edgesRelaxed,
                stackOperations, queueOperations
        );
    }

    public String toReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(algorithmName).append(" Metrics ===\n");
        sb.append("Execution Time: ").append(String.format("%.3f", getElapsedTimeMillis())).append(" ms\n");
        sb.append("Vertices Visited: ").append(verticesVisited).append("\n");
        sb.append("Edges Relaxed: ").append(edgesRelaxed).append("\n");
        sb.append("Stack Operations: ").append(stackOperations).append("\n");
        sb.append("Queue Operations: ").append(queueOperations).append("\n");
        return sb.toString();
    }
}