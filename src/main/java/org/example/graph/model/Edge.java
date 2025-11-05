package org.example.graph.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Edge {
    @JsonProperty("u")
    private int u; // source vertex

    @JsonProperty("v")
    private int v; // target vertex

    @JsonProperty("w")
    private int w; // weight

    public Edge() {}

    public Edge(int u, int v, int w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }

    // Getters and setters
    public int getU() {
        return u;
    }

    public void setU(int u) {
        this.u = u;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    @Override
    public String toString() {
        return "Edge{" + u + " -> " + v + ", w=" + w + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Edge edge = (Edge) obj;
        return u == edge.u && v == edge.v && w == edge.w;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(u, v, w);
    }
}
